package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PriorityAwarePolicyTest {
    @Test
    void shouldApplyTieredShedding() {
        PriorityAwarePolicy policy = new PriorityAwarePolicy(100);
        // At 85/100, High+ accepted, Normal degraded, Low rejected
        SystemSnapshot snap85 = new SystemSnapshot(85, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true);
        assertThat(policy.decide(RequestContext.builder().priority(Priority.HIGH).build(), snap85).type()).isEqualTo(DecisionType.ACCEPT);
        assertThat(policy.decide(RequestContext.builder().priority(Priority.NORMAL).build(), snap85).type()).isEqualTo(DecisionType.DEGRADE);
        assertThat(policy.decide(RequestContext.builder().priority(Priority.LOW).build(), snap85).type()).isEqualTo(DecisionType.REJECT);

        // At 96/100, Critical accepted, High rejected
        SystemSnapshot snap96 = new SystemSnapshot(96, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true);
        assertThat(policy.decide(RequestContext.builder().priority(Priority.CRITICAL).build(), snap96).type()).isEqualTo(DecisionType.ACCEPT);
        assertThat(policy.decide(RequestContext.builder().priority(Priority.HIGH).build(), snap96).type()).isEqualTo(DecisionType.REJECT);
    }
}
