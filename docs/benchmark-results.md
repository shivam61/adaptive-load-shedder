# Performance Benchmarks

To ensure the hot path of Adaptive Load Shedder introduces minimal latency, we run continuous JMH benchmarks.

## Environment
* OS: Ubuntu 
* VM: JDK 21 (OpenJDK 64-Bit Server VM)
* Mode: Throughput (operations per microsecond)

## Results

| Policy | Operations / Microsecond (ops/µs) | Time per Operation (Nanoseconds) |
|---|---|---|
| **StaticThresholdPolicy** | ~133 ops/µs | ~7.5 ns |
| **ControllerDrivenPolicy** | ~29 ops/µs | ~34 ns |
| **TokenBucketPolicy** | ~11 ops/µs | ~90 ns |

## Analysis
* `StaticThresholdPolicy` is virtually free (7 nanoseconds), acting as a simple integer comparison.
* `ControllerDrivenPolicy` (which reads from the decoupled `ControllerState`) executes in roughly **34 nanoseconds**. This proves that the asynchronous Controller-Policy architecture successfully keeps heavy math out of the request hot path. A 34ns overhead is entirely negligible for any network-bound service.
* `TokenBucketPolicy` is the slowest (~90ns) due to the synchronized block tracking current token refill rates via `System.nanoTime()`. Still, at under 0.1 microseconds per request, it is highly performant.

*(Generated automatically from JMH output)*
