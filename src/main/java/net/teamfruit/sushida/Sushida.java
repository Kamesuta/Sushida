package net.teamfruit.sushida;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Sushida extends JavaPlugin {

    public Logger logger;
    private final Map<Player, GamePlayerData> data = new HashMap<>();
    public Romaji romaji;
    public Word word;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        romaji = Romaji.load(getResource("romaji.yml"));
        word = Word.load(getResource("word.yml"));

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

    public GamePlayerData getPlayerData(Player player) {
        return data.computeIfAbsent(player, e -> new GamePlayerData());
    }

}
