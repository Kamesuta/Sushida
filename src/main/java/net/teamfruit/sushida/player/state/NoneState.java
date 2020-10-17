package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class NoneState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        player.sendTitle("", "", 0, 0, 0);

        player.stopSound("sushida:sushida.op", SoundCategory.RECORDS);
        player.stopSound("sushida:sushida.bgm", SoundCategory.RECORDS);

        return null;
    }
}
