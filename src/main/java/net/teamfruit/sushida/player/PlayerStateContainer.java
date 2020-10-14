package net.teamfruit.sushida.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerStateContainer {

    private final Map<Player, PlayerState> data = new HashMap<>();

    public PlayerState getPlayerState(Player player) {
        return data.computeIfAbsent(player, e -> new PlayerState());
    }

}
