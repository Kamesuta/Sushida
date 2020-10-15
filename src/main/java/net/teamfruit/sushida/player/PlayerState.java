package net.teamfruit.sushida.player;

public class PlayerState {
    private GameState session;

    public GameState getSession() {
        return session;
    }

    public boolean hasSession() {
        return session != null;
    }

    public void create() {
        if (session != null)
            return;
        session = new GameState();
        session.start();
    }

    public void destroy() {
        if (session != null)
            session.destroy();
        session = null;
    }
}
