package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ManageCommandListener implements CommandExecutor, TabCompleter {

    private String get(List<String> args, int index) {
        if (args.size() > index)
            return args.get(index);
        return null;
    }

    private List<String> getFrom(List<String> args, int index) {
        if (args.size() > index)
            return args.subList(index, args.size());
        return Collections.emptyList();
    }

    private boolean validateGroupOwner(PlayerData state) {
        if (!state.getGroup().hasPermission(state.player)) {
            state.player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("グループのホストのみがメンバーを追加できます").color(ChatColor.RED)
                    .create()
            );
            return false;
        }
        return true;
    }

    private List<Player> getPlayers(CommandSender sender, List<String> args) {
        List<Player> players = new ArrayList<>();
        if (args.size() > 0) {
            if ("@a".equals(args.get(0)))
                players.addAll(Bukkit.getOnlinePlayers());
            else
                players.addAll(args.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList()));
        } else {
            players.add((Player) sender);
        }
        return players;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("プレイヤーのみ実行可能です。").color(ChatColor.RED)
                    .create()
            );
            return true;
        }
        Player player = (Player) sender;
        PlayerData state = Sushida.logic.states.getPlayerState(player);

        List<String> args = Arrays.asList(arg);
        String arg0 = get(args, 0);
        if ("assign".equals(arg0)) {
            if (!validateGroupOwner(state))
                return true;
            if (!player.hasPermission("sushida.other")) {
                player.sendMessage(new ComponentBuilder()
                        .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                        .append("他人の強制追加するためには権限が足りません").color(ChatColor.RED)
                        .create()
                );
                return true;
            }
            List<Player> players = getPlayers(player, getFrom(args, 1));
            players.stream().map(Sushida.logic.states::getPlayerState).forEach(e -> e.getGroup().addMember(e));
            player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append(players.size() + "人").color(ChatColor.WHITE)
                    .append("を参加させました").color(ChatColor.GREEN)
                    .create()
            );
        } else if ("kick".equals(arg0)) {
            if (!validateGroupOwner(state))
                return true;
            List<Player> players = getPlayers(player, getFrom(args, 1));
            players.stream().map(Sushida.logic.states::getPlayerState).forEach(e -> e.getGroup().removeMember(e));
            player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append(players.size() + "人").color(ChatColor.WHITE)
                    .append("を退出させました").color(ChatColor.GREEN)
                    .create()
            );
        } else if ("join".equals(arg0)) {
            String arg1 = get(args, 0);
            if (arg1 == null) {
                player.sendMessage(new ComponentBuilder()
                        .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                        .append("/sushida join <player>").color(ChatColor.RED)
                        .create()
                );
                return true;
            }
            Player groupPlayer = Bukkit.getPlayer(arg1);
            if (groupPlayer == null) {
                player.sendMessage(new ComponentBuilder()
                        .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                        .append("プレイヤーが見つかりません").color(ChatColor.RED)
                        .create()
                );
                return true;
            }
            Group group = Sushida.logic.states.getPlayerState(groupPlayer).getGroup();
            state.join(group);
            player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append(groupPlayer.getName()).color(ChatColor.WHITE)
                    .append("に参加しました").color(ChatColor.GREEN)
                    .create()
            );
        } else if ("leave".equals(arg0)) {
            player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append(state.getGroup().owner.player.getName()).color(ChatColor.WHITE)
                    .append("から退出しました").color(ChatColor.GREEN)
                    .create()
            );
            state.leave();
        } else if ("word".equals(arg0)) {
            if (!validateGroupOwner(state))
                return true;

        } else if ("start".equals(arg0)) {
            List<Player> players = getPlayers(player, getFrom(args, 1));
            for (Player player : players) {
                PlayerData state = Sushida.logic.states.getPlayerState(player);
                if (state.hasSession())
                    state.destroy();
                state.create();
            }
        } else if ("stop".equals(arg0)) {
            List<Player> players = getPlayers(player, getFrom(args, 1));
            if (players == null)
                return true;

            for (Player player : players) {
                PlayerData state = Sushida.logic.states.getPlayerState(player);
                if (state.hasSession()) {
                    state.destroy();
                    player.sendMessage("寿司打を終了しました。");
                }
            }
        }



        for (Player player : players) {
            PlayerData state = Sushida.logic.states.getPlayerState(player);
            if (state.hasSession()) {
                state.destroy();
                player.sendMessage("寿司打を終了しました。");
            } else {
                //player.sendMessage("寿司打を開始しました。");
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
