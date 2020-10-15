package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.logic.GameLogic;
import net.teamfruit.sushida.player.PlayerState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommandListener implements CommandExecutor {

    private final GameLogic logic;

    public GameCommandListener(GameLogic logic) {
        this.logic = logic;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("プレイヤーのみ実行可能です。").color(ChatColor.RED)
                    .create()
            );
            return true;
        }
        Player player = (Player) sender;

        PlayerState playerState = logic.states.getPlayerState(player);
        playerState.create();
        if (!playerState.hasSession() || playerState.getSession().isS) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("寿司打を始めるためには").color(ChatColor.GREEN)
                    .append(new ComponentBuilder(" /sushida ").color(ChatColor.GRAY).bold(true).create())
                    .append("と入力してください").color(ChatColor.GREEN)
                    .create()
            );
            return true;
        }

        PlayerState state = logic.states.getPlayerState(player);
        state.pause();

        player.sendTitle("一時停止", "再開するには「/ (スラッシュ・スペース)」と入力してください。", 0, 100, 0);

        return true;
    }

}
