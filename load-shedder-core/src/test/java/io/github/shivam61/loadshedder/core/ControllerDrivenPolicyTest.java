package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.Mockito;

class ControllerDrivenPolicyTest {
    @Test
    void shouldRejectOnHardLimit() {
        AdaptiveController mockController = Mockito.mock(AdaptiveController.class);
        Mockito.when(mockController.getState()).thenReturn(new ControllerState(1.0, PriorityAcceptanceShaper.shape(1.0), 0, "TEST", "TEST"));
        ControllerDrivenPolicy policy = new ControllerDrivenPolicy(mockController);
        
        SystemSnapshot hardLimitSnap = new SystemSnapshot(100, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true);
        LoadShedDecision dec = policy.decide(RequestContext.builder().priority(Priority.CRITICAL).build(), hardLimitSnap);
        assertThat(dec.type()).isEqualTo(DecisionType.REJECT);
        assertThat(dec.reason()).isEqualTo(DecisionReason.HARD_LIMIT_BREACHED);
    }

    @Test
    void shouldRejectBasedOnProbability() {
        AdaptiveController mockController = Mockito.mock(AdaptiveController.class);
        // 0.0 probability forces rejection/degradation
        Mockito.when(mockController.getState()).thenReturn(new ControllerState(0.0, PriorityAcceptanceShaper.shape(0.0), 0, "TEST", "TEST"));
        ControllerDrivenPolicy policy = new ControllerDrivenPolicy(mockController);
        SystemSnapshot normalSnap = new SystemSnapshot(10, 100, 0, 100, 0, 0, 0, 0, 0, 0, 0, true);

        // BACKGROUND -> REJECT
        LoadShedDecision rejectDec = policy.decide(RequestContext.builder().priority(Priority.BACKGROUND).build(), normalSnap);
        assertThat(rejectDec.type()).isEqualTo(DecisionType.REJECT);

        // NORMAL -> DEGRADE
        LoadShedDecision degradeDec = policy.decide(RequestContext.builder().priority(Priority.NORMAL).build(), normalSnap);
        assertThat(degradeDec.type()).isEqualTo(DecisionType.DEGRADE);
    }
}
