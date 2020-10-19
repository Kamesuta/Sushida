package net.teamfruit.sushida.player.state;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.entity.Player;

public class ResultState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        state.timer.pause();

        player.sendMessage(new ComponentBuilder()
                .append("▼結果").color(ChatColor.GOLD)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("完了: ").color(ChatColor.WHITE)
                .append(String.valueOf(state.doneCount)).color(ChatColor.YELLOW)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("ミス: ").color(ChatColor.WHITE)
                .append(String.valueOf(state.missCount)).color(ChatColor.YELLOW)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("スコア: ").color(ChatColor.WHITE)
                .append(String.valueOf(state.scoreCount)).color(ChatColor.YELLOW)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("タイム").color(ChatColor.WHITE)
                .append(String.valueOf(state.timer.getTime())).append("秒").color(ChatColor.YELLOW)
                .create()
        );

        return null;
    }
}
