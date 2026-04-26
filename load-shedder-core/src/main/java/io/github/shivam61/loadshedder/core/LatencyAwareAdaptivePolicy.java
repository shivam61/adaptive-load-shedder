package io.github.shivam61.loadshedder.core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class LatencyAwareAdaptivePolicy implements Policy {
    private final double targetP95Ms;
    private final AtomicReference<Double> acceptanceProbability = new AtomicReference<>(1.0);
    
    // AIMD parameters
    private static final double ADDITIVE_INCREASE = 0.05;
    private static final double MULTIPLICATIVE_DECREASE = 0.8;
    private static final double MIN_PROBABILITY = 0.01;

    public LatencyAwareAdaptivePolicy(double targetP95Ms) {
        this.targetP95Ms = targetP95Ms;
    }

    @Override
    public LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot) {
        updateProbability(snapshot.p95LatencyMs());

        // Always accept critical requests regardless of probability, though they might queue
        if (request.priority() == Priority.CRITICAL) {
            return LoadShedDecision.ACCEPT;
        }

        double prob = acceptanceProbability.get();
        
        // Priority weighting: higher priority requests have a better chance of being accepted
        double priorityWeight = 1.0;
        if (request.priority() == Priority.HIGH) priorityWeight = 1.2;
        if (request.priority() == Priority.LOW) priorityWeight = 0.5;
        if (request.priority() == Priority.BACKGROUND) priorityWeight = 0.2;

        double effectiveProb = Math.min(1.0, prob * priorityWeight);

        if (ThreadLocalRandom.current().nextDouble() <= effectiveProb) {
            return LoadShedDecision.ACCEPT;
        }

        if (request.priority() == Priority.NORMAL || request.priority() == Priority.LOW) {
            return LoadShedDecision.DEGRADE;
        }
        
        return LoadShedDecision.REJECT;
    }

    private void updateProbability(double observedP95) {
        if (observedP95 <= 0) return; // No data

        acceptanceProbability.updateAndGet(current -> {
            if (observedP95 > targetP95Ms) {
                // Multiplicative Decrease
                return Math.max(MIN_PROBABILITY, current * MULTIPLICATIVE_DECREASE);
            } else {
                // Additive Increase
                return Math.min(1.0, current + ADDITIVE_INCREASE);
            }
        });
    }

    public double getAcceptanceProbability() {
        return acceptanceProbability.get();
    }
}
