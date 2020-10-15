package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.PlayerData;
import net.teamfruit.sushida.player.StateContainer;
import net.teamfruit.sushida.player.state.NoneState;
import net.teamfruit.sushida.player.state.PauseState;
import net.teamfruit.sushida.player.state.PlayState;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommandListener implements CommandExecutor {

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

        PlayerData playerState = Sushida.logic.states.getPlayerState(player);
        if (!playerState.hasSession()) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("寿司打を始めるためには").color(ChatColor.GREEN)
                    .append(new ComponentBuilder(" /sushida ").color(ChatColor.GRAY).bold(true).create())
                    .append("と入力してください").color(ChatColor.GREEN)
                    .create()
            );
            return true;
        }

        playerState.getSession().apply(StateContainer.changed(PlayState.class, StateContainer.supply(PauseState::new)));

        player.sendTitle("一時停止", "再開するには「/ (スラッシュ・スペース)」と入力してください。", 0, 100, 0);

        return true;
    }

}
