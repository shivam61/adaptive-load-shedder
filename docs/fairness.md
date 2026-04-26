# Fairness and Tenant Isolation

In multi-tenant environments, a "noisy neighbor" can consume all available resources, causing load shedding policies to reject traffic from well-behaved tenants.

## Implementation
The `TokenBucketPolicy` and `PriorityAwarePolicy` are used to combat this:
1. **Tokens by Priority:** Higher priority traffic is allocated larger buckets or faster refill rates.
2. **Priority Weighting:** The `LatencyAwareAdaptivePolicy` applies multipliers to the base acceptance probability. For example, a `HIGH` priority request might have a `1.2x` multiplier, effectively shielding it from shedding until the baseline probability drops significantly.
