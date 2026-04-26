package io.github.shivam61.loadshedder.core;
public class GradientAdaptiveController implements AdaptiveController {
    private volatile ControllerState state;
    private double currentAcceptance = 1.0;
    private double previousP95 = -1;

    public GradientAdaptiveController() {
        this.state = new ControllerState(1.0, PriorityAcceptanceShaper.shape(1.0), 0.0, "GRADIENT", "Initial state");
    }

    @Override
    public ControllerState update(ControlSnapshot snapshot) {
        double currentP95 = snapshot.observedP95();
        double error = currentP95 - snapshot.targetP95();
        double gradient = previousP95 < 0 ? 0 : currentP95 - previousP95;
        previousP95 = currentP95;

        String explanation;
        if (error > 0 && gradient > 0) {
            currentAcceptance *= 0.70;
            explanation = "Latency above target and worsening";
        } else if (error > 0 && gradient <= 0) {
            currentAcceptance *= 0.95;
            explanation = "Latency above target but improving";
        } else if (error <= 0 && gradient < 0) {
            currentAcceptance += 0.05;
            explanation = "Latency healthy and improving";
        } else {
            currentAcceptance += 0.01;
            explanation = "Latency healthy but degrading slightly";
        }

        currentAcceptance = Math.max(0.05, Math.min(1.00, currentAcceptance));
        this.state = new ControllerState(currentAcceptance, PriorityAcceptanceShaper.shape(currentAcceptance), error > 0 ? 1.5 : 0.5, "GRADIENT", explanation);
        return this.state;
    }

    @Override
    public ControllerState getState() { return state; }
}
