package net.teamfruit.sushida.player;

import net.teamfruit.sushida.util.Timer;

public class GameState {
    private Timer timer;

    public int score;

    public GameState() {
        timer = new Timer();
        timer.pause();
        timer.reset();
    }

    public enum State {
        CREATED,
        STARTED,
        PAUSED,
        FINISHED,
        RESULT,
    }

    public void create() {

    }

    public void destroy() {

    }

    public boolean isStarted() {
        timer.reset();
        timer.resume();
    }

    public void start() {
        timer.reset();
        timer.resume();
    }

    public void finish() {
        timer.pause();
    }

    public boolean isPause() {
        return timer.isPaused();
    }

    public void pause() {
        timer.pause();
    }

    public void resume() {
        timer.resume();
    }
}
