package net.teamfruit.sushida.listener;

import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.stream.Collectors;

public class TickEventGenerator extends BukkitRunnable {
    @Override
    public void run() {
        Set<Player> playing = Sushida.logic.states.getPlayers().stream()
                .filter(PlayerData::hasSession)
                .map(e -> e.player)
                .collect(Collectors.toSet());
        SoundManager.nearbyPlayers = playing.stream()
                .collect(Collectors.toMap(e -> e,
                        e -> e.getWorld().getNearbyPlayers(e.getLocation(), 16, f -> !playing.contains(f))
                ));
    }
}
