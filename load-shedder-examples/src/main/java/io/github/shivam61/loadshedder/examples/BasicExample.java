package io.github.shivam61.loadshedder.examples;
import io.github.shivam61.loadshedder.core.*;
import io.github.shivam61.loadshedder.simulator.*;
import java.util.*;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicExample {
    public static void main(String[] args) throws Exception {
        System.out.println("Running comprehensive simulations...");
        
        // 1. No Shedding
        Simulator noShedSim = new Simulator(null, null);
        SimulationResult r1 = noShedSim.run("No Shedding", 15, 100, 800, 5, 10);
        
        // 2. Static Shedding
        AdaptiveLoadShedder staticShedder = new AdaptiveLoadShedder(List.of(new StaticThresholdPolicy(300)), ctx -> Optional.empty());
        Simulator staticSim = new Simulator(staticShedder, null);
        SimulationResult r2 = staticSim.run("Static Threshold", 15, 100, 800, 5, 10);
        
        // 3. AIMD Shedding
        AimdAdaptiveController aimd = new AimdAdaptiveController();
        AdaptiveLoadShedder aimdShedder = new AdaptiveLoadShedder(List.of(new ControllerDrivenPolicy(aimd)), ctx -> Optional.empty());
        Simulator aimdSim = new Simulator(aimdShedder, aimd);
        SimulationResult r3 = aimdSim.run("AIMD Controller", 15, 100, 800, 5, 10);

        // 4. Gradient Shedding
        GradientAdaptiveController grad = new GradientAdaptiveController();
        AdaptiveLoadShedder gradShedder = new AdaptiveLoadShedder(List.of(new ControllerDrivenPolicy(grad)), ctx -> Optional.empty());
        Simulator gradSim = new Simulator(gradShedder, grad);
        SimulationResult r4 = gradSim.run("Gradient Controller", 15, 100, 800, 5, 10);

        List<SimulationResult> results = List.of(r1, r2, r3, r4);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("../simulation-results.json"), results);

        StringBuilder md = new StringBuilder("# Simulation Results\n\n");
        md.append("| Scenario | Total | Accepted | Rejected | p95 Latency | Critical Drop Rate |\n");
        md.append("|---|---|---|---|---|---|\n");
        for (SimulationResult r : results) {
            md.append(String.format("| %s | %d | %d | %d | %.1fms | %.2f%% |\n", r.scenario(), r.totalRequests(), r.accepted(), r.rejected(), r.p95Latency(), r.criticalDropRate() * 100));
        }
        java.nio.file.Files.writeString(java.nio.file.Paths.get("../simulation-results.md"), md.toString());

        System.out.println("Simulation complete. Wrote simulation-results.json and simulation-results.md");
        System.exit(0);
    }
}
