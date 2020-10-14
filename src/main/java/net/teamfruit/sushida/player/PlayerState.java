package net.teamfruit.sushida.player;

public class PlayerState {
    public GameState session;

    public boolean isStarted() {
        return session != null;
    }

    public void start() {
        session = new GameState();
    }

    public void finish() {
        session = null;
    }
}
