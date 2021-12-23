/*
 * Decompiled with CFR 0.150.
 */
package cascade.manager;

import cascade.features.Feature;

public class TimerManager
extends Feature {
    private float timer = 1.0f;
    private int usages;

    public void unload() {
        this.timer = 1.0f;
        TimerManager.mc.timer.tickLength = 50.0f;
    }

    public void setTimer(float timer) {
        if (timer > 0.0f) {
            TimerManager.mc.timer.tickLength = 50.0f / timer;
        }
    }

    public float getTimer() {
        return this.timer;
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
        TimerManager.mc.timer.tickLength = 50.0f;
    }
}

