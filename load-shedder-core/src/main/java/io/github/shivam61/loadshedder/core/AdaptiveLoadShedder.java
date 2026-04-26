package io.github.shivam61.loadshedder.core;
import java.util.List;
import java.util.stream.Collectors;
public class AdaptiveLoadShedder {
    private final List<Policy> policies;
    private final DegradationStrategy degradationStrategy;
    public AdaptiveLoadShedder(List<Policy> policies, DegradationStrategy degradationStrategy) {
        this.policies = policies; this.degradationStrategy = degradationStrategy;
    }
    public LoadShedDecision evaluate(RequestContext context, SystemSnapshot snapshot) {
        List<LoadShedDecision> decisions = policies.stream().map(p -> p.decide(context, snapshot)).collect(Collectors.toList());
        return DecisionCombiner.combine(decisions);
    }
    public DegradationStrategy getDegradationStrategy() { return degradationStrategy; }
}
