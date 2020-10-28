package net.teamfruit.sushida.mode;

import net.md_5.bungee.api.ChatColor;
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
        return state.clearCount;
    }

    @Override
    public String getScoreString(StateContainer state) {
        return ChatColor.WHITE + "残り時間:"
                + (state.timer.getTime() < 10 ? ChatColor.YELLOW : ChatColor.GREEN)
                + ChatColor.BOLD + String.format("%.0f秒", getSetting(SettingTime) - state.timer.getTime())
                + ChatColor.GRAY + ", "
                + ChatColor.WHITE + "金額:"
                + ChatColor.GREEN + ChatColor.BOLD + String.format("%,d円", state.moneyCount);
    }
}
