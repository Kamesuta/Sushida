package net.teamfruit.sushida.player;

import net.teamfruit.sushida.player.state.NoneState;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.entity.Player;

public class PlayerData {
    public final Player player;
    private StateContainer session;
    private Group group;

    public PlayerData(Player player) {
        this.player = player;
        this.group = new Group(this);
    }

    public Group getGroup() {
        return group;
    }

    public void join(Group group) {
        this.group.getMembers().forEach(PlayerData::leave);
        this.group = group;
        this.group.addMember(this);
    }

    public void leave() {
        this.group.removeMember(this);
        this.group = new Group(this);
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
        session.apply(StateContainer.supply(TitleState::new));
    }

    public void destroy() {
        if (session != null)
            session.apply(StateContainer.supply(NoneState::new));
        session = null;
    }
}
