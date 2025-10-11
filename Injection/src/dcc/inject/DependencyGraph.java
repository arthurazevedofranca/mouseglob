/*******************************************************************************
 * Copyright (c) 2016 Daniel Coelho de Castro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Daniel Coelho de Castro - initial API and implementation
 ******************************************************************************/
package dcc.inject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyGraph {

	private final Graph<Class<?>> graph;

	public DependencyGraph() {
		graph = new Graph<Class<?>>();
	}

	public void add(Class<?> clazz) {
		graph.addNode(clazz);
		graph.addEdges(clazz, inspectDependencies(clazz));
	}

	public void addAll(Collection<Class<?>> classes) {
		for (Class<?> clazz : classes)
			add(clazz);
	}

	private static Set<Class<?>> inspectDependencies(Class<?> clazz) {
		Set<Class<?>> dependencies = new HashSet<>();
		for (Class<?> dependency : InjectionUtils.getAllDependencies(clazz)) {
			dependencies.add(dependency);
			dependencies.addAll(inspectDependencies(dependency));
		}
		return dependencies;
	}

	public boolean dependsOn(Class<?> class1, Class<?> class2) {
		return graph.hasEdge(class1, class2);
	}

	public Set<Class<?>> getDependencies(Class<?> clazz) {
		return graph.getOutgoing(clazz);
	}

	public Set<Class<?>> getAllThatDependOn(Class<?> dependency) {
		return graph.getIncoming(dependency);
	}

	public List<Class<?>> getOrder() {
		return graph.getTopologicalOrder();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Class<?> clazz : graph.getNodes()) {
			sb.append(String.format("%s (%H)\n", clazz.getSimpleName(),
					clazz.hashCode()));
			for (Class<?> dep : graph.getOutgoing(clazz))
				sb.append(String.format("\t%s (%H)\n", dep.getSimpleName(),
						dep.hashCode()));
		}
		return sb.toString();
	}

}
