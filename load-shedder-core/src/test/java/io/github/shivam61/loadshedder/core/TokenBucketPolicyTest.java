package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TokenBucketPolicyTest {
    @Test
    void shouldAcceptWithinCapacity() {
        TokenBucketPolicy policy = new TokenBucketPolicy(100, 100);
        SystemSnapshot snap = new SystemSnapshot(0, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true);
        assertThat(policy.decide(RequestContext.builder().build(), snap).type()).isEqualTo(DecisionType.ACCEPT);
    }
    @Test
    void shouldRejectWhenExhausted() {
        TokenBucketPolicy policy = new TokenBucketPolicy(1, 1);
        SystemSnapshot snap = new SystemSnapshot(0, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true);
        RequestContext req = RequestContext.builder().priority(Priority.CRITICAL).build();
        
        assertThat(policy.decide(req, snap).type()).isEqualTo(DecisionType.ACCEPT);
        assertThat(policy.decide(req, snap).type()).isEqualTo(DecisionType.REJECT); // empty
    }
}
