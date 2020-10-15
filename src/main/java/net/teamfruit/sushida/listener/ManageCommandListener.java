package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.logic.GameLogic;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ManageCommandListener implements CommandExecutor, TabCompleter {

    private final GameLogic logic;

    public ManageCommandListener(GameLogic logic) {
        this.logic = logic;
    }

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

        sender.sendMessage("");
        sender.sendMessage(new ComponentBuilder()
                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                .append(new ComponentBuilder("寿司打").bold(true).create()).color(ChatColor.GREEN)
                .create()
        );
        sender.sendMessage("");
        sender.sendMessage(new ComponentBuilder()
                .append("画面中央の文字の指示に従ってください").color(ChatColor.GREEN)
                .append("※快適なプレイのために一度チャット画面を閉じ、").color(ChatColor.GREEN)
                .append(new ComponentBuilder("【F3+D】").bold(true).create()).color(ChatColor.GRAY)
                .append("を押すことを推奨します").color(ChatColor.GREEN)
                .create()
        );
        sender.sendMessage("");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
    
}
