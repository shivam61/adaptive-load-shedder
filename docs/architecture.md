# Architecture

## System Overview
Adaptive Load Shedder acts as a gatekeeper for incoming requests. It is typically integrated as a middleware/interceptor (e.g., gRPC Interceptor, HTTP Filter).

## Decision Flow
1. **Request Interception:** An incoming request is wrapped in a `RequestContext` (extracting priority, route, tenant).
2. **Snapshot Retrieval:** A `SystemSnapshot` is retrieved (containing p95 latency, inflight count, etc.).
3. **Policy Evaluation:** The orchestrator iterates through a chain of configured `Policies`.
4. **Decision:** The most restrictive decision wins (`REJECT` > `DEGRADE` > `QUEUE` > `ACCEPT`).
5. **Execution:** The interceptor applies the decision (e.g., returns HTTP 429, or sets a degradation flag in the context).
