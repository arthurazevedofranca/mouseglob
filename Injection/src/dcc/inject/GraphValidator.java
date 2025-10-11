package dcc.inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * Builds and validates a dependency graph for the custom DI framework.
 * Provides cycle detection, missing binding reporting, and a human-readable wiring report.
 */
public final class GraphValidator {
    private static final Logger log = LoggerFactory.getLogger(GraphValidator.class);

    private GraphValidator() {}

    public static final class ValidationResult {
        public final boolean ok;
        public final List<List<Class<?>>> cycles;
        public final List<String> missingBindings;
        public final String report;

        public ValidationResult(boolean ok, List<List<Class<?>>> cycles, List<String> missingBindings, String report) {
            this.ok = ok;
            this.cycles = cycles;
            this.missingBindings = missingBindings;
            this.report = report;
        }
    }

    public static ValidationResult validate(Indexer indexer, Context context) {
        Graph<Class<?>> fullGraph = buildGraph(indexer);
        Graph<Class<?>> constructorGraph = buildGraph(indexer, InjectionUtils::getRequiredConstructorDependencies);
        List<List<Class<?>>> cycles = detectCycles(constructorGraph);
        List<String> missing = findMissingBindings(indexer, fullGraph);
        String report = buildReport(fullGraph, context, cycles, missing);
        boolean ok = cycles.isEmpty() && missing.isEmpty();
        if (ok) {
            log.info("DI validation OK. Nodes: {} Edges: {}", fullGraph.getNodes().size(), fullGraph.getEdges().stream().mapToInt(Set::size).sum());
        } else {
            log.warn("DI validation FAILED. cycles={}, missing={} (see report)", cycles.size(), missing.size());
        }
        return new ValidationResult(ok, cycles, missing, report);
    }

    public static Graph<Class<?>> buildGraph(Indexer indexer) {
        return buildGraph(indexer, InjectionUtils::getRequiredDependencies);
    }

    public static Graph<Class<?>> buildGraph(Indexer indexer, Function<Class<?>, Set<Class<?>>> dependencyResolver) {
        Graph<Class<?>> g = new Graph<>();
        Set<Class<?>> nodes = new LinkedHashSet<>(indexer.getClassesToInstantiate());
        for (Class<?> n : nodes) {
            g.addNode(n);
        }
        for (Class<?> n : nodes) {
            for (Class<?> dep : dependencyResolver.apply(n)) {
                g.addEdge(n, dep);
                if (!g.hasNode(dep)) g.addNode(dep);
            }
        }
        return g;
    }

    private static List<List<Class<?>>> detectCycles(Graph<Class<?>> g) {
        List<List<Class<?>>> cycles = new ArrayList<>();
        // DFS with recursion stack
        Map<Class<?>, Integer> color = new HashMap<>(); // 0=white,1=gray,2=black
        Deque<Class<?>> stack = new ArrayDeque<>();
        for (Class<?> node : g.getNodes()) color.put(node, 0);
        for (Class<?> node : g.getNodes()) {
            if (color.get(node) == 0) dfs(node, g, color, stack, cycles);
        }
        return cycles;
    }

    private static void dfs(Class<?> u, Graph<Class<?>> g, Map<Class<?>, Integer> color, Deque<Class<?>> stack, List<List<Class<?>>> cycles) {
        color.put(u, 1); // gray
        stack.push(u);
        for (Class<?> v : g.getOutgoing(u)) {
            Integer c = color.get(v);
            if (c == null) continue;
            if (c == 0) {
                dfs(v, g, color, stack, cycles);
            } else if (c == 1) { // back-edge => cycle
                // build cycle from v to u on stack
                List<Class<?>> cycle = new ArrayList<>();
                for (Class<?> it : stack) {
                    cycle.add(it);
                    if (it.equals(v)) break;
                }
                Collections.reverse(cycle);
                cycles.add(cycle);
            }
        }
        stack.pop();
        color.put(u, 2); // black
    }

    private static List<String> findMissingBindings(Indexer indexer, Graph<Class<?>> g) {
        List<String> missing = new ArrayList<>();
        Set<Class<?>> provisioned = new LinkedHashSet<>(indexer.getClassesToInstantiate());
        for (Class<?> n : g.getNodes()) {
            for (Class<?> dep : g.getOutgoing(n)) {
                if (!provisioned.contains(dep)) {
                    if (dep.isInterface() || Modifier.isAbstract(dep.getModifiers())) {
                        missing.add("No binding/implementation found for " + dep.getName() + " required by " + n.getName());
                    } else {
                        // For concretes, we assume they can be instantiated reflectively; no missing binding.
                    }
                }
            }
        }
        return missing;
    }

    private static String buildReport(Graph<Class<?>> g, Context context, List<List<Class<?>>> cycles, List<String> missing) {
        StringBuilder sb = new StringBuilder();
        sb.append("[DI Wiring Report]\n");
        sb.append("Nodes: ").append(g.getNodes().size()).append("\n");
        int edges = 0; for (Set<Class<?>> e : g.getEdges()) edges += e.size();
        sb.append("Edges: ").append(edges).append("\n\n");
        List<Class<?>> nodes = new ArrayList<>(g.getNodes());
        nodes.sort(Comparator.comparing(Class::getName));
        for (Class<?> n : nodes) {
            sb.append(n.getName());
            Object inst = getExisting(context, n);
            if (inst != null) sb.append(" [existing instance]");
            sb.append("\n");
            List<Class<?>> deps = new ArrayList<>(g.getOutgoing(n));
            deps.sort(Comparator.comparing(Class::getName));
            for (Class<?> d : deps) {
                sb.append("  -> ").append(d.getName());
                if (!g.getNodes().contains(d)) sb.append(" [external]");
                sb.append("\n");
            }
        }
        if (!cycles.isEmpty()) {
            sb.append("\nCycles detected in constructor dependency graph (" + cycles.size() + "):\n");
            int i = 1;
            for (List<Class<?>> cyc : cycles) {
                sb.append("  ").append(i++).append(": ");
                for (int j = 0; j < cyc.size(); j++) {
                    sb.append(cyc.get(j).getName());
                    if (j < cyc.size() - 1) sb.append(" -> ");
                }
                sb.append("\n");
            }
        }
        if (!missing.isEmpty()) {
            sb.append("\nMissing bindings (" + missing.size() + "):\n");
            for (String m : missing) sb.append("  - ").append(m).append("\n");
        }
        return sb.toString();
    }

    private static Object getExisting(Context context, Class<?> n) {
        try {
            java.lang.reflect.Field f = Context.class.getDeclaredField("instances");
            f.setAccessible(true);
            Map<Class<?>, Object> map = (Map<Class<?>, Object>) f.get(context);
            Object inst = map.get(n);
            if (inst != null) return inst;
            for (Class<?> k : map.keySet()) if (n.isAssignableFrom(k)) return map.get(k);
        } catch (Throwable ignored) {}
        return null;
    }
}
