package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.player.StateContainer;

public interface IState {
    default IState onEnter(StateContainer state) {
        return null;
    }

    default void onExit(StateContainer state) {
    }

    default IState onPause(StateContainer state) {
        return null;
    }

    default IState onType(StateContainer state, String typed, String buffer) {
        return null;
    }

    default IState onReady(StateContainer state, int total, int ready) {
        return null;
    }

    default IState onTick(StateContainer state) {
        return null;
    }
}
