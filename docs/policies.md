# Policies

The `load-shedder-core` includes a flexible `Policy` interface, allowing you to combine multiple strategies. The `AdaptiveLoadShedder` evaluates all policies and takes the most restrictive decision (`REJECT` > `DEGRADE` > `QUEUE` > `ACCEPT`).

## Included Policies
1. **StaticThresholdPolicy:** Rejects requests if inflight requests exceed a static limit.
2. **PriorityAwarePolicy:** Has tiered limits. E.g., normal requests are rejected at 80% capacity, high priority at 95%, and critical requests are never rejected by this policy.
3. **TokenBucketPolicy:** Rate limits requests per priority level using the Token Bucket algorithm.
4. **LatencyAwareAdaptivePolicy:** Uses AIMD to dynamically adjust an acceptance probability based on p95 latency.

You can chain these together. For example, a `StaticThresholdPolicy` acting as an absolute failsafe limit, combined with a `LatencyAwareAdaptivePolicy` to gracefully handle typical spikes.
