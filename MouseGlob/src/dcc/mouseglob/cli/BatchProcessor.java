package dcc.mouseglob.cli;

import dcc.graphics.PImageAdapter;
import dcc.graphics.image.Image;
import dcc.inject.Context;
import dcc.mouseglob.PropertiesManager;
import dcc.mouseglob.maze.BoundariesManager;
import dcc.mouseglob.maze.BoundaryMask;
import dcc.mouseglob.tracking.Tracker;
import dcc.mouseglob.tracking.TrackingManager;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Headless/batch processor that runs tracking without any UI and writes simple outputs.
 * This class wires only domain components and iterates the video frames using FFmpeg directly.
 */
public final class BatchProcessor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BatchProcessor.class);

    public enum OutputFormat { CSV, NDJSON }

    private final Context context;
    private final TrackingManager trackingManager;
    private final BoundariesManager boundariesManager;

    public BatchProcessor(Context context) {
        this.context = context;
        this.trackingManager = context.getInstance(TrackingManager.class);
        this.boundariesManager = context.getInstance(BoundariesManager.class);
    }

    public void run(String inputVideo,
                    Path outputFile,
                    OutputFormat fmt,
                    Path pipelineFile,
                    Integer trackerSize) throws Exception {
        if (pipelineFile != null) {
            PropertiesManager.getInstance().set("tracking.pipeline.file", pipelineFile.toString());
            PropertiesManager.getInstance().set("tracking.pipeline.enabled", "true");
        }
        if (trackerSize != null && trackerSize > 0) {
            // best-effort: public API only exposes getTrackerSize; set via controller normally
            try {
                java.lang.reflect.Method m = TrackingManager.class.getDeclaredMethod("setTrackerSize", int.class);
                m.setAccessible(true);
                m.invoke(trackingManager, trackerSize);
            } catch (ReflectiveOperationException ignore) {
                log.warn("Could not set tracker size via reflection; using default.");
            }
        }

        Files.createDirectories(outputFile.getParent() == null ? Path.of(".") : outputFile.getParent());
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(coerceExt(outputFile, fmt)))) {
            if (fmt == OutputFormat.CSV) writeCsvHeader(out);
            processVideo(inputVideo, out, fmt);
        }
    }

    private static Path coerceExt(Path file, OutputFormat fmt) {
        String s = file.toString();
        String ext = (fmt == OutputFormat.CSV ? ".csv" : ".ndjson");
        if (!s.toLowerCase(Locale.ROOT).endsWith(ext)) return file.resolveSibling(s + ext);
        return file;
    }

    private void writeCsvHeader(PrintWriter out) {
        out.println("t_ms,x_px,y_px");
    }

    private void processVideo(String inputVideo, PrintWriter out, OutputFormat fmt) throws Exception {
        log.info("Starting headless processing of {}", inputVideo);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideo)) {
            grabber.start();
            Java2DFrameConverter conv = new Java2DFrameConverter();
            Image scratch = null;
            boolean trackerAdded = false;
            long lastTs = -1L;
            while (true) {
                Frame f = grabber.grabImage();
                if (f == null) break; // EOF
                BufferedImage bi = conv.convert(f);
                if (bi == null) continue;
                long tMs = Math.max(0L, grabber.getTimestamp() / 1000L);
                if (tMs == lastTs) tMs++; // enforce monotonic
                lastTs = tMs;

                // Convert and dispatch to domain components (no UI)
                Image img = PImageAdapter.bufferedToImage(bi, scratch);
                scratch = img;
                // Boundaries may need video size to initialize masks
                boundariesManager.newFrame(img, tMs);

                // Add a default tracker at first frame center
                if (!trackerAdded) {
                    int cx = bi.getWidth() / 2;
                    int cy = bi.getHeight() / 2;
                    BoundaryMask mask = boundariesManager.getMask(cx, cy);
                    Tracker tracker = new Tracker(cx, cy, trackingManager.getTrackerSize(), mask);
                    tracker.setName("tracker-1");
                    trackingManager.add(tracker);
                    trackerAdded = true;
                }

                // Run tracking for this frame
                trackingManager.newFrame(img, tMs);

                // For simplicity, output the first tracker's position each frame
                if (fmt == OutputFormat.CSV) {
                    writeCsvLine(out, tMs);
                } else {
                    writeNdjsonLine(out, tMs);
                }
            }
            grabber.stop();
        }
        log.info("Headless processing done.");
    }

    private void writeCsvLine(PrintWriter out, long tMs) {
        Tracker tr = firstTracker();
        if (tr == null) return;
        dcc.graphics.math.Vector p = tr.getPosition();
        out.printf(Locale.ROOT, "%d,%.2f,%.2f%n", tMs, p.x, p.y);
    }

    private void writeNdjsonLine(PrintWriter out, long tMs) {
        Tracker tr = firstTracker();
        if (tr == null) return;
        dcc.graphics.math.Vector p = tr.getPosition();
        // Minimal NDJSON per frame
        String json = String.format(Locale.ROOT,
                "{\"type\":\"frame\",\"t_ms\":%d,\"trackers\":[{\"name\":\"%s\",\"x_px\":%.2f,\"y_px\":%.2f}]}",
                tMs, tr.getName() == null ? "tracker-1" : tr.getName(), p.x, p.y);
        out.println(json);
    }

    private Tracker firstTracker() {
        java.util.List<Tracker> list = trackingManager.getTrackers();
        return list.isEmpty() ? null : list.get(0);
    }
}
