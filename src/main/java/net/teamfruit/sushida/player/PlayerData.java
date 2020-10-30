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

    public boolean join(Group group) {
        // 自分のグループ
        if (equals(group.owner))
            return false;
        // すでに参加しているグループ
        if (group.equals(getGroup()))
            return false;
        // 自分がオーナーな場合蹴る
        if (equals(getGroup().owner))
            getGroup().getMembers().forEach(PlayerData::leave);
        leave();
        this.group = group;
        return this.group.getMembers().add(this);
    }

    public boolean leave() {
        boolean b = this.group.getMembers().remove(this);
        this.group = new Group(this);
        return b;
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
