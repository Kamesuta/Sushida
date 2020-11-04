package net.teamfruit.sushida.ranking;

import com.google.common.collect.ImmutableMap;
import net.teamfruit.sushida.mode.GameMode;
import net.teamfruit.sushida.mode.GameModes;
import net.teamfruit.sushida.mode.GameSettingType;
import net.teamfruit.sushida.player.Group;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Optional;

public class RankingSetting {
    public final String name;
    public final String title;
    public final String scoreboard;
    public final String word;
    public final GameMode mode;
    public final ImmutableMap<GameSettingType, Integer> settings;

    public RankingSetting(String name, String title, String scoreboard, String word, GameMode mode, ImmutableMap<GameSettingType, Integer> settings) {
        this.name = name;
        this.title = title;
        this.scoreboard = scoreboard;
        this.word = word;
        this.mode = mode;
        this.settings = settings;
    }

    public Objective getOrCreateObjective(Scoreboard sc) {
        Objective objective = sc.getObjective(scoreboard);
        if (objective == null)
            objective = sc.registerNewObjective(scoreboard, "dummy", title);
        return objective;
    }

    public static Optional<RankingSetting> fromConfig(String name, ConfigurationSection config) {
        if (config == null)
            return Optional.empty();
        String title = Optional.ofNullable(config.getString("title")).orElse(name);
        String scoreboard = Optional.ofNullable(config.getString("scoreboard")).orElse(name);
        String word = config.getString("word");
        GameModes modes = GameModes.fromName(config.getString("rule"));
        ConfigurationSection settingsConfig = config.getConfigurationSection("settings");
        if (name == null || word == null || modes == null)
            return Optional.empty();
        ImmutableMap<GameSettingType, Integer> settings = ImmutableMap.of();
        if (settingsConfig != null)
            settings = modes.mode.getSettingTypes().stream()
                    .collect(ImmutableMap.toImmutableMap(e -> e, e -> settingsConfig.getInt(e.name, e.defaultValue)));
        return Optional.of(new RankingSetting(name, title, scoreboard, word, modes.mode, settings));
    }

    public static RankingSetting fromGroup(String name, String title, String scoreboard, Group group) {
        return new RankingSetting(name, title, scoreboard, group.getWordName(), group.getMode(), ImmutableMap.copyOf(group.getSettings()));
    }
}
