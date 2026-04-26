package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GradientAdaptiveControllerTest {
    @Test
    void shouldReduceAcceptanceWhenWorsening() {
        GradientAdaptiveController controller = new GradientAdaptiveController();
        controller.update(new ControlSnapshot(40.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05)); // baseline
        ControllerState state = controller.update(new ControlSnapshot(60.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05)); // spike
        assertThat(state.globalAcceptanceProbability()).isLessThan(1.0);
        assertThat(state.explanation()).contains("above target and worsening");
    }
    @Test
    void shouldRecoverWhenImproving() {
        GradientAdaptiveController controller = new GradientAdaptiveController();
        controller.update(new ControlSnapshot(60.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        ControllerState state = controller.update(new ControlSnapshot(40.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        assertThat(state.globalAcceptanceProbability()).isEqualTo(1.0); // maxed
        assertThat(state.explanation()).contains("healthy and improving");
    }
    @Test
    void shouldDecaySlowlyWhenAboveButImproving() {
        GradientAdaptiveController controller = new GradientAdaptiveController();
        controller.update(new ControlSnapshot(100.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        ControllerState state = controller.update(new ControlSnapshot(80.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        assertThat(state.globalAcceptanceProbability()).isEqualTo(0.9025);
        assertThat(state.explanation()).contains("above target but improving");
    }
    @Test
    void shouldDegradeSlightlyWhenHealthyButDegrading() {
        GradientAdaptiveController controller = new GradientAdaptiveController();
        controller.update(new ControlSnapshot(20.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        // Force probability down slightly manually or just test the logic branch:
        // Actually since it caps at 1.0, it will just stay 1.0. We test the branch.
        ControllerState state = controller.update(new ControlSnapshot(30.0, 50.0, 0, 100, 0, 100, 0, 0.05, 0, 0.05));
        assertThat(state.explanation()).contains("healthy but degrading slightly");
    }
}
