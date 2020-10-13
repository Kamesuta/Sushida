package net.teamfruit.sushida;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Sushida extends JavaPlugin implements Listener {
    public Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onType(AsyncTabCompleteEvent event) {
        logger.log(Level.INFO, "async complete: " + event.getBuffer());
    }

    @EventHandler
    public void onType(TabCompleteEvent event) {
        logger.log(Level.INFO, "complete: " + event.getBuffer());
    }

    @EventHandler
    public void onType(PlayerCommandSendEvent event) {
        logger.log(Level.INFO, "player cmd send: " + event.getCommands());
    }

}
