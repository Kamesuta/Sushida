package net.teamfruit.sushida.listener;

import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // player変数を新しいエンティティに更新
        Sushida.logic.states.getPlayerState(event.getPlayer()).player = event.getPlayer();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // 部屋に参加者がいたらホストを変更
        PlayerData state = Sushida.logic.states.getPlayerState(event.getPlayer());
        Group group = state.getGroup();
        if (group.isOwner(state))
            group.getMembers().stream().findFirst().ifPresent(group::setOwner);
    }

}
