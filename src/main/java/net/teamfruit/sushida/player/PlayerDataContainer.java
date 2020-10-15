package net.teamfruit.sushida.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataContainer {

    private final Map<Player, PlayerData> data = new HashMap<>();

    public PlayerData getPlayerState(Player player) {
        return data.computeIfAbsent(player, e -> new PlayerData(player));
    }

    public void removeOfflinePlayers() {
        data.entrySet().removeIf(e -> {
            if (!e.getKey().isOnline()) {
                e.getValue().destroy();
                return true;
            }
            return false;
        });
    }

    public Collection<PlayerData> getPlayers() {
        return data.values();
    }

}
