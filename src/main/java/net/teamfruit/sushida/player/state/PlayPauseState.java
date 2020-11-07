package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlayPauseState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        player.sendTitle(new Title(
                new ComponentBuilder("「/␣」").bold(true).color(ChatColor.GREEN).create(),
                new ComponentBuilder("スラッシュ＋スペースで続行").bold(false).color(ChatColor.GREEN).create(),
                0, 10000, 0));

        SoundManager.playSound(player, "sushida:sushida.poke", SoundCategory.PLAYERS, 1, 1);

        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed, String buffer) {
        Player player = state.data.player;

        if ("".equals(typed)) {
            SoundManager.playSound(player, "sushida:sushida.open", SoundCategory.PLAYERS, 1, 1);

            return new PlayState();
        }

        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        if (state.data.getGroup().getMode().isGameOver(state))
            return new ResultWaitState();

        if (state.bgmCount++ >= 4) {
            state.bgmCount = 0;
            SoundManager.playSound(player, "sushida:sushida.bgm", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
