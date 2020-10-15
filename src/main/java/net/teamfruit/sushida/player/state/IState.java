package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.player.StateContainer;

public interface IState {
    default IState onEnter(StateContainer state) {
        return this;
    }

    default void onExit(StateContainer state) {
    }

    default IState onCommand(StateContainer state, String command) {
        return this;
    }

    default IState onType(StateContainer state, char typed) {
        return this;
    }
}
