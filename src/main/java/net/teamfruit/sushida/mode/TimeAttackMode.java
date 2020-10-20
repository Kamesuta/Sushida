package net.teamfruit.sushida.mode;

import net.teamfruit.sushida.player.StateContainer;

import java.util.*;

public class TimeAttackMode implements GameMode {
    public static final GameSettingType SettingCount = new GameSettingType("count", "問題数", Arrays.asList(10, 20, 60));

    private final Map<GameSettingType, Integer> settings = new HashMap<>();

    @Override
    public List<GameSettingType> getSettingTypes() {
        return Collections.singletonList(SettingCount);
    }

    @Override
    public Map<GameSettingType, Integer> getSetting() {
        return settings;
    }

    @Override
    public boolean isGameOver(StateContainer state) {
        return false;
    }

    @Override
    public int getScore(StateContainer state) {
        return 0;
    }
}
