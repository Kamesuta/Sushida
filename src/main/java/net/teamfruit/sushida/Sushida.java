package net.teamfruit.sushida;

import com.google.common.collect.ImmutableMap;
import net.teamfruit.sushida.belowname.BelowNameManager;
import net.teamfruit.sushida.data.ConversionTableLoader;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.listener.*;
import net.teamfruit.sushida.logic.GameLogic;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Sushida extends JavaPlugin {

    public static Logger logger;
    public static GameLogic logic;
    public static Plugin plugin;
    public static BelowNameManager belowName;
    public static ResourcePack resourcePack;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        plugin = this;

        // リソースパック
        saveDefaultConfig();
        String url = getConfig().getString("resourcepack.url");
        String hash = getConfig().getString("resourcepack.hash");
        resourcePack = new ResourcePack(url, hash);

        File folder = new File(getDataFolder(), "wordset");
        if (!new File(folder, "word.yml").exists())
            saveResource("wordset/word.yml", false);
        Map<String, Word> wordset = Arrays.stream(Optional.ofNullable(folder.listFiles())
                .orElseGet(() -> new File[0]))
                .collect(Collectors.toMap(
                        e -> StringUtils.substringBeforeLast(e.getName(), ".yml"),
                        e -> {
                            try (InputStream in = new FileInputStream(e)) {
                                return Word.load(in);
                            } catch (Exception ex) {
                                Sushida.logger.log(Level.SEVERE, String.format("Failed to load word [%s]", e.getName()), ex);
                            }
                            return new Word(ImmutableMap.of());
                        }
                ));
        logic = new GameLogic.GameLogicBuilder()
                .romaji(ConversionTableLoader.createFromStream(getResource("romaji.csv")))
                .word(wordset)
                .build();

        belowName = new BelowNameManager();

        // Event
        getServer().getPluginManager().registerEvents(new TypeEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

        // Tick
        new TickEventGenerator().runTaskTimerAsynchronously(this, 0, 20);

        // Command
        getCommand("sushida").setExecutor(new ManageCommandListener());
        getCommand("").setExecutor(new GameCommandListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        belowName.clearManaged();
    }

}
