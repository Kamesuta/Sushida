package net.teamfruit.sushida;

import com.google.common.collect.ImmutableMap;
import net.teamfruit.sushida.data.ConversionTableLoader;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.listener.*;
import net.teamfruit.sushida.logic.GameLogic;
import net.teamfruit.sushida.ranking.RankingSetting;
import net.teamfruit.sushida.util.ConfigProperty;
import net.teamfruit.sushida.util.StringUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
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
    public static ConfigProperty<Boolean> enableAroundYou;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        plugin = this;

        // リソースパック
        saveDefaultConfig();
        Configuration config = getConfig();
        String url = config.getString("resourcepack.url");
        String hash = config.getString("resourcepack.hash");
        resourcePack = new ResourcePack(url, hash);
        enableAroundYou = new ConfigProperty<>("aroundyou.enabled", getConfig()::getBoolean, getConfig()::set, this::saveConfig);

        // ランキング
        ConfigurationSection rankingSection = config.getConfigurationSection("rankings");
        ImmutableMap<String, RankingSetting> rankings = Optional.ofNullable(rankingSection)
                .map(r -> r.getKeys(false).stream()
                        .map(name -> RankingSetting.fromConfig(name, rankingSection.getConfigurationSection(name)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(ImmutableMap.toImmutableMap(e -> e.name, f -> f))
                ).orElseGet(ImmutableMap::of);

        // 辞書
        File folder = new File(getDataFolder(), "wordset");
        if (!new File(folder, "word.yml").exists())
            saveResource("wordset/word.yml", false);
        Map<String, Word> wordset = Arrays.stream(Optional.ofNullable(folder.listFiles())
                .orElseGet(() -> new File[0]))
                .collect(Collectors.toMap(
                        e -> StringUtils.substringBeforeLast(e.getName(), ".yml"),
                        e -> {
                            try (InputStream in = Files.newInputStream(e.toPath())) {
                                return Word.load(in);
                            } catch (Exception ex) {
                                Sushida.logger.log(Level.SEVERE, String.format("Failed to load word [%s]", e.getName()), ex);
                            }
                            return new Word("壊れた辞書", ImmutableMap.of());
                        }
                ));

        // ロジック
        logic = new GameLogic.GameLogicBuilder()
                .romaji(ConversionTableLoader.createFromStream(getResource("romaji.csv")))
                .word(wordset)
                .ranking(rankings)
                .build();

        belowName = new BelowNameManager();

        // Event
        getServer().getPluginManager().registerEvents(new TypeEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

        // Tick
        new TickEventGenerator().runTaskTimer(this, 0, 20);
        new AsyncTickEventGenerator().runTaskTimerAsynchronously(this, 0, 20);

        // Command
        Objects.requireNonNull(getCommand("sushida")).setExecutor(new ManageCommandListener());
        Objects.requireNonNull(getCommand("")).setExecutor(new GameCommandListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        belowName.clearManaged();
    }

}
