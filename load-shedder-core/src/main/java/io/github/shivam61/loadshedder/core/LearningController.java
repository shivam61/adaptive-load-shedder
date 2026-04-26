package io.github.shivam61.loadshedder.core;
/** Experimental RL Interface */
public interface LearningController {
    ControllerState chooseAction(ControlSnapshot snapshot);
    void observeReward(ControlSnapshot snapshot, Object action, double reward);
}
