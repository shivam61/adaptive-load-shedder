package io.github.shivam61.loadshedder.core;

import java.util.Map;

public record RequestContext(
        String requestId,
        String route,
        String tenant,
        Priority priority,
        long deadlineMs,
        long estimatedCost,
        Map<String, String> metadata
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String requestId;
        private String route = "default";
        private String tenant = "default";
        private Priority priority = Priority.NORMAL;
        private long deadlineMs = Long.MAX_VALUE;
        private long estimatedCost = 1;
        private Map<String, String> metadata = Map.of();

        public Builder requestId(String requestId) { this.requestId = requestId; return this; }
        public Builder route(String route) { this.route = route; return this; }
        public Builder tenant(String tenant) { this.tenant = tenant; return this; }
        public Builder priority(Priority priority) { this.priority = priority; return this; }
        public Builder deadlineMs(long deadlineMs) { this.deadlineMs = deadlineMs; return this; }
        public Builder estimatedCost(long estimatedCost) { this.estimatedCost = estimatedCost; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        public RequestContext build() {
            return new RequestContext(requestId, route, tenant, priority, deadlineMs, estimatedCost, metadata);
        }
    }
}
