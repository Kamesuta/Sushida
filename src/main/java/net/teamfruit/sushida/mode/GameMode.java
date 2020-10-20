package net.teamfruit.sushida.mode;

import net.teamfruit.sushida.player.StateContainer;

import java.util.List;
import java.util.Map;

public interface GameMode {
    List<GameSettingType> getSettingTypes();
    Map<GameSettingType, Integer> getSetting();

    boolean isGameOver(StateContainer state);

    int getScore(StateContainer state);
}
