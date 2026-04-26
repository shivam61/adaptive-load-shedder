# Tradeoffs

When implementing load shedding, several architectural tradeoffs must be considered:

## Accuracy vs. Latency
Maintaining perfectly accurate, real-time percentiles (p50, p95, p99) requires synchronization or complex data structures (like HDRHistogram) which can introduce contention. We trade perfect accuracy for speed by using periodic snapshots and lock-free estimators on the hot path.

## Fairness vs. Efficiency
Strictly fair algorithms (like Deficit Round Robin) require significant state management per tenant. We optimize for efficiency by using probabilistic drops based on weights, which provides statistical fairness with zero coordination overhead.

## Simplicity vs. Adaptability
Static thresholds (max inflight) are simple to understand but brittle under changing conditions (e.g., a downstream database slows down). Adaptive policies (AIMD) are resilient but require tuning of the increase/decrease parameters to avoid oscillations.
