package io.github.shivam61.loadshedder.simulator;
import java.util.Map;
public record SimulationResult(
    String scenario, int totalRequests, int accepted, int rejected, int degraded, int timeouts,
    double p50Latency, double p95Latency, double p99Latency, Map<String, Integer> droppedByPriority, double criticalDropRate
) {}
