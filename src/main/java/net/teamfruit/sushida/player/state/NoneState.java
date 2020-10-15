package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.player.StateContainer;

public class NoneState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        state.data.player.sendTitle("", "", 0, 0, 0);
        return null;
    }
}
