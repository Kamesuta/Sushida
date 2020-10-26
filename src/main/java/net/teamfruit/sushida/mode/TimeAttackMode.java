package net.teamfruit.sushida.mode;

import net.teamfruit.sushida.player.StateContainer;

import java.util.*;

public class TimeAttackMode implements GameMode {
    public static final GameSettingType SettingCount = new GameSettingType("count", "問題数", 30, Arrays.asList(10, 20, 30, 60, 120));

    private final Map<GameSettingType, Integer> settings = new HashMap<>();

    @Override
    public String title() {
        return "タイムアタック";
    }

    @Override
    public List<GameSettingType> getSettingTypes() {
        return Collections.singletonList(SettingCount);
    }

    @Override
    public Map<GameSettingType, Integer> getSettings() {
        return settings;
    }

    @Override
    public boolean isGameOver(StateContainer state) {
        return state.typingLogic.wordRemainingCount() <= 0;
    }

    @Override
    public int getScore(StateContainer state) {
        return (int) state.timer.getTime();
    }
}
