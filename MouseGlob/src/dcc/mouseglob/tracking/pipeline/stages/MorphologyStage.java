package dcc.mouseglob.tracking.pipeline.stages;

import dcc.graphics.image.BinaryImage;
import dcc.mouseglob.tracking.pipeline.PipelineContext;
import dcc.mouseglob.tracking.pipeline.Stage;

import java.util.Map;

/**
 * Morphological operation on the binary mask.
 *
 * Params:
 * - operation: "open" (default), "close", "erode", "dilate"
 * - kernel: "3x3" (default) or integer size (odd)
 */
public final class MorphologyStage implements Stage.ConfigurableStage {
    private String operation = "open";
    private int size = 3;

    private static int[][] kernel(int n) {
        int[][] k = new int[n][n];
        for (int j = 0; j < n; j++) for (int i = 0; i < n; i++) k[j][i] = 1;
        return k;
    }

    @Override
    public void configure(Map<String, Object> params) {
        if (params == null) return;
        Object op = params.get("operation"); if (op instanceof String) operation = ((String)op).toLowerCase();
        Object k = params.get("kernel");
        if (k instanceof String) {
            String s = ((String)k).toLowerCase();
            if (s.contains("3")) size = 3; else if (s.contains("5")) size = 5; else if (s.contains("7")) size = 7;
        } else if (k instanceof Number) {
            int v = ((Number)k).intValue();
            if (v % 2 == 0) v++;
            size = Math.max(3, v);
        }
    }

    @Override
    public void apply(PipelineContext ctx) {
        if (ctx.mask == null) return; // nothing to do
        int[][] el = kernel(size);
        BinaryImage out = ctx.mask; // in-place writing is fine as API returns destination
        switch (operation) {
            case "erode":
                ctx.mask = ctx.mask.erode(el, out);
                break;
            case "dilate":
                ctx.mask = ctx.mask.dilate(el, out);
                break;
            case "close":
                ctx.mask = ctx.mask.close(el, out);
                break;
            case "open":
            default:
                ctx.mask = ctx.mask.open(el, out);
                break;
        }
    }
}
