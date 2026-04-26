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
        
        if (inflight >= maxInflightRequests) {
            return LoadShedDecision.REJECT;
        }

        if (inflight >= criticalThreshold && request.priority().getLevel() < Priority.CRITICAL.getLevel()) {
            return LoadShedDecision.REJECT;
        }

        if (inflight >= highPriorityThreshold && request.priority().getLevel() <= Priority.NORMAL.getLevel()) {
            if (request.priority() == Priority.NORMAL) {
                return LoadShedDecision.DEGRADE;
            }
            return LoadShedDecision.REJECT;
        }

        return LoadShedDecision.ACCEPT;
    }
}
