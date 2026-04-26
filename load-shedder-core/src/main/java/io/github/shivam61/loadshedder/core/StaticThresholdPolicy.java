package io.github.shivam61.loadshedder.core;
public class StaticThresholdPolicy implements Policy {
    private final int maxInflightRequests;
    public StaticThresholdPolicy(int maxInflightRequests) { this.maxInflightRequests = maxInflightRequests; }
    @Override
    public LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot) {
        if (snapshot.inflightRequests() >= maxInflightRequests) return new LoadShedDecision(DecisionType.REJECT, DecisionReason.CAPACITY_EXCEEDED, 0.0, "Static limit");
        return new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "Under limit");
    }
}
