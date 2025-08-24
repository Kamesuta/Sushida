package net.teamfruit.sushida.util;

/**
 * A hires' timer. This measures time in seconds as floating point values.
 * All Timers created are updated simultaneously by calling the static method
 * tick(). This ensures that within a single iteration of a game loop that
 * all timers are updated consistently with each other.
 *
 * @author cix_foo <cix_foo@users.sourceforge.net>
 * @version $Revision$
 * $Id$
 */
public class Timer {

    // Timer resolution
    private static final long resolution = 1000;

    // When the timer was started
    private long startTime;

    // The last time recorded by getTime()
    private long lastTime;

    // Whether the timer is paused
    private boolean paused;

    /**
     * Constructs a timer. The timer will be reset to 0.0 and resumed immediately.
     */
    public Timer() {
        reset();
        resume();
    }

    /**
     * @return the time in seconds, as a float
     */
    public float getTime() {
        if (!paused) {
            lastTime = currentTime() - startTime;
        }

        return (float) ((double) lastTime / (double) resolution);
    }

    /**
     * Pause the timer. Whilst paused the time will not change for this timer
     * when tick() is called.
     *
     * @see #resume()
     */
    public void pause() {
        if (!paused) {
            lastTime = currentTime() - startTime;
        }

        paused = true;
    }

    /**
     * Reset the timer. Equivalent to set(0.0f);
     *
     * @see #set(float)
     */
    public void reset() {
        set(0.0f);
    }

    /**
     * Resume the timer.
     *
     * @see #pause()
     */
    public void resume() {
        if (paused) {
            startTime = currentTime() - lastTime;
        }

        paused = false;
    }

    /**
     * Set the time of this timer
     *
     * @param newTime the new time, in seconds
     */
    public void set(float newTime) {
        long newTimeInTicks = (long) ((double) newTime * (double) resolution);
        startTime = currentTime() - newTimeInTicks;
        lastTime = newTimeInTicks;
    }

    /**
     * Get the next time update from the system's hires timer.
     *
     * @return time
     */
    public static long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Debug output.
     */
    public String toString() {
        return "Timer[Time=" + getTime() + ", Paused=" + paused + "]";
    }
}