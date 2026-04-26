# Contributing to Adaptive Load Shedder

We welcome contributions!

## Development Setup
1. Ensure you have Java 21 and Maven installed.
2. Run `mvn clean install` to build the project.
3. Run the simulator using the examples module: `mvn exec:java -Dexec.mainClass="io.github.shivam61.loadshedder.examples.BasicExample" -pl load-shedder-examples`

## Guidelines
-   All new policies must be accompanied by comprehensive unit tests.
-   Modifications to the hot path (core evaluation logic) must include JMH benchmarks proving they do not introduce allocation overhead or latency regressions.
-   Avoid bringing in heavy dependencies (like Spring) to the core module. The core should remain dependency-free except for a JSON parser (if needed for config) and testing tools.
