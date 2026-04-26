package io.github.shivam61.loadshedder.core;
public class PriorityAwarePolicy implements Policy {
    private final int maxInflightRequests;
    private final int highPriorityThreshold;
    private final int criticalThreshold;

    public PriorityAwarePolicy(int maxInflightRequests) {
        this.maxInflightRequests = maxInflightRequests;
        this.highPriorityThreshold = (int) (maxInflightRequests * 0.8);
        this.criticalThreshold = (int) (maxInflightRequests * 0.95);
    }

    @Override
    public LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot) {
        int inflight = snapshot.inflightRequests();
        if (inflight >= maxInflightRequests) return new LoadShedDecision(DecisionType.REJECT, DecisionReason.CAPACITY_EXCEEDED, 0.0, "Limit");
        if (inflight >= criticalThreshold && request.priority().getLevel() < Priority.CRITICAL.getLevel()) return new LoadShedDecision(DecisionType.REJECT, DecisionReason.PRIORITY_SHED, 0.0, "Critical only");
        if (inflight >= highPriorityThreshold && request.priority().getLevel() <= Priority.NORMAL.getLevel()) {
            if (request.priority() == Priority.NORMAL) return new LoadShedDecision(DecisionType.DEGRADE, DecisionReason.PRIORITY_SHED, 0.5, "Degrade normal");
            return new LoadShedDecision(DecisionType.REJECT, DecisionReason.PRIORITY_SHED, 0.0, "Shed low");
        }
        return new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "Healthy");
    }
}
