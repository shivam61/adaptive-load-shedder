package io.github.shivam61.loadshedder.core;
import java.util.Map;
public class PriorityAcceptanceShaper {
    public static Map<Priority, Double> shape(double global) {
        return Map.of(
            Priority.CRITICAL, Math.max(0.98, global),
            Priority.HIGH, Math.min(1.00, global * 1.20),
            Priority.NORMAL, global,
            Priority.LOW, global * 0.50,
            Priority.BACKGROUND, global * 0.20
        );
    }
}
