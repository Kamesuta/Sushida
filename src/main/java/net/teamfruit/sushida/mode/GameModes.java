package net.teamfruit.sushida.mode;

public enum GameModes {
    stock(new TimeAttackMode()),
    time(new TimeLimitMode()),
    ;

    public final GameMode mode;

    GameModes(GameMode mode) {
        this.mode = mode;
    }

    public static GameModes fromName(String name) {
        if (name == null)
            return null;
        try {
            return valueOf(name.toLowerCase());
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }
}
