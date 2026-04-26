# Experimental: Reinforcement Learning Controller

While `AimdAdaptiveController` and `GradientAdaptiveController` use explicit mathematical rules to adjust acceptance probabilities, an RL-based controller learns these rules over time by interacting with the system.

We provide the `LearningController` interface as an experimental foundation for future research.

## Interface Definition
```java
public interface LearningController {
    ControllerState chooseAction(ControlSnapshot snapshot);
    void observeReward(ControlSnapshot snapshot, Object action, double reward);
}
```

## Reward Function Design
To train an RL agent (like Q-Learning or PPO), the reward function must penalize latency violations and over-shedding. A strong starting reward signal would look like this:

```text
reward = completedWithinSlo 
       - (timeoutRate * timeoutPenalty) 
       - (criticalDropRate * criticalDropPenalty) 
       - overSheddingPenalty
       - unfairnessPenalty
```

## Why it remains experimental
1. **State Space Explosion:** Metrics like latency and queue depth are continuous. Discretizing them accurately is complex.
2. **Cold Start Problem:** An untrained RL controller might shed 100% of traffic or accept 100% of traffic (leading to collapse) during initial learning.
3. **Safety Bounds:** An RL agent needs rigid safety fallbacks (like hard max-inflight limits) to prevent unbounded failures.

Currently, we recommend the deterministic `AimdAdaptiveController` for production systems.
