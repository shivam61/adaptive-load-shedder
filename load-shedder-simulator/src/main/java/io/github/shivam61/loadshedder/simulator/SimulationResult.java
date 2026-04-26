package io.github.shivam61.loadshedder.simulator;

public record SimulationResult(
    int totalRequests,
    int accepted,
    int rejected,
    int degraded,
    double p95Latency,
    double p99Latency
) {
    public void print() {
        System.out.println("Simulation Results:");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Accepted: " + accepted);
        System.out.println("Rejected: " + rejected);
        System.out.println("Degraded: " + degraded);
        System.out.printf("Acceptance Rate: %.2f%%\n", (double) accepted / totalRequests * 100);
        System.out.printf("Rejection Rate: %.2f%%\n", (double) rejected / totalRequests * 100);
        System.out.printf("P95 Latency: %.2f ms\n", p95Latency);
        System.out.printf("P99 Latency: %.2f ms\n", p99Latency);
    }
}
