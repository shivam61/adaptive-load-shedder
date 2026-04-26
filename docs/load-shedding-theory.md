# Load Shedding Theory

## Tail Latency Amplification
In microservices architectures, a single user request often fans out to dozens of backend services. If any one of those services experiences a latency spike, the entire user request is delayed. This means 99th percentile (p99) latency of individual components heavily influences the median latency of the overall system.

## Queue Collapse
When queues grow unbounded, the time a request spends waiting in the queue exceeds the client's timeout. The server then spends CPU cycles processing a request whose response will be ignored by the client. This leads to a feedback loop where the server throughput drops to 0 "goodput".

## Overload Dynamics
By rejecting load early (at the edge or gateway) and prioritizing critical traffic, we maintain the server in a healthy state, ensuring that the requests it *does* accept are processed successfully and on time.
