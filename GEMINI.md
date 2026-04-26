# AI Agent Guidelines for Adaptive Load Shedder

This repository is designed with strict Staff-level engineering constraints. When assisting or generating code for this repository, you **MUST** adhere to the following rules:

## 1. Architectural Constraints
* **Separation of Concerns:** Never mix control loops with per-request logic. The hot path (`Policy.decide()`) must remain lock-free, O(1), and allocation-light. Heavy calculations (percentiles, gradients, AIMD state updates) belong in the `AdaptiveController.update()` loop which ticks asynchronously.
* **No Heavy Frameworks in Core:** The `load-shedder-core` module must remain free of heavy DI frameworks like Spring. It should only rely on standard Java 21 features.
* **Experimental Features:** The `LearningController` (RL) is strictly experimental. Do not attempt to write a fully trained Q-learning or PPO agent implementation unless specifically and explicitly requested by the user. Default to improving `AimdAdaptiveController` or `GradientAdaptiveController`.

## 2. Testing and Validation
* **Simulator Driven Development:** Any changes to the core algorithms (policies, controllers, shapers) MUST be validated against the discrete-event simulator.
* **Reproducible Claims:** Do not hallucinate performance numbers. If you alter the control algorithms, you must run the simulator:
  `mvn exec:java -Dexec.mainClass="io.github.shivam61.loadshedder.examples.BasicExample" -pl load-shedder-examples`
  and ensure that `docs/simulation-results.md` reflects the new reality.
* **Verification:** Code changes are not complete until `mvn clean test` and the simulation example both execute successfully.

## 3. Idiomatic Code
* **Modern Java:** Use Java 21 idioms: records, enhanced switch statements, and pattern matching where applicable.
* **Observability:** All load shedding decisions must be easily traceable. Ensure `LoadShedDecision` records capture the *reason* and *explanation* for a drop. Do not swallow context.
* **Fail Safe:** Ensure that if metrics (like p95) are missing, the controllers default to safe fallback behaviors rather than crashing or shedding 100% of traffic.

## 4. Documentation
* If you introduce a new feature or controller, you must document its mathematical tradeoffs in the `docs/` folder.
