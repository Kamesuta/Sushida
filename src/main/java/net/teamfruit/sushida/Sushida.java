package net.teamfruit.sushida;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Sushida extends JavaPlugin {
    public Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        ManageCommandListener manageCommand = new ManageCommandListener(this);
        GameLogic gameLogic = new GameLogic(this);

        // Event
        getServer().getPluginManager().registerEvents(gameLogic, this);

        // Command
        getCommand("sushida").setExecutor(manageCommand);
        getCommand("").setExecutor(gameLogic);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
