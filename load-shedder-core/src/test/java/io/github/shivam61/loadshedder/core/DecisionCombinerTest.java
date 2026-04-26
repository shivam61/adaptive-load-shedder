package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class DecisionCombinerTest {
    @Test
    void shouldReturnAcceptWhenEmpty() {
        LoadShedDecision decision = DecisionCombiner.combine(List.of());
        assertThat(decision.type()).isEqualTo(DecisionType.ACCEPT);
    }
    @Test
    void shouldPrioritizeRejectOverAll() {
        LoadShedDecision d1 = new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "");
        LoadShedDecision d2 = new LoadShedDecision(DecisionType.REJECT, DecisionReason.CAPACITY_EXCEEDED, 0.0, "");
        LoadShedDecision d3 = new LoadShedDecision(DecisionType.DEGRADE, DecisionReason.CONTROLLER_REJECTION, 0.5, "");
        assertThat(DecisionCombiner.combine(List.of(d1, d2, d3)).type()).isEqualTo(DecisionType.REJECT);
    }
    @Test
    void shouldPrioritizeDegradeOverQueueAndAccept() {
        LoadShedDecision d1 = new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "");
        LoadShedDecision d2 = new LoadShedDecision(DecisionType.QUEUE, DecisionReason.QUEUE_FULL, 0.5, "");
        LoadShedDecision d3 = new LoadShedDecision(DecisionType.DEGRADE, DecisionReason.CONTROLLER_REJECTION, 0.5, "");
        assertThat(DecisionCombiner.combine(List.of(d1, d2, d3)).type()).isEqualTo(DecisionType.DEGRADE);
    }
    @Test
    void shouldPrioritizeQueueOverAccept() {
        LoadShedDecision d1 = new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "");
        LoadShedDecision d2 = new LoadShedDecision(DecisionType.QUEUE, DecisionReason.QUEUE_FULL, 0.5, "");
        assertThat(DecisionCombiner.combine(List.of(d1, d2)).type()).isEqualTo(DecisionType.QUEUE);
    }
}
