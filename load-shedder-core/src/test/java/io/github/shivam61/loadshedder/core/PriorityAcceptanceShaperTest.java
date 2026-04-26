package io.github.shivam61.loadshedder.core;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class PriorityAcceptanceShaperTest {
    @Test
    void shouldShapeProbabilitiesCorrectly() {
        Map<Priority, Double> shaped = PriorityAcceptanceShaper.shape(0.5);
        assertThat(shaped.get(Priority.CRITICAL)).isEqualTo(0.98); // Min floor
        assertThat(shaped.get(Priority.HIGH)).isEqualTo(0.60); // 0.5 * 1.2
        assertThat(shaped.get(Priority.NORMAL)).isEqualTo(0.50);
        assertThat(shaped.get(Priority.LOW)).isEqualTo(0.25);
        assertThat(shaped.get(Priority.BACKGROUND)).isEqualTo(0.10);
    }
    @Test
    void shouldCapHighAtOne() {
        Map<Priority, Double> shaped = PriorityAcceptanceShaper.shape(0.95);
        assertThat(shaped.get(Priority.HIGH)).isEqualTo(1.0); // Capped at 1.0
    }
}
