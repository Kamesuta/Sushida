package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PauseState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        state.data.player.sendTitle("「/ 」", "スラッシュ＋スペースで続行", 0, 10000, 0);
        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        if ("".equals(typed))
            return new PlayState();
        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        if (state.bgmCount++ >= 4) {
            state.bgmCount = 0;
            player.playSound(player.getLocation(), "sushida:sushida.bgm", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
