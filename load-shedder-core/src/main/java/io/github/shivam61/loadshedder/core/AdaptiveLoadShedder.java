package io.github.shivam61.loadshedder.core;

import java.util.List;

public class AdaptiveLoadShedder {
    private final List<Policy> policies;
    private final DegradationStrategy degradationStrategy;

    public AdaptiveLoadShedder(List<Policy> policies, DegradationStrategy degradationStrategy) {
        this.policies = policies;
        this.degradationStrategy = degradationStrategy;
    }

    public LoadShedDecision evaluate(RequestContext context, SystemSnapshot snapshot) {
        LoadShedDecision finalDecision = LoadShedDecision.ACCEPT;

        for (Policy policy : policies) {
            LoadShedDecision decision = policy.decide(context, snapshot);
            
            // The most restrictive decision wins
            if (decision == LoadShedDecision.REJECT) {
                return LoadShedDecision.REJECT;
            }
            if (decision == LoadShedDecision.DEGRADE) {
                finalDecision = LoadShedDecision.DEGRADE;
            }
            if (decision == LoadShedDecision.QUEUE && finalDecision == LoadShedDecision.ACCEPT) {
                finalDecision = LoadShedDecision.QUEUE;
            }
        }

        return finalDecision;
    }

    public DegradationStrategy getDegradationStrategy() {
        return degradationStrategy;
    }
}
