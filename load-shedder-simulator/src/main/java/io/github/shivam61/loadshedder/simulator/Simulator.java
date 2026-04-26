package io.github.shivam61.loadshedder.simulator;
import io.github.shivam61.loadshedder.core.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator {
    private final AdaptiveLoadShedder shedder;
    private final AdaptiveController controller; // Can be null
    private final AtomicInteger inflight = new AtomicInteger();
    private final Queue<Long> responseTimes = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public Simulator(AdaptiveLoadShedder shedder, AdaptiveController controller) {
        this.shedder = shedder;
        this.controller = controller;
    }

    public SimulationResult run(String scenario, int durationSeconds, int rpsBase, int rpsSpike, int spikeStartSec, int spikeEndSec) throws InterruptedException {
        int totalRequests = 0, accepted = 0, rejected = 0, degraded = 0, timeouts = 0, criticalTotal = 0, criticalDropped = 0;
        Map<String, Integer> droppedByPriority = new HashMap<>();

        for (int sec = 0; sec < durationSeconds; sec++) {
            if (controller != null) {
                ControlSnapshot csnap = new ControlSnapshot(calculateP95(), 50.0, inflight.get(), 1000, inflight.get(), 1000, 0.0, 0.05, 0.0, 0.05);
                controller.update(csnap);
            }

            int currentRps = (sec >= spikeStartSec && sec <= spikeEndSec) ? rpsSpike : rpsBase;
            for (int i = 0; i < currentRps; i++) {
                totalRequests++;
                Priority priority = Priority.values()[ThreadLocalRandom.current().nextInt(Priority.values().length)];
                if (priority == Priority.CRITICAL) criticalTotal++;
                RequestContext req = RequestContext.builder().priority(priority).build();
                
                SystemSnapshot snapshot = new SystemSnapshot(inflight.get(), 1000, inflight.get(), 1000, 0.5, 0.5, calculatePercentile(50), calculateP95(), calculatePercentile(99), 0, 0, true);
                LoadShedDecision decision = shedder != null ? shedder.evaluate(req, snapshot) : new LoadShedDecision(DecisionType.ACCEPT, DecisionReason.HEALTHY, 1.0, "");
                
                if (decision.type() == DecisionType.ACCEPT) { accepted++; simulateProcessing(false); }
                else if (decision.type() == DecisionType.DEGRADE) { degraded++; simulateProcessing(true); }
                else {
                    rejected++;
                    if (priority == Priority.CRITICAL) criticalDropped++;
                    droppedByPriority.merge(priority.name(), 1, Integer::sum);
                }
            }
            Thread.sleep(10); // Super fast sim
        }

        double crDropRate = criticalTotal > 0 ? (double)criticalDropped / criticalTotal : 0;
        return new SimulationResult(scenario, totalRequests, accepted, rejected, degraded, timeouts, calculatePercentile(50), calculateP95(), calculatePercentile(99), droppedByPriority, crDropRate);
    }

    private void simulateProcessing(boolean degraded) {
        inflight.incrementAndGet();
        long start = System.nanoTime();
        int currentInflight = inflight.get();
        long processingTimeMs = 10 + (currentInflight / 5);
        if (degraded) processingTimeMs /= 2;

        scheduler.schedule(() -> {
            inflight.decrementAndGet();
            long latency = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            responseTimes.add(latency);
            if (responseTimes.size() > 1000) responseTimes.poll();
        }, processingTimeMs, TimeUnit.MILLISECONDS);
    }

    private double calculateP95() { return calculatePercentile(95); }
    private double calculatePercentile(double percentile) {
        if (responseTimes.isEmpty()) return 0;
        List<Long> sorted = new ArrayList<>(responseTimes);
        Collections.sort(sorted);
        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }
}
