package io.github.shivam61.loadshedder.core;

public enum Priority {
    CRITICAL(4),
    HIGH(3),
    NORMAL(2),
    LOW(1),
    BACKGROUND(0);

    private final int level;

    Priority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
