package com.cerbon.bosses_of_mass_destruction.client.render;

import com.cerbon.bosses_of_mass_destruction.animation.IAnimationTimer;

public class FrameLimiter {
    private final double minimumFrameDelta;
    private final IAnimationTimer timer;
    private double previousTime = 0.0;

    public FrameLimiter(float framesPerUnit, IAnimationTimer timer) {
        this.minimumFrameDelta = 1 / framesPerUnit;
        this.timer = timer;
    }

    public boolean canDoFrame() {
        double currentTick = timer.getCurrentTick();
        double frameDelta = currentTick - previousTime;
        if (frameDelta >= minimumFrameDelta) {
            previousTime = currentTick;
            return true;
        }
        return false;
    }
}

