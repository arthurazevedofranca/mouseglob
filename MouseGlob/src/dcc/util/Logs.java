package dcc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Centralized logging utilities for SLF4J/Logback and MDC helpers.
 */
public final class Logs {
    private Logs() {}

    public static Logger get(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void setExperimentId(String experimentId) {
        if (experimentId == null || experimentId.isEmpty()) {
            MDC.remove("experimentId");
        } else {
            MDC.put("experimentId", experimentId);
        }
    }

    public static void clearExperimentId() {
        MDC.remove("experimentId");
    }
}
