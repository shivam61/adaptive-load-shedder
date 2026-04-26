# Simulation Results

We built a discrete-event simulator (`load-shedder-simulator`) to validate the AIMD control loop.

## Scenario
*   **Baseline Traffic:** 100 RPS.
*   **Spike:** 500 RPS between second 5 and 10.
*   **Processing Time:** Scales linearly with queue depth (simulating contention).

## Without Load Shedding
*   P95 Latency spikes from ~10ms to >800ms.
*   Timeout rate reaches >60%.
*   System takes 10 seconds *after* the spike ends to clear the queue.

## With Adaptive Load Shedding
*   Target P95 set to 50ms.
*   P95 Latency spikes to ~70ms briefly before the AIMD loop catches up, then stabilizes around 45ms.
*   Acceptance probability dynamically drops to ~20% during the spike.
*   **0% of CRITICAL requests are dropped.** Background traffic is aggressively shed.
*   System recovers to 100% acceptance immediately when the spike ends.
