package io.github.shivam61.loadshedder.core;
public record LoadShedDecision(DecisionType type, DecisionReason reason, double acceptanceProbability, String explanation) {}
