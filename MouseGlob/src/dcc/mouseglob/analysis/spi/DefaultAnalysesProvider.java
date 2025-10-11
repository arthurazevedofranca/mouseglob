package dcc.mouseglob.analysis.spi;

import dcc.mouseglob.analysis.Analysis;
import dcc.mouseglob.analysis.analyses.*;
import dcc.mouseglob.visit.VisitAnalysis;

import java.util.Arrays;
import java.util.Collection;

/**
 * Default built-in analyses provider. This lists the analyses that ship with MouseGlob.
 * External plugins can provide their own providers on the classpath.
 */
public final class DefaultAnalysesProvider implements AnalysisProvider {
    @Override
    public Collection<Class<? extends Analysis>> getAnalyses() {
        return Arrays.asList(
                MomentsAnalysis.class,
                PositionAnalysis.class,
                OrientationAnalysis.class,
                OrientationCorrectionAnalysis.class,
                DistanceAnalysis.class,
                DisplacementAnalysis.class,
                VelocityAnalysis.class,
                SpeedAnalysis.class,
                TurningAnalysis.class,
                AngleAnalysis.class,
                MouseModel.class,
                VisitAnalysis.class
        );
    }

    @Override
    public String getName() { return "DefaultBuiltInAnalyses"; }
}