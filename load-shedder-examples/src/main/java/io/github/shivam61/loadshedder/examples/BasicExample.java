package io.github.shivam61.loadshedder.examples;

import io.github.shivam61.loadshedder.core.*;
import io.github.shivam61.loadshedder.simulator.*;
import java.util.List;
import java.util.Optional;

public class BasicExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. Setup Adaptive Load Shedder
        AdaptiveLoadShedder shedder = new AdaptiveLoadShedder(List.of(
            new PriorityAwarePolicy(200),
            new LatencyAwareAdaptivePolicy(50.0) // target p95 of 50ms
        ), ctx -> Optional.of("fallback_data"));

        // 2. Setup Simulator
        Simulator simulator = new Simulator(shedder);

        System.out.println("Starting Load Simulation...");
        System.out.println("Baseline: 100 req/sec, Spike: 500 req/sec (sec 5-10)");

        // 3. Run Simulation (15 seconds, 100 RPS normal, 500 RPS spike)
        SimulationResult result = simulator.run(15, 100, 500, 5, 10);
        
        System.out.println("\n--- Simulation Complete ---");
        result.print();
        System.exit(0);
    }
}
