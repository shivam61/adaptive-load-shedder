# Adaptive Controller Design

The core of the adaptability is the `LatencyAwareAdaptivePolicy`.

## AIMD Explanation
We use Additive Increase, Multiplicative Decrease (AIMD), inspired by TCP congestion control.
- **Additive Increase:** When observed p95 latency is *below* our target, we slowly increase the acceptance probability (e.g., +0.05).
- **Multiplicative Decrease:** When observed p95 latency exceeds our target, we aggressively cut the acceptance probability (e.g., *0.8).

This ensures rapid response to sudden spikes and safe, gradual recovery when the load subsides.
