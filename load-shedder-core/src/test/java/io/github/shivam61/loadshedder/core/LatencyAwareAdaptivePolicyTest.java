package io.github.shivam61.loadshedder.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LatencyAwareAdaptivePolicyTest {

    @Test
    void shouldReduceProbabilityWhenLatencyExceedsTarget() {
        LatencyAwareAdaptivePolicy policy = new LatencyAwareAdaptivePolicy(50.0);
        
        RequestContext req = RequestContext.builder().priority(Priority.NORMAL).build();
        SystemSnapshot snap = new SystemSnapshot(10, 0, 0.5, 0.5, 20.0, 100.0, 150.0, 0, 0, true);

        // Multiple iterations to show multiplicative decrease
        policy.decide(req, snap);
        double prob1 = policy.getAcceptanceProbability();
        
        policy.decide(req, snap);
        double prob2 = policy.getAcceptanceProbability();

        assertThat(prob1).isLessThan(1.0);
        assertThat(prob2).isLessThan(prob1);
    }

    @Test
    void shouldIncreaseProbabilityWhenLatencyIsBelowTarget() {
        LatencyAwareAdaptivePolicy policy = new LatencyAwareAdaptivePolicy(50.0);
        
        // Decrease it first
        policy.decide(RequestContext.builder().priority(Priority.NORMAL).build(), 
            new SystemSnapshot(10, 0, 0.5, 0.5, 20.0, 100.0, 150.0, 0, 0, true));
        double decreasedProb = policy.getAcceptanceProbability();

        // Now increase it
        policy.decide(RequestContext.builder().priority(Priority.NORMAL).build(), 
            new SystemSnapshot(10, 0, 0.5, 0.5, 20.0, 30.0, 40.0, 0, 0, true));
        
        assertThat(policy.getAcceptanceProbability()).isGreaterThan(decreasedProb);
    }

    @Test
    void shouldAlwaysAcceptCriticalRequests() {
        LatencyAwareAdaptivePolicy policy = new LatencyAwareAdaptivePolicy(50.0);
        
        // Drive probability very low
        for (int i = 0; i < 20; i++) {
            policy.decide(RequestContext.builder().priority(Priority.NORMAL).build(), 
                new SystemSnapshot(10, 0, 0.5, 0.5, 20.0, 200.0, 300.0, 0, 0, true));
        }

        RequestContext criticalReq = RequestContext.builder().priority(Priority.CRITICAL).build();
        LoadShedDecision decision = policy.decide(criticalReq, new SystemSnapshot(10, 0, 0.5, 0.5, 20.0, 200.0, 300.0, 0, 0, true));
        
        assertThat(decision).isEqualTo(LoadShedDecision.ACCEPT);
    }
}
