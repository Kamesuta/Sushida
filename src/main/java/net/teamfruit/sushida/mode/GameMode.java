package net.teamfruit.sushida.mode;

import net.teamfruit.sushida.player.StateContainer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GameMode {
    String title();

    List<GameSettingType> getSettingTypes();

    Map<GameSettingType, Integer> getSettings();

    default void setSetting(GameSettingType settingType, int value) {
        getSettings().put(settingType, value);
    }

    default int getSetting(GameSettingType settingType) {
        return getSettings().getOrDefault(settingType, settingType.defaultValue);
    }

    default GameSettingType getSettingType(String name) {
        return getSettingTypes().stream()
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElse(null);
    }

    boolean isGameOver(StateContainer state);

    int getScore(StateContainer state);

    String getScoreString(StateContainer state);
}
