package dcc.mouseglob.tracking.pipeline;

import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Image;

/**
 * A processing stage in the tracking pipeline.
 * Implementations read and write fields in the PipelineContext.
 *
 * Contract:
 * - ctx.input: input ARGB image for the current frame (never null).
 * - ctx.gray: may be null initially; stages may produce/update it.
 * - ctx.background: optional grayscale background; stages may read/update it.
 * - ctx.work: optional scratch grayscale buffer.
 * - ctx.mask: binary mask of the tracked foreground; the last stage should produce it.
 */
public interface Stage {
    void apply(PipelineContext ctx);

    /** Utility marker to indicate a Stage can be configured from JSON */
    interface ConfigurableStage extends Stage {
        void configure(java.util.Map<String,Object> params);
    }
}
