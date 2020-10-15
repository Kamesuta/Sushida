package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.logic.GameLogic;
import net.teamfruit.sushida.player.PlayerState;

public interface IState {
    default IState onInit(GameLogic logic, PlayerState state) { return this; }
    default IState onCommand(GameLogic logic, PlayerState state, String command) { return this; }
    default IState onType(GameLogic logic, PlayerState state, char typed) { return this; }
}
