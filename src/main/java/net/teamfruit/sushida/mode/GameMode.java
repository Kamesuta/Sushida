package net.teamfruit.sushida.mode;

import com.google.common.collect.ImmutableList;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.StateContainer;

import java.util.*;
import java.util.function.Consumer;

public interface GameMode {
    GameSettingType SettingTimeout = new GameSettingType("timeout", "秒数", "一問に対する制限時間", 6, Arrays.asList(6, -1));

    String title();

    List<GameSettingType> getSettingTypes();

    default void setSetting(Map<GameSettingType, Integer> settings, GameSettingType settingType, int value) {
        settings.put(settingType, value);
    }

    default int getSetting(Map<GameSettingType, Integer> settings, GameSettingType settingType) {
        return settings.getOrDefault(settingType, settingType.defaultValue);
    }

    default GameSettingType getSettingType(String name) {
        return getSettingTypes().stream()
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElse(null);
    }

    boolean isGameOver(StateContainer state);

    String getScoreBelowName(StateContainer state);

    default int getDynamicScore(StateContainer state) {
        return getScore(state);
    }

    int getScore(StateContainer state);

    default Comparator<Integer> getScoreComparator() {
        return Comparator.reverseOrder();
    }

    String getScoreString(StateContainer state);

    Iterator<Consumer<StateContainer>> getResultMessageTasks();

    ImmutableList<Map.Entry<String, String>> getWords(Group group);
}
