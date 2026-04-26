# Adaptive Load Shedder

A low-latency, adaptive load shedding library for backend services. 
Protects latency-sensitive services by rejecting or degrading low-priority traffic under overload, ensuring Service Level Objectives (SLOs) are maintained.

## Problem
In highly concurrent systems, sudden spikes in traffic can lead to unbounded queues. When a service is overloaded, response times increase dramatically (tail latency amplification), leading to downstream timeouts. If the service attempts to process requests that have already timed out, it wastes resources on "dead" work, leading to a catastrophic queue collapse. Rate limiting is often insufficient because it uses static thresholds and doesn't adapt to the current health of the system.

## The Approach
**Goal is not maximizing requests served, but maximizing useful requests completed within SLO.**
Adaptive Load Shedder dynamically sheds load based on real-time system metrics (like p95 latency and inflight requests). It uses an AIMD (Additive Increase, Multiplicative Decrease) control loop to adjust acceptance probabilities on the fly. 

## Features
* **Adaptive Control:** Continuously tunes acceptance rate to keep tail latencies within target SLOs.
* **Priority Handling:** Safely sheds BACKGROUND or LOW priority traffic before touching CRITICAL traffic.
* **Graceful Degradation:** Supports degrading requests (e.g., fetching from cache or limiting fanout) instead of outright rejection.
* **Fairness & Isolation:** Protects against noisy neighbors using token buckets and priority weighting.

## Simulation Results
Based on our internal simulator (simulated but reproducible):
* **Baseline (No shedding):** Spikes cause p95 to exceed 500ms, massive timeout rate.
* **Static Threshold:** Rejects too aggressively or too late, missing the "Goldilocks zone".
* **Adaptive Shedder:** Maintains p95 at ~50ms, safely rejects/degrades lower priority traffic, 0% CRITICAL request drops.

## Tradeoffs and Limitations
* **Accuracy vs Latency:** Gathering exact percentiles per request is expensive; the system relies on periodically updated snapshots.
* **Fairness vs Efficiency:** Strict fairness can reduce total throughput. Adaptive Shedder provides weighted probability rather than strict round-robin to maximize efficiency.
* **Simplicity vs Adaptability:** AIMD loops require tuning for specific workload profiles. The defaults work for typical web services but might need adjustment for batch processing.

## Integration
See `load-shedder-grpc` and `load-shedder-http` modules for drop-in interceptors/filters.
