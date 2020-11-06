package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class CountdownState implements IState {
    private int countdown = 6;

    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        // シングルプレイのときは飛ばす
        if (state.data.getGroup().getMembers().isEmpty()) {
            SoundManager.playSoundAround(player, "sushida:sushida.whistle1", SoundCategory.PLAYERS, 1, 1);

            return new PlayState();
        }

        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        countdown--;
        if (countdown <= 0) {
            SoundManager.playSoundAround(player, "sushida:sushida.whistle1", SoundCategory.PLAYERS, 1, 1);

            return new PlayState();
        } else {
            SoundManager.playSoundAround(player, "sushida:sushida.kclick", SoundCategory.PLAYERS, 1, 1);
        }

        player.sendTitle(new Title(
                new ComponentBuilder(String.format("%d 秒前", countdown)).bold(true).color(ChatColor.YELLOW).create(),
                new ComponentBuilder("まもなくスタートします").bold(false).color(ChatColor.GREEN).create(),
                5, 10, 5));

        return null;
    }
}
