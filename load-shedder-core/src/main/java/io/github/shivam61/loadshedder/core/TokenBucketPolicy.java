package io.github.shivam61.loadshedder.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketPolicy implements Policy {
    private final ConcurrentHashMap<Priority, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketPolicy(double ratePerSecond, int capacity) {
        for (Priority p : Priority.values()) {
            double priorityMultiplier = Math.max(0.1, p.getLevel() / 4.0);
            if (p == Priority.CRITICAL) priorityMultiplier = 1.0;
            buckets.put(p, new Bucket(capacity * priorityMultiplier, ratePerSecond * priorityMultiplier));
        }
    }

    @Override
    public LoadShedDecision decide(RequestContext request, SystemSnapshot snapshot) {
        Bucket bucket = buckets.get(request.priority());
        if (bucket != null && bucket.tryConsume(request.estimatedCost())) {
            return LoadShedDecision.ACCEPT;
        }
        return LoadShedDecision.REJECT;
    }

    private static class Bucket {
        private final double capacity;
        private final double rate;
        private double tokens;
        private long lastRefillTimestamp;

        Bucket(double capacity, double rate) {
            this.capacity = capacity;
            this.rate = rate;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.nanoTime();
        }

        synchronized boolean tryConsume(long amount) {
            refill();
            if (tokens >= amount) {
                tokens -= amount;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsedTime = (now - lastRefillTimestamp) / 1_000_000_000.0;
            if (elapsedTime > 0) {
                tokens = Math.min(capacity, tokens + elapsedTime * rate);
                lastRefillTimestamp = now;
            }
        }
    }
}
