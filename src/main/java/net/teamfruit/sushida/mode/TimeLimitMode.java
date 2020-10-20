package net.teamfruit.sushida.mode;

import net.teamfruit.sushida.player.StateContainer;

public class TimeLimitMode implements GameMode {
    @Override
    public boolean isGameOver(StateContainer state) {
        return false;
    }

    @Override
    public int getScore(StateContainer state) {
        return 0;
    }
}
