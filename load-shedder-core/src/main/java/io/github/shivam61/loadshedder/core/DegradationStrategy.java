package io.github.shivam61.loadshedder.core;

import java.util.Optional;

public interface DegradationStrategy {
    Optional<String> getFallbackStrategy(RequestContext context);
}
