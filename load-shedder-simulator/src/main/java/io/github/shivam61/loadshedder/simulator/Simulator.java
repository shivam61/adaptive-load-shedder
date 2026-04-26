package io.github.shivam61.loadshedder.simulator;

import io.github.shivam61.loadshedder.core.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator {
    private final AdaptiveLoadShedder shedder;
    private final AtomicInteger inflight = new AtomicInteger();
    private final Queue<Long> responseTimes = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public Simulator(AdaptiveLoadShedder shedder) {
        this.shedder = shedder;
    }

    public SimulationResult run(int durationSeconds, int rpsBase, int rpsSpike, int spikeStartSec, int spikeEndSec) throws InterruptedException {
        int totalRequests = 0;
        int accepted = 0;
        int rejected = 0;
        int degraded = 0;

        for (int sec = 0; sec < durationSeconds; sec++) {
            int currentRps = (sec >= spikeStartSec && sec <= spikeEndSec) ? rpsSpike : rpsBase;
            
            for (int i = 0; i < currentRps; i++) {
                totalRequests++;
                Priority priority = Priority.values()[ThreadLocalRandom.current().nextInt(Priority.values().length)];
                RequestContext req = RequestContext.builder().priority(priority).build();
                
                SystemSnapshot snapshot = generateSnapshot();
                LoadShedDecision decision = shedder.evaluate(req, snapshot);
                
                if (decision == LoadShedDecision.ACCEPT) {
                    accepted++;
                    simulateProcessing(false);
                } else if (decision == LoadShedDecision.DEGRADE) {
                    degraded++;
                    simulateProcessing(true);
                } else {
                    rejected++;
                }
            }
            Thread.sleep(100); // Fast forward simulation, not true realtime
        }

        return new SimulationResult(totalRequests, accepted, rejected, degraded, calculateP95(), calculateP99());
    }

    private SystemSnapshot generateSnapshot() {
        double p95 = calculateP95();
        return new SystemSnapshot(inflight.get(), 0, 0.5, 0.5, p95 * 0.8, p95, p95 * 1.5, 0, 0, true);
    }

    private void simulateProcessing(boolean degraded) {
        inflight.incrementAndGet();
        long start = System.nanoTime();
        
        // Processing time depends on inflight (queueing theory)
        int currentInflight = inflight.get();
        long processingTimeMs = 10 + (currentInflight / 10);
        if (degraded) {
            processingTimeMs /= 2;
        }

        scheduler.schedule(() -> {
            inflight.decrementAndGet();
            long latency = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            responseTimes.add(latency);
            if (responseTimes.size() > 1000) responseTimes.poll();
        }, processingTimeMs, TimeUnit.MILLISECONDS);
    }

    private double calculateP95() {
        return calculatePercentile(95);
    }

    private double calculateP99() {
        return calculatePercentile(99);
    }

    private double calculatePercentile(double percentile) {
        if (responseTimes.isEmpty()) return 0;
        List<Long> sorted = new ArrayList<>(responseTimes);
        Collections.sort(sorted);
        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        if (index < 0) index = 0;
        return sorted.get(index);
    }
}
