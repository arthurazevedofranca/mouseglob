package dcc.mouseglob.analysis.spi;

import dcc.mouseglob.analysis.Analysis;
import java.util.Collection;

/**
 * SPI for providing Analysis implementations via ServiceLoader.
 * A provider returns the set of Analysis classes it contributes.
 *
 * To register a provider, add a file at
 * META-INF/services/dcc.mouseglob.analysis.spi.AnalysisProvider
 * containing the fully qualified provider class name.
 */
public interface AnalysisProvider {
    /**
     * @return a collection of Analysis implementation classes available from this provider
     */
    Collection<Class<? extends Analysis>> getAnalyses();

    /** Optional provider name for logging */
    default String getName() { return getClass().getName(); }
}