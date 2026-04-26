# Adaptive Load Shedder

A low-latency, adaptive load shedding library for backend services. 
Protects latency-sensitive services by rejecting or degrading low-priority traffic under overload, ensuring Service Level Objectives (SLOs) are maintained.

## The Problem
In highly concurrent systems, sudden spikes in traffic can lead to unbounded queues. When a service is overloaded, response times increase dramatically (tail latency amplification), leading to downstream timeouts. If the service attempts to process requests that have already timed out, it wastes resources on "dead" work, leading to a catastrophic queue collapse. Static rate limiting is often insufficient because it doesn't adapt to the real-time health and latency of the system.

## The Solution
**Goal is not maximizing requests served, but maximizing useful requests completed within SLO.**

This library implements a decoupled **Controller-Policy** architecture:
1. **Controllers** (`AdaptiveController`) run periodically, analyze real-time system snapshots (p95 latency, queue depth, error rates), and calculate an `overloadScore` to adjust a global acceptance probability.
2. **Policies** (`Policy`) run on the hot path per-request, applying the controller's probability against request priorities (CRITICAL to BACKGROUND) to make a fast `ACCEPT`, `DEGRADE`, or `REJECT` decision.

### High-Level Design (HLD)

```mermaid
graph TD
    %% Control Plane (Async)
    subgraph "Control Plane (Asynchronous Tick)"
        Metrics[(System Metrics\np95 Latency, Inflight)] -->|ControlSnapshot| Controller[AdaptiveController\n(e.g., AIMD)]
        Controller -->|Calculates Overload Score| State[ControllerState\nGlobal Probability]
        State --> Shaper[PriorityAcceptanceShaper]
        Shaper -->|Per-Priority Probabilities| PolicyState((Shared State))
    end

    %% Data Plane (Sync Hot-Path)
    subgraph "Data Plane (Synchronous Hot-Path)"
        Req[Incoming Request] --> Ctx[RequestContext\nPriority, Route]
        Ctx --> Evaluator[ControllerDrivenPolicy]
        
        PolicyState -.->|Lock-free read| Evaluator
        
        Evaluator -->|evaluates| Decision{Decision}
        
        Decision -->|Accept| OK[Proceed to Service]
        Decision -->|Degrade| Fallback[Execute Fallback / Cache]
        Decision -->|Reject| Drop[HTTP 429 / gRPC RESOURCE_EXHAUSTED]
    end
```

### Included Controllers
* **`AimdAdaptiveController` (Default):** Uses Additive Increase, Multiplicative Decrease (AIMD) based on a composite overload score. Modulates probability smoothly during mild load and aggressively cuts load during severe spikes.
* **`GradientAdaptiveController`:** Compares current latencies against past latencies (derivatives) to determine if the system is worsening or improving, providing hyper-responsive shedding in noisy environments.
* **`LearningController` (Experimental):** Interface provided for Reinforcement Learning-based control loops.

## Features
* **Adaptive Control:** Continuously tunes acceptance rate to keep tail latencies within target SLOs.
* **Priority Handling:** Safely sheds `BACKGROUND` or `LOW` priority traffic before touching `CRITICAL` traffic.
* **Graceful Degradation:** Supports degrading requests (e.g., fetching from cache or limiting fanout) instead of outright rejection.
* **Hard Safety Limits:** Circuit breaks even `CRITICAL` traffic if hard queue limits or inflight caps are breached to prevent JVM exhaustion.

## Simulation Results
We built a discrete-event simulator to test these controllers under extreme spikes. 
The results clearly show that AIMD and Gradient controllers protect latency without dropping `CRITICAL` requests.

See the live-generated results here: [docs/simulation-results.md](docs/simulation-results.md).

To run the simulation yourself:
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="io.github.shivam61.loadshedder.examples.BasicExample" -pl load-shedder-examples
```

## Tradeoffs and Limitations
* **Accuracy vs Latency:** Gathering exact percentiles per request is expensive; the system relies on periodically updated snapshots.
* **Fairness vs Efficiency:** Strict fairness can reduce total throughput. This system uses weighted probability (via `PriorityAcceptanceShaper`) rather than strict round-robin to maximize efficiency.

## Integration
See `load-shedder-grpc` and `load-shedder-http` modules for drop-in interceptors/filters.
