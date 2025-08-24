package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.PlayerData;
import net.teamfruit.sushida.player.StateContainer;
import net.teamfruit.sushida.player.state.IState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GameCommandListener implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[寿司打プラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("プレイヤーのみ実行可能です。").color(ChatColor.RED)
                    .create()
            );
            return true;
        }
        Player player = (Player) sender;

        PlayerData state = Sushida.logic.states.getPlayerState(player);

        if (!state.hasSession()) {
            player.sendMessage(new ComponentBuilder()
                    .append("[寿司打プラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("寿司打を始めるためには").color(ChatColor.GREEN)
                    .append(new ComponentBuilder(" /sushida ").color(ChatColor.GRAY).bold(true).create())
                    .append("と入力してください").color(ChatColor.GREEN)
                    .create()
            );
            return true;
        }

        StateContainer session = state.getSession();
        session.inputCursor = 0;
        session.apply(IState::onPause);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return Collections.emptyList();
    }

}
