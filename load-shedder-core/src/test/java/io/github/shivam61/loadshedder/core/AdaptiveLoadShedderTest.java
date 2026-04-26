package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
class AdaptiveLoadShedderTest {
    @Test
    void shouldAcceptWhenAllPoliciesAccept() {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(List.of(
            (req, snap) -> new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "")
        ), ctx -> java.util.Optional.empty());
        LoadShedDecision decision = shedder.evaluate(RequestContext.builder().build(), new SystemSnapshot(0, 10, 0, 10, 0, 0, 0, 0, 0, 0, 0, true));
        assertThat(decision.type()).isEqualTo(DecisionType.ACCEPT);
    }
}
