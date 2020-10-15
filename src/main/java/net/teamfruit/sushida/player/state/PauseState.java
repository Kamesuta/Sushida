package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.player.StateContainer;

public class PauseState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        state.data.player.sendTitle("一時停止", "「/ 」スラッシュを押してからスペースを押すと再開します", 0, 10000, 0);
        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        if ("".equals(typed))
            return new PlayState();
        return null;
    }
}
