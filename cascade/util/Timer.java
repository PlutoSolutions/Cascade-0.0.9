/*
 * Decompiled with CFR 0.150.
 */
package cascade.util;

import cascade.util.Util;

public class Timer
implements Util {
    private long time = -1L;
    private final long current;
    long startTime = System.currentTimeMillis();
    long delay = 0L;
    boolean paused = false;

    public Timer() {
        this.current = -1L;
    }

    public final boolean hasReached(long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public boolean passedS(double s) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(s * 1000.0);
    }

    public boolean passedM(double m) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(m * 1000.0 * 60.0);
    }

    public boolean passedDms(double dms) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(dms * 10.0);
    }

    public boolean passedDs(double ds) {
        return this.getMs(System.nanoTime() - this.time) >= (long)(ds * 100.0);
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - ms * 1000000L;
    }

    public boolean isPassed() {
        return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public long getTime() {
        return this.time;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public static float getTimer() {
        return Timer.mc.timer.tickLength;
    }

    public static void setTimer(float speed) {
        Timer.mc.timer.tickLength = 50.0f / speed;
    }

    public static void resetTimer() {
        Timer.mc.timer.tickLength = 50.0f;
    }

    public boolean passedMs(long ms) {
        return this.getMs(System.nanoTime() - this.time) >= ms;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public void reset() {
        this.time = System.nanoTime();
    }

    public long getMs(long time) {
        return time / 1000000L;
    }
}

