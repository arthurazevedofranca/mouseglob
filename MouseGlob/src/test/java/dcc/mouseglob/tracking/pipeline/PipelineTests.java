package dcc.mouseglob.tracking.pipeline;

import dcc.graphics.Color;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.Image;

import java.util.Random;

/**
 * Smoke tests for the tracking pipeline. No external test framework required.
 * Run main() to execute checks. Build system compiles this with the app.
 */
public class PipelineTests {

    public static void main(String[] args) {
        boolean ok = true;
        try {
            ok &= detectsBrightBlobUnderNoiseAndIlluminationShift();
            ok &= partialOcclusionStillLeavesSignal();
        } catch (Throwable t) {
            System.out.println("[DEBUG_LOG] Pipeline smoke tests FAILED: " + t);
            t.printStackTrace();
            System.exit(1);
        }
        System.out.println("[DEBUG_LOG] Pipeline smoke tests " + (ok ? "OK" : "FAILED"));
    }

    static boolean detectsBrightBlobUnderNoiseAndIlluminationShift() {
        FramePipeline pipeline = JsonPipelineLoader.loadFromResource("/resource/pipelines/default.json");
        if (pipeline == null || pipeline.getStages().isEmpty()) throw new IllegalStateException("Pipeline not loaded");
        int w = 160, h = 120;
        Image bg = new Image(w, h);
        fill(bg, Color.rgb(120,120,120));
        // create moving bright circle frames
        Random rnd = new Random(123);
        dcc.graphics.image.GrayscaleImage background = null;
        int detections = 0;
        for (int t = 0; t < 20; t++) {
            Image frame = bg.clone();
            int cx = 20 + t * 5; int cy = 60;
            drawCircle(frame, cx, cy, 10, Color.rgb(220,220,220));
            addGaussianNoise(frame, rnd, 10);
            // simulate slow illumination change
            int offset = (t < 10 ? t : 20 - t);
            brighten(frame, offset);

            PipelineContext ctx = pipeline.run(frame, background);
            background = ctx.background; // EMA background from pipeline
            BinaryImage mask = ctx.mask;
            if (mask == null) throw new IllegalStateException("Pipeline must output a mask");
            int white = countWhite(mask);
            if (white > 50) detections++;
        }
        if (detections < 15) throw new AssertionError("Blob not detected often enough, detections=" + detections);
        return true;
    }

    static boolean partialOcclusionStillLeavesSignal() {
        FramePipeline pipeline = JsonPipelineLoader.loadFromResource("/resource/pipelines/default.json");
        if (pipeline == null || pipeline.getStages().isEmpty()) throw new IllegalStateException("Pipeline not loaded");
        int w = 160, h = 120;
        Image bg = new Image(w, h);
        fill(bg, Color.rgb(120,120,120));
        dcc.graphics.image.GrayscaleImage background = null;
        Image frame = bg.clone();
        drawCircle(frame, 80, 60, 12, Color.rgb(220,220,220));
        // Occlude half with background color
        fillRect(frame, 80, 60, 12, 12, Color.rgb(120,120,120));

        PipelineContext ctx = pipeline.run(frame, background);
        BinaryImage mask = ctx.mask;
        int white = countWhite(mask);
        if (white <= 20) throw new AssertionError("Occlusion test failed, white=" + white);
        return true;
    }

    // --- helpers ---
    private static void fill(Image img, int rgb) {
        int[] p = img.getPixels();
        for (int i = 0; i < p.length; i++) p[i] = rgb;
    }

    private static void drawCircle(Image img, int cx, int cy, int r, int rgb) {
        int w = img.getWidth(), h = img.getHeight();
        for (int y = Math.max(0, cy - r); y < Math.min(h, cy + r); y++) {
            for (int x = Math.max(0, cx - r); x < Math.min(w, cx + r); x++) {
                int dx = x - cx, dy = y - cy;
                if (dx*dx + dy*dy <= r*r) img.set(x,y, rgb);
            }
        }
    }

    private static void fillRect(Image img, int cx, int cy, int hw, int hh, int rgb) {
        int w = img.getWidth(), h = img.getHeight();
        for (int y = Math.max(0, cy - hh); y < Math.min(h, cy + hh); y++) {
            for (int x = cx; x < Math.min(w, cx + hw); x++) {
                img.set(x,y, rgb);
            }
        }
    }

    private static void addGaussianNoise(Image img, java.util.Random rnd, int sigma) {
        int[] p = img.getPixels();
        for (int i = 0; i < p.length; i++) {
            int r = Color.r(p[i]); int g = Color.g(p[i]); int b = Color.b(p[i]);
            int n = (int) Math.round(rnd.nextGaussian() * sigma);
            r = clamp(r + n); g = clamp(g + n); b = clamp(b + n);
            p[i] = Color.rgb(r,g,b);
        }
    }

    private static void brighten(Image img, int offset) {
        int[] p = img.getPixels();
        for (int i = 0; i < p.length; i++) {
            int r = Color.r(p[i]); int g = Color.g(p[i]); int b = Color.b(p[i]);
            r = clamp(r + offset); g = clamp(g + offset); b = clamp(b + offset);
            p[i] = Color.rgb(r,g,b);
        }
    }

    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    private static int countWhite(BinaryImage mask) {
        int[] p = mask.getPixels();
        int c = 0; for (int v : p) if (v == Color.WHITE) c++;
        return c;
    }
}
