package net.teamfruit.sushida;

import net.teamfruit.sushida.data.Romaji;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.listener.GameCommandListener;
import net.teamfruit.sushida.listener.ManageCommandListener;
import net.teamfruit.sushida.listener.TickEventGenerator;
import net.teamfruit.sushida.listener.TypeEventListener;
import net.teamfruit.sushida.logic.GameLogic;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Sushida extends JavaPlugin {

    public static Logger logger;
    public static GameLogic logic;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        logic = new GameLogic.GameLogicBuilder()
                .romaji(Romaji.load(getResource("romaji.yml")))
                .word(Word.load(getResource("word.yml")))
                .build();

        // Event
        getServer().getPluginManager().registerEvents(new TypeEventListener(), this);

        // Tick
        new TickEventGenerator().runTaskTimerAsynchronously(this, 0, 20);

        // Command
        getCommand("sushida").setExecutor(new ManageCommandListener());
        getCommand("").setExecutor(new GameCommandListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
