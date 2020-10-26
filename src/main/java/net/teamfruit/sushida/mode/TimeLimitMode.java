package net.teamfruit.sushida.mode;

import net.teamfruit.sushida.player.StateContainer;

import java.util.*;

public class TimeLimitMode implements GameMode {
    public static final GameSettingType SettingTime = new GameSettingType("time", "制限時間", 60, Arrays.asList(60, 90, 120));

    private final Map<GameSettingType, Integer> settings = new HashMap<>();

    @Override
    public String title() {
        return "時間制";
    }

    @Override
    public List<GameSettingType> getSettingTypes() {
        return Collections.singletonList(SettingTime);
    }

    @Override
    public Map<GameSettingType, Integer> getSettings() {
        return settings;
    }

    @Override
    public boolean isGameOver(StateContainer state) {
        return state.timer.getTime() > getSetting(SettingTime);
    }

    @Override
    public int getScore(StateContainer state) {
        return state.typingLogic.wordDoneCount();
    }
}
