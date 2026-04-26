package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class AimdAdaptiveControllerTest {
    @Test
    void shouldReduceProbabilityUnderSevereOverload() {
        AimdAdaptiveController controller = new AimdAdaptiveController();
        // Severe overload -> latency 2x target
        ControllerState state = controller.update(new ControlSnapshot(100.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        assertThat(state.globalAcceptanceProbability()).isLessThan(1.0);
        assertThat(state.explanation()).contains("Severe overload");
    }
}
