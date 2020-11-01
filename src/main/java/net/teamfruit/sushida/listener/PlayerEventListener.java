package net.teamfruit.sushida.listener;

import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // player変数を新しいエンティティに更新
        PlayerData state = Sushida.logic.states.getPlayerState(event.getPlayer());
        state.player = event.getPlayer();
        // ネーム生成
        new BukkitRunnable() {
            @Override
            public void run() {
                Sushida.belowName.spawn(state);
            }
        }.runTaskLater(Sushida.plugin, 10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // 部屋に参加者がいたらホストを変更
        PlayerData state = Sushida.logic.states.getPlayerState(event.getPlayer());
        Group group = state.getGroup();
        if (group.isOwner(state))
            group.getMembers().stream().findFirst().ifPresent(group::setOwner);
        // ネーム削除
        //Sushida.belowName.despawn(state);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        // ネーム削除
        Sushida.belowName.checkAndRemove(event.getEntity());
    }

}
