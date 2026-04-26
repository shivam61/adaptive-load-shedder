package io.github.shivam61.loadshedder.core;
public record ControlSnapshot(double observedP95, double targetP95, int queueDepth, int maxQueueDepth, int inflight, int maxInflight, double errorRate, double targetErrorRate, double timeoutRate, double targetTimeoutRate) {}
