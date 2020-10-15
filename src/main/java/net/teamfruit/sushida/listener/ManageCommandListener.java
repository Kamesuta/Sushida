package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class ManageCommandListener implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        List<Player> players = new ArrayList<>();
        if (args.length > 0) {
            if (!sender.hasPermission("sushida.other")) {
                sender.sendMessage(new ComponentBuilder()
                        .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                        .append("他人のゲームを操作するためには権限が足りません").color(ChatColor.RED)
                        .create()
                );
                return true;
            }
            String target = args[0];
            if ("@a".equals(target))
                players.addAll(Bukkit.getOnlinePlayers());
            else
                Optional.ofNullable(Bukkit.getPlayer(target)).ifPresent(players::add);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(new ComponentBuilder()
                        .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                        .append("プレイヤーのみ実行可能です。").color(ChatColor.RED)
                        .create()
                );
                return true;
            }
            players.add((Player) sender);
        }

        for (Player player : players) {
            PlayerData state = Sushida.logic.states.getPlayerState(player);
            if (state.hasSession()) {
                state.destroy();
                player.sendMessage("寿司打を終了しました。");
            } else {
                player.sendMessage("寿司打を開始しました。");
                state.create();
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1)
            return Arrays.asList("start", "exit");
        return Collections.emptyList();
    }

}
