package io.github.shivam61.loadshedder.core;
public class AimdAdaptiveController implements AdaptiveController {
    private volatile ControllerState state;
    private double currentAcceptance = 1.0;

    public AimdAdaptiveController() {
        this.state = new ControllerState(1.0, PriorityAcceptanceShaper.shape(1.0), 0.0, "AIMD", "Initial state");
    }

    @Override
    public ControllerState update(ControlSnapshot snapshot) {
        double latencyPressure = snapshot.targetP95() > 0 ? snapshot.observedP95() / snapshot.targetP95() : 0;
        double queuePressure = snapshot.maxQueueDepth() > 0 ? (double)snapshot.queueDepth() / snapshot.maxQueueDepth() : 0;
        double inflightPressure = snapshot.maxInflight() > 0 ? (double)snapshot.inflight() / snapshot.maxInflight() : 0;
        double errorPressure = snapshot.targetErrorRate() > 0 ? snapshot.errorRate() / snapshot.targetErrorRate() : 0;
        double timeoutPressure = snapshot.targetTimeoutRate() > 0 ? snapshot.timeoutRate() / snapshot.targetTimeoutRate() : 0;

        double overloadScore = 0.45 * latencyPressure + 0.20 * queuePressure + 0.15 * inflightPressure + 0.10 * errorPressure + 0.10 * timeoutPressure;

        String explanation;
        if (overloadScore > 1.8) {
            currentAcceptance *= 0.60;
            explanation = "Severe overload";
        } else if (overloadScore > 1.3) {
            currentAcceptance *= 0.75;
            explanation = "Serious overload";
        } else if (overloadScore > 1.0) {
            currentAcceptance *= 0.90;
            explanation = "Mild overload";
        } else {
            currentAcceptance += 0.02;
            explanation = "Healthy";
        }

        currentAcceptance = Math.max(0.05, Math.min(1.00, currentAcceptance));
        this.state = new ControllerState(currentAcceptance, PriorityAcceptanceShaper.shape(currentAcceptance), overloadScore, "AIMD", explanation);
        return this.state;
    }

    @Override
    public ControllerState getState() { return state; }
}
