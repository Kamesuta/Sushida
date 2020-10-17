package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlayState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        onType(state, "");

        return null;
    }

    @Override
    public void onExit(StateContainer state) {
        Player player = state.data.player;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        Player player = state.data.player;

        if (state.typingLogic.type(typed)) {
            // OK
            player.playSound(player.getLocation(), "sushida:sushida.k", SoundCategory.PLAYERS, 1, 1);
        } else {
            // NG
            player.playSound(player.getLocation(), "sushida:sushida.miss", SoundCategory.PLAYERS, 1, 1);
        }

        if (state.typingLogic.isNextTiming()) {
            // Next
            player.playSound(player.getLocation(), "sushida:sushida.coin", SoundCategory.PLAYERS, 1, 1);
        }

        player.sendTitle(new Title(
                new ComponentBuilder()
                        .append(state.typingLogic.getRequiredKanji()).color(ChatColor.GREEN)
                        .create(),
                new ComponentBuilder()
                        .append(state.typingLogic.getRequiredHiragana().substring(0, state.typingLogic.getRequiredHiragana().length() - state.typingLogic.getRemainingRequiredHiraganaVisual().length())).color(ChatColor.WHITE)
                        .append(state.typingLogic.getRemainingRequiredHiraganaVisual()).color(ChatColor.GRAY)
                        .create(),
                0, 10000, 0));

        player.sendActionBar(ChatColor.WHITE + state.typingLogic.getTypedTotalRomaji() + ChatColor.GRAY + state.typingLogic.getTypedRomaji());

        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PauseState();
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        player.sendActionBar(ChatColor.WHITE + state.typingLogic.getTypedTotalRomaji() + ChatColor.GRAY + state.typingLogic.getTypedRomaji());

        if (state.bgmCount++ >= 4) {
            state.bgmCount = 0;
            player.playSound(player.getLocation(), "sushida:sushida.bgm", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
