package net.teamfruit.sushida;

import org.bukkit.entity.Player;

public class ResourcePack {
    public final String url;
    public final String hash;

    public ResourcePack(String url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    public void apply(Player player) {
        if (url != null && hash != null)
            player.setResourcePack(url, hash);
    }
}
