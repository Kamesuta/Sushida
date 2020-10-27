package net.teamfruit.sushida.mode;

import net.md_5.bungee.api.ChatColor;
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

    @Override
    public String getScoreString(StateContainer state) {
        return ChatColor.WHITE + "経過時間:"
                + ChatColor.GREEN + ChatColor.BOLD + String.format("%.0f秒", state.timer.getTime())
                + ChatColor.GRAY + ", "
                + ChatColor.WHITE + "残り枚数:"
                + ChatColor.GREEN + ChatColor.BOLD + String.format("%d皿", state.typingLogic.wordRemainingCount());
    }
}
