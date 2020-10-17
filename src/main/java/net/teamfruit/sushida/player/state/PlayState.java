package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.player.StateContainer;
import org.apache.commons.lang.StringUtils;

public class PlayState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        onType(state, "");
        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        if (state.typingLogic.type(typed)) {
            // OK
        } else {
            // NG
        }

        state.data.player.sendTitle(new Title(
                new ComponentBuilder()
                        .append(state.typingLogic.getRequiredKanji()).color(ChatColor.GREEN)
                        .create(),
                new ComponentBuilder()
                        .append(StringUtils.substringBefore(state.typingLogic.getRequiredHiragana(), state.typingLogic.getRemainingRequiredHiragana())).color(ChatColor.WHITE)
                        .append(state.typingLogic.getRemainingRequiredHiragana()).color(ChatColor.GRAY)
                        .create(),
                0, 10000, 0));

        state.data.player.sendActionBar(ChatColor.WHITE + state.typingLogic.getTypedTotalRomaji() + ChatColor.GRAY + state.typingLogic.getTypedRomaji());

        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PauseState();
    }

    @Override
    public IState onTick(StateContainer state) {
        state.data.player.sendActionBar(ChatColor.WHITE + state.typingLogic.getTypedTotalRomaji() + ChatColor.GRAY + state.typingLogic.getTypedRomaji());

        return null;
    }
}
