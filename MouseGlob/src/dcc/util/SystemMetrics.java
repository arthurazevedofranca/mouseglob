package dcc.util;

import java.lang.management.ManagementFactory;

/**
 * Lightweight system metrics helpers.
 */
public final class SystemMetrics {
    private SystemMetrics() {}

    /**
     * Returns the process CPU load in range [0,100], or -1 if not available.
     */
    public static double getProcessCpuLoadPercent() {
        try {
            // Try com.sun.management.OperatingSystemMXBean if available
            java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean b = (com.sun.management.OperatingSystemMXBean) osBean;
                double v = b.getProcessCpuLoad(); // 0.0-1.0 or negative if not available
                if (v >= 0) return v * 100.0;
            }
        } catch (Throwable ignored) {
        }
        return -1.0;
    }
}
