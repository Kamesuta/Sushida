package net.teamfruit.sushida.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataContainer {

    private final Map<UUID, PlayerData> data = new HashMap<>();

    public PlayerData getPlayerState(Player player) {
        return data.computeIfAbsent(player.getUniqueId(), e -> new PlayerData(player));
    }

    public void removeOfflinePlayers() {
        data.values().removeIf(e -> {
            if (!e.player.isOnline()) {
                e.destroy();
                return true;
            }
            return false;
        });
    }

    public Collection<PlayerData> getPlayers() {
        return data.values();
    }

}
