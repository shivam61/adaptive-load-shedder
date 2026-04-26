package io.github.shivam61.loadshedder.core;
import java.util.List;
public class DecisionCombiner {
    public static LoadShedDecision combine(List<LoadShedDecision> decisions) {
        if (decisions == null || decisions.isEmpty()) return new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "No policies");
        LoadShedDecision result = decisions.get(0);
        for (LoadShedDecision d : decisions) {
            if (precedence(d.type()) > precedence(result.type())) {
                result = d;
            }
        }
        return result;
    }
    private static int precedence(DecisionType type) {
        return switch (type) {
            case REJECT -> 4;
            case DEGRADE -> 3;
            case QUEUE -> 2;
            case ACCEPT -> 1;
        };
    }
}
