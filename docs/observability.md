# Observability

For adaptive load shedding to work, it relies heavily on accurate, low-latency metrics.
Additionally, operators need visibility into *why* requests are being shed.

## Key Metrics Exposed
You should integrate `AdaptiveLoadShedder` with your metric registry (e.g., Micrometer):
- `load_shedder_requests_total{decision="ACCEPT/REJECT/DEGRADE"}`
- `load_shedder_inflight`
- `load_shedder_acceptance_probability`
- `load_shedder_latency_p95`

## Decision Tracing
For debugging, trace attributes are added to the distributed trace (e.g., OpenTelemetry) if a request is shed. This allows operators to see exactly which policy triggered the `REJECT` and what the system state was at that moment.
