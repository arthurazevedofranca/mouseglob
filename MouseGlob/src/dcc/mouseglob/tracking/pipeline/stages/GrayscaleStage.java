package dcc.mouseglob.tracking.pipeline.stages;

import dcc.graphics.image.GrayscaleImage;
import dcc.mouseglob.tracking.pipeline.PipelineContext;
import dcc.mouseglob.tracking.pipeline.Stage;

/**
 * Converts the input ARGB frame to a grayscale image using luminance.
 */
public final class GrayscaleStage implements Stage {
    @Override
    public void apply(PipelineContext ctx) {
        ctx.gray = ctx.input.luminance(ctx.gray);
    }
}
