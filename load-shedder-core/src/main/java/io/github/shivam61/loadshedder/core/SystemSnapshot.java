package io.github.shivam61.loadshedder.core;

public record SystemSnapshot(
        int inflightRequests,
        int queueDepth,
        double cpuLoad,
        double memoryPressure,
        double p50LatencyMs,
        double p95LatencyMs,
        double p99LatencyMs,
        double errorRate,
        double timeoutRate,
        boolean downstreamHealth
) {
}
