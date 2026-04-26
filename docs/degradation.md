# Degradation

Sometimes, completely rejecting a request (`REJECT`) provides a poor user experience. It is often better to provide a degraded experience (`DEGRADE`).

## When to Degrade
The `LatencyAwareAdaptivePolicy` naturally returns `DEGRADE` for `NORMAL` and `LOW` priority requests when the system is under moderate pressure, before it reaches the point of returning `REJECT`.

## Implementing Degradation
The `DegradationStrategy` interface provides a fallback instruction string to the application.
Examples of degradation:
*   **Search Service:** Limit search to 10 results instead of 100, or disable heavy facets.
*   **Recommendation Engine:** Return pre-computed cached recommendations instead of live ML inference.
*   **Social Feed:** Load only text, omit heavy image/video queries.

The interceptor/filter flags the `RequestContext`, and the application layer checks this flag to alter its execution path.
