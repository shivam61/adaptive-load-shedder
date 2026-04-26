package io.github.shivam61.loadshedder.core;
import java.util.concurrent.ThreadLocalRandom;
public class ControllerDrivenPolicy implements Policy {
    private final AdaptiveController controller;
    public ControllerDrivenPolicy(AdaptiveController controller) { this.controller = controller; }

    @Override
    public LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot) {
        ControllerState state = controller.getState();
        double prob = state.priorityAcceptance().getOrDefault(request.priority(), state.globalAcceptanceProbability());

        boolean hardLimitBreached = snapshot.inflightRequests() >= snapshot.maxInflight() || snapshot.queueDepth() >= snapshot.maxQueueDepth();
        if (hardLimitBreached) {
            return new LoadShedDecision(DecisionType.REJECT, DecisionReason.HARD_LIMIT_BREACHED, 0.0, "Hard safety limit breached");
        }

        if (ThreadLocalRandom.current().nextDouble() <= prob) {
            return new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, prob, state.explanation());
        }

        if (request.priority() == Priority.NORMAL || request.priority() == Priority.LOW) {
            return new LoadShedDecision(DecisionType.DEGRADE, DecisionReason.CONTROLLER_REJECTION, prob, state.explanation());
        }

        return new LoadShedDecision(DecisionType.REJECT, DecisionReason.CONTROLLER_REJECTION, prob, state.explanation());
    }
}
