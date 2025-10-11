package dcc.mouseglob.tracking.pipeline.stages;

import dcc.graphics.Color;
import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.GrayscaleImage;
import dcc.mouseglob.tracking.pipeline.PipelineContext;
import dcc.mouseglob.tracking.pipeline.Stage;

import java.util.Map;

/**
 * Thresholding stage supporting global and adaptive-mean methods.
 *
 * Params:
 * - mode: "global" (default) or "adaptiveMean"
 * - threshold: integer [0..255] used in global mode (default 128)
 * - dark: boolean when true selects dark-on-light threshold (default false => light-on-dark)
 * - blockSize: odd window size for adaptive mean (default 15)
 * - c: constant subtracted from mean in adaptive method (default 5)
 */
public final class AdaptiveThresholdStage implements Stage.ConfigurableStage {
    private String mode = "global";
    private int threshold = 128;
    private boolean dark = false;
    private int blockSize = 15;
    private int c = 5;

    @Override
    public void configure(Map<String, Object> params) {
        if (params == null) return;
        Object m = params.get("mode"); if (m instanceof String) mode = ((String)m).toLowerCase();
        Object t = params.get("threshold"); if (t instanceof Number) threshold = ((Number)t).intValue();
        Object d = params.get("dark"); if (d instanceof Boolean) dark = (Boolean)d;
        Object bs = params.get("blockSize"); if (bs instanceof Number) blockSize = Math.max(3, ((Number)bs).intValue() | 1);
        Object cc = params.get("c"); if (cc instanceof Number) c = ((Number)cc).intValue();
    }

    @Override
    public void apply(PipelineContext ctx) {
        if (ctx.gray == null) ctx.gray = ctx.input.luminance(ctx.gray);
        if ("adaptiveMean".equals(mode)) {
            // approximate local mean by gaussian blur with sigma ~ blockSize/6
            double sigma = Math.max(1.0, blockSize / 6.0);
            ctx.work = ctx.gray.blur(sigma, ctx.work);
            if (ctx.mask == null) ctx.mask = new BinaryImage(ctx.gray.getWidth(), ctx.gray.getHeight());
            int[] g = ctx.gray.getPixels();
            int[] m = ctx.work.getPixels();
            int[] out = ctx.mask.getPixels();
            for (int i = 0; i < out.length; i++) {
                int gv = Color.r(g[i]);
                int mv = Color.r(m[i]) + c;
                boolean isFg = dark ? (gv < mv) : (gv > mv);
                out[i] = isFg ? Color.WHITE : Color.BLACK;
            }
        } else {
            // global threshold
            if (dark) ctx.mask = ctx.gray.inverseThreshold(threshold, ctx.mask);
            else ctx.mask = ctx.gray.threshold(threshold, ctx.mask);
        }
    }
}
