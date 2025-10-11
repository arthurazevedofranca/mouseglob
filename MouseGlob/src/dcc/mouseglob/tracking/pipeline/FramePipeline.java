package dcc.mouseglob.tracking.pipeline;

import java.util.ArrayList;
import java.util.List;

import dcc.graphics.image.GrayscaleImage;
import dcc.graphics.image.Image;

/**
 * A simple sequential frame processing pipeline.
 */
public final class FramePipeline {
    private final List<Stage> stages = new ArrayList<>();

    public FramePipeline add(Stage stage) {
        if (stage != null) stages.add(stage);
        return this;
    }

    public List<Stage> getStages() { return stages; }

    public PipelineContext run(Image input, GrayscaleImage background) {
        PipelineContext ctx = new PipelineContext(input);
        ctx.background = background;
        for (Stage s : stages) s.apply(ctx);
        return ctx;
    }
}
