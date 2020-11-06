package net.teamfruit.sushida;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SoundManager {
    public static Map<Player, List<Player>> nearbyPlayers = new HashMap<>();

    public static void playSound(Player player, String sound, SoundCategory category, float volume, float pitch) {
        Location loc = player.getLocation();
        player.playSound(loc, sound, category, volume, pitch);
    }

    public static void playSoundAround(Player player, String sound, SoundCategory category, float volume, float pitch) {
        Location loc = player.getLocation();
        player.playSound(loc, sound, category, volume, pitch);
        Optional.ofNullable(nearbyPlayers.get(player)).ifPresent(e -> e.forEach(f -> f.playSound(loc, sound, category, volume / 2, pitch)));
    }
}
