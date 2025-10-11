package dcc.mouseglob.tracking.pipeline;

import dcc.graphics.image.BinaryImage;
import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Image;

/**
 * Mutable context object passed between pipeline stages.
 */
public final class PipelineContext {
    public final Image input;              // Input ARGB image for current frame
    public GrayscaleImage gray;            // Working grayscale image
    public GrayscaleImage background;      // Background (optional)
    public GrayscaleImage work;            // Scratch grayscale buffer
    public BinaryImage mask;               // Output binary mask (foreground)

    public PipelineContext(Image input) {
        this.input = input;
    }
}
