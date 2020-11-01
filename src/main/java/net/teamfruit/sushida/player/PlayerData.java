package net.teamfruit.sushida.player;

import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.belowname.BelowNameManager;
import net.teamfruit.sushida.player.state.NoneState;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {
    public Player player;
    private StateContainer session;
    private Group group;

    public BelowNameManager.NameTagReference entity;

    public PlayerData(Player player) {
        this.player = player;
        this.group = new Group(this);
        this.entity = new BelowNameManager.NameTagReference();
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
        // ゲーム中
        if (hasSession())
            return false;
        if (group.owner.hasSession())
            return false;
        // 自分がオーナーな場合蹴る
        if (equals(getGroup().owner))
            getGroup().getMembers().forEach(PlayerData::leave);
        // グループ変更
        this.group.getMembers().remove(this);
        if (this.group.getMembers().isEmpty())
            this.group.owner.leaveScoreboard();
        leaveScoreboard();
        this.group = group;
        return this.group.getMembers().add(this);
    }

    public boolean leave() {
        // ゲーム中
        if (hasSession())
            return false;
        // 解散
        if (this.group.isOwner(this))
            this.group.getMembers().forEach(PlayerData::leave);
        // グループ退出
        boolean b = this.group.getMembers().remove(this);
        if (this.group.getMembers().isEmpty())
            this.group.owner.leaveScoreboard();
        leaveScoreboard();
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

        // リソースパック
        Sushida.resourcePack.apply(player);

        if (!getGroup().getMembers().isEmpty())
            joinScoreboard(getGroup().getGroupScoreboard());
        Sushida.belowName.spawn(this);
    }

    public void destroy() {
        if (session == null)
            return;
        session.apply(StateContainer.supply(NoneState::new));
        session = null;
        Sushida.belowName.despawn(this);
    }
}
