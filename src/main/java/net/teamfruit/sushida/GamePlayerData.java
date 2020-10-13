package net.teamfruit.sushida;

public class GamePlayerData {
    public GamePlayerSession session;

    public boolean isStarted() {
        return session != null;
    }

    public void start() {
        session = new GamePlayerSession();
    }

    public void finish() {
        session = null;
    }
}
