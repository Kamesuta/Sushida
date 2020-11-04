package net.teamfruit.sushida.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.mode.GameMode;
import net.teamfruit.sushida.mode.GameModes;
import net.teamfruit.sushida.mode.GameSettingType;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {
    public PlayerData owner;
    private String wordName = "word";
    private GameMode gameMode = GameModes.time.mode;
    private Word word = Sushida.logic.word.get(wordName);
    private Map<GameSettingType, Integer> settings = new HashMap<>();
    private Set<PlayerData> members = new HashSet<>();
    private ImmutableList<Map.Entry<String, String>> wordRequiredList;

    private Scoreboard groupScoreboard;
    private Objective scoreLeaderboard;
    private Objective tabLeaderboard;

    public Group(PlayerData owner) {
        this.owner = owner;
        this.groupScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public boolean isOwner(PlayerData player) {
        return player.equals(owner);
    }

    public boolean setOwner(PlayerData player) {
        if (isOwner(player))
            return false;
        if (!members.contains(player))
            return false;
        members.remove(player);
        members.add(owner);
        owner = player;
        return true;
    }

    public Map<GameSettingType, Integer> getSettings() {
        return settings;
    }

    public Scoreboard getGroupScoreboard() {
        return groupScoreboard;
    }

    public Objective getScoreLeaderboard() {
        return scoreLeaderboard;
    }

    public Objective getTabLeaderboard() {
        return tabLeaderboard;
    }

    public boolean setWord(String name) {
        Word word = Sushida.logic.word.get(name);
        if (word == null)
            return false;
        this.wordName = name;
        this.word = word;
        return true;
    }

    public boolean setMode(String name) {
        GameModes mode = GameModes.fromName(name);
        if (mode == null)
            return false;
        return setMode(mode.mode);
    }

    public boolean setMode(GameMode mode) {
        if (mode == null)
            return false;
        this.gameMode = mode;
        return true;
    }

    public Set<PlayerData> getMembers() {
        return members;
    }

    public ImmutableSet<PlayerData> getPlayers() {
        return ImmutableSet.<PlayerData>builder().add(owner).addAll(members).build();
    }

    public String getWordName() {
        return wordName;
    }

    public Word getWord() {
        return word;
    }

    public GameMode getMode() {
        return gameMode;
    }

    public ImmutableList<Map.Entry<String, String>> getWordList() {
        return wordRequiredList;
    }

    public void init() {
        wordRequiredList = getMode().getWords(this);

        this.scoreLeaderboard = groupScoreboard.getObjective("score");
        if (this.scoreLeaderboard != null)
            this.scoreLeaderboard.unregister();
        this.scoreLeaderboard = groupScoreboard.registerNewObjective("score", "dummy", "スコア");
        this.scoreLeaderboard.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.tabLeaderboard = groupScoreboard.getObjective("tab");
        if (this.tabLeaderboard != null)
            this.tabLeaderboard.unregister();
        this.tabLeaderboard = groupScoreboard.registerNewObjective("tab", "dummy", "スコア");
        this.tabLeaderboard.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
}
