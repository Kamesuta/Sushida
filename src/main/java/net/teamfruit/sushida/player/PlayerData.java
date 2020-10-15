package net.teamfruit.sushida.player;

import net.teamfruit.sushida.player.state.NoneState;
import net.teamfruit.sushida.player.state.PlayState;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.entity.Player;

public class PlayerData {
    private Player player;
    private StateContainer session;

    public PlayerData(Player player) {
        this.player = player;
    }

    public StateContainer getSession() {
        return session;
    }

    public boolean hasSession() {
        return session != null;
    }

    public void create() {
        if (session != null)
            return;
        session = new StateContainer(this);
        session.apply(StateContainer.supply(PlayState::new));
    }

    public void destroy() {
        if (session != null)
            session.apply(StateContainer.supply(NoneState::new));
        session = null;
    }
}
