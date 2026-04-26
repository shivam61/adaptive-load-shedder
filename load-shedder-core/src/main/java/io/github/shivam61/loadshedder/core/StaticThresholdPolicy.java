package io.github.shivam61.loadshedder.core;

public class StaticThresholdPolicy implements Policy {
    private final int maxInflightRequests;

    public StaticThresholdPolicy(int maxInflightRequests) {
        this.maxInflightRequests = maxInflightRequests;
    }

    @Override
    public LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot) {
        if (snapshot.inflightRequests() >= maxInflightRequests) {
            return LoadShedDecision.REJECT;
        }
        return LoadShedDecision.ACCEPT;
    }
}
