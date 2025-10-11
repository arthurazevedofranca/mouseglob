package dcc.mouseglob.tracking.pipeline.stages;

import dcc.graphics.Color;
import dcc.graphics.image.GrayscaleImage;
import dcc.mouseglob.tracking.pipeline.PipelineContext;
import dcc.mouseglob.tracking.pipeline.Stage;

import java.util.Map;

/**
 * Background subtraction stage.
 *
 * Modes:
 * - static: keep first frame as background and subtract current gray.
 * - running: update background with exponential moving average: bg = (1-a)*bg + a*gray
 *   where a in [0,1].
 *
 * Params:
 * - mode: "static" (default) or "running"
 * - alpha: smoothing factor for running mode (default 0.05)
 */
public final class BackgroundSubtractStage implements Stage.ConfigurableStage {
    private String mode = "static";
    private double alpha = 0.05;

    @Override
    public void configure(Map<String, Object> params) {
        if (params == null) return;
        Object m = params.get("mode");
        if (m instanceof String) mode = ((String) m).toLowerCase();
        Object a = params.get("alpha");
        if (a instanceof Number) alpha = Math.max(0.0, Math.min(1.0, ((Number) a).doubleValue()));
    }

    @Override
    public void apply(PipelineContext ctx) {
        if (ctx.gray == null) ctx.gray = ctx.input.luminance(ctx.gray);
        if (ctx.background == null) {
            ctx.background = ctx.gray.copy(ctx.background);
        } else if ("running".equals(mode) && alpha > 0.0) {
            // Exponential moving average in integer space
            int[] bg = ctx.background.getPixels();
            int[] gr = ctx.gray.getPixels();
            for (int i = 0; i < bg.length; i++) {
                int g = Color.r(gr[i]);
                int b = Color.r(bg[i]);
                int nb = (int) Math.round(b + alpha * (g - b));
                if (nb < 0) nb = 0; else if (nb > 255) nb = 255;
                bg[i] = Color.rgb(nb, nb, nb);
            }
        }
        // Replace gray with |gray - background|
        ctx.work = ctx.gray.subtract(ctx.background, ctx.work);
        ctx.gray = ctx.work;
    }
}
