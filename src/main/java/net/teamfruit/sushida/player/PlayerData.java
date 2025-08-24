package net.teamfruit.sushida.player;

import net.teamfruit.sushida.BelowNameManager;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.state.NoneState;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Optional;

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
        if (group.equals(this.group))
            return false;
        // ゲーム中
        if (hasSession())
            return false;
        if (group.owner.hasSession())
            return false;
        // 自分がオーナーな場合蹴る
        if (equals(this.group.owner))
            new ArrayList<>(this.group.getMembers()).forEach(PlayerData::leave);
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
            new ArrayList<>(this.group.getMembers()).forEach(PlayerData::leave);
        // グループ退出
        boolean b = this.group.getMembers().remove(this);
        if (this.group.getMembers().isEmpty())
            this.group.owner.leaveScoreboard();
        leaveScoreboard();
        this.group = new Group(this);
        return b;
    }

    public void joinScoreboard() {
        player.setScoreboard(group.getGroupScoreboard());
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
        if (!player.hasResourcePack())
            Sushida.resourcePack.apply(player);

        // 周囲にリソースパック
        // 周囲のプレイヤーが更新される1秒後以降に実行
        new BukkitRunnable() {
            @Override
            public void run() {
                SoundManager.resourcePackAround(player);
            }
        }.runTaskLaterAsynchronously(Sushida.plugin, 40);

        joinScoreboard();
        Sushida.belowName.spawn(this);
    }

    public void destroy() {
        if (session == null)
            return;
        session.apply(StateContainer.supply(NoneState::new));
        session = null;

        // チームをリセット
        Optional.ofNullable(group.getGroupScoreboard().getEntryTeam(player.getName())).ifPresent(e -> e.removeEntry(player.getName()));

        if (this.group.getMembers().isEmpty())
            leaveScoreboard();

        // デスポーン
        new BukkitRunnable() {
            @Override
            public void run() {
                Sushida.belowName.despawn(PlayerData.this);
            }
        }.runTask(Sushida.plugin);
    }
}
