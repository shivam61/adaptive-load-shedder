package io.github.shivam61.loadshedder.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

class AdaptiveLoadShedderTest {

    @Test
    void shouldAcceptWhenAllPoliciesAccept() {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(List.of(
            (req, snap) -> LoadShedDecision.ACCEPT,
            (req, snap) -> LoadShedDecision.ACCEPT
        ), ctx -> java.util.Optional.empty());

        LoadShedDecision decision = shedder.evaluate(RequestContext.builder().build(), new SystemSnapshot(0, 0, 0, 0, 0, 0, 0, 0, 0, true));
        assertThat(decision).isEqualTo(LoadShedDecision.ACCEPT);
    }

    @Test
    void shouldRejectIfAnyPolicyRejects() {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(List.of(
            (req, snap) -> LoadShedDecision.ACCEPT,
            (req, snap) -> LoadShedDecision.REJECT
        ), ctx -> java.util.Optional.empty());

        LoadShedDecision decision = shedder.evaluate(RequestContext.builder().build(), new SystemSnapshot(0, 0, 0, 0, 0, 0, 0, 0, 0, true));
        assertThat(decision).isEqualTo(LoadShedDecision.REJECT);
    }

    @Test
    void shouldDegradeIfPolicySuggestsDegrade() {
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(List.of(
            (req, snap) -> LoadShedDecision.ACCEPT,
            (req, snap) -> LoadShedDecision.DEGRADE
        ), ctx -> java.util.Optional.empty());

        LoadShedDecision decision = shedder.evaluate(RequestContext.builder().build(), new SystemSnapshot(0, 0, 0, 0, 0, 0, 0, 0, 0, true));
        assertThat(decision).isEqualTo(LoadShedDecision.DEGRADE);
    }
}
