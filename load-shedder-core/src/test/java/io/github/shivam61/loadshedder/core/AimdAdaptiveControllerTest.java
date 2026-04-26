package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class AimdAdaptiveControllerTest {
    @Test
    void shouldReduceProbabilityUnderSevereOverload() {
        AimdAdaptiveController controller = new AimdAdaptiveController();
        // Severe overload -> latency 2x target -> score > 0.9 (since weighted 0.45, 2 * 0.45 = 0.9). Need other metrics high too for severe.
        ControllerState state = controller.update(new ControlSnapshot(200.0, 50.0, 100, 100, 100, 100, 1.0, 0.05, 1.0, 0.05));
        assertThat(state.globalAcceptanceProbability()).isLessThan(1.0);
        assertThat(state.explanation()).contains("Severe overload");
    }
    @Test
    void shouldIncreaseProbabilityWhenHealthy() {
        AimdAdaptiveController controller = new AimdAdaptiveController();
        // Drive down first
        controller.update(new ControlSnapshot(200.0, 50.0, 100, 100, 100, 100, 1.0, 0.05, 1.0, 0.05));
        double lowProb = controller.getState().globalAcceptanceProbability();
        // Healthy
        controller.update(new ControlSnapshot(20.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        assertThat(controller.getState().globalAcceptanceProbability()).isGreaterThan(lowProb);
    }
    @Test
    void shouldDetectSeriousOverload() {
        AimdAdaptiveController controller = new AimdAdaptiveController();
        // Score between 1.3 and 1.8
        ControllerState state = controller.update(new ControlSnapshot(150.0, 50.0, 100, 100, 100, 100, 0, 0.05, 0, 0.05));
        assertThat(state.explanation()).contains("Serious overload"); // just ensuring code branch hit
    }
}
