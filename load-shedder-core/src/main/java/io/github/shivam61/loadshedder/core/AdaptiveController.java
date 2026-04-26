package io.github.shivam61.loadshedder.core;
public interface AdaptiveController {
    ControllerState update(ControlSnapshot snapshot);
    ControllerState getState();
}
