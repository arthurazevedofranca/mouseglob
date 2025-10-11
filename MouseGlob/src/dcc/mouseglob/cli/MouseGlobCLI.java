package dcc.mouseglob.cli;

import dcc.inject.Context;
import dcc.inject.Indexer;
import dcc.mouseglob.PropertiesManager;
import dcc.mouseglob.cli.BatchProcessor.OutputFormat;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.tracking.TrackingManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Command-line entry point to run MouseGlob in headless/batch mode.
 *
 * Usage:
 *   java -cp ... dcc.mouseglob.cli.MouseGlobCLI --input video.mp4 --output out.csv [--ndjson] [--pipeline path.json] [--tracker-size 20]
 *
 * Notes:
 * - No UI is created. Domain logic is wired via the custom DI Context.
 */
public final class MouseGlobCLI {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MouseGlobCLI.class);

    public static void main(String[] args) {
        try {
            Map<String,String> opts = parse(args);
            String in = req(opts, "--input");
            String out = req(opts, "--output");
            OutputFormat fmt = opts.containsKey("--ndjson") ? OutputFormat.NDJSON : OutputFormat.CSV;
            Path pipeline = opts.containsKey("--pipeline") ? Path.of(opts.get("--pipeline")) : null;
            Integer trackerSize = opts.containsKey("--tracker-size") ? Integer.parseInt(opts.get("--tracker-size")) : null;

            // Wire minimal domain graph into a local context (no UI)
            Context ctx = new Context();
            // Ensure field/method injections are applied to these classes and their dependencies
            ctx.inject(Indexer.load(TrackingManager.class, BoundariesManager.class));

            BatchProcessor proc = new BatchProcessor(ctx);
            proc.run(in, Path.of(out), fmt, pipeline, trackerSize);
            log.info("Done.");
        } catch (Throwable t) {
            System.err.println("[ERROR] " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static Map<String,String> parse(String[] args) {
        Map<String,String> m = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("--")) {
                if (i + 1 < args.length && !args[i+1].startsWith("--")) {
                    m.put(a, args[++i]);
                } else {
                    m.put(a, "true");
                }
            }
        }
        return m;
    }

    private static String req(Map<String,String> m, String key) {
        String v = m.get(key);
        if (v == null || v.isEmpty()) throw new IllegalArgumentException("Missing required option: " + key);
        return v;
    }
}
