package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StaticThresholdPolicyTest {
    @Test
    void shouldAcceptBelowThreshold() {
        StaticThresholdPolicy policy = new StaticThresholdPolicy(10);
        assertThat(policy.decide(RequestContext.builder().build(), new SystemSnapshot(5, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true)).type()).isEqualTo(DecisionType.ACCEPT);
    }
    @Test
    void shouldRejectAtThreshold() {
        StaticThresholdPolicy policy = new StaticThresholdPolicy(10);
        assertThat(policy.decide(RequestContext.builder().build(), new SystemSnapshot(10, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true)).type()).isEqualTo(DecisionType.REJECT);
    }
}
