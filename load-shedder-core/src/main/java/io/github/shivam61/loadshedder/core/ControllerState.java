package io.github.shivam61.loadshedder.core;
import java.util.Map;
public record ControllerState(double globalAcceptanceProbability, Map<Priority, Double> priorityAcceptance, double overloadScore, String mode, String explanation) {}
