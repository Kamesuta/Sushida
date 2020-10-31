package net.teamfruit.sushida.player;

import net.teamfruit.sushida.player.state.NoneState;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

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
        // グループ変更
        this.group.getMembers().remove(this);
        this.group = group;
        return this.group.getMembers().add(this);
    }

    public boolean leave() {
        // グループ退出
        boolean b = this.group.getMembers().remove(this);
        this.group = new Group(this);
        return b;
    }

    public void joinScoreboard(Scoreboard newScoreboard) {
        player.setScoreboard(newScoreboard);
    }

    public void leaveScoreboard() {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
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
        if (!getGroup().getMembers().isEmpty())
            joinScoreboard(getGroup().getGroupScoreboard());
    }

    public void destroy() {
        if (session == null)
            return;
        leaveScoreboard();
        session.apply(StateContainer.supply(NoneState::new));
        session = null;
    }
}
