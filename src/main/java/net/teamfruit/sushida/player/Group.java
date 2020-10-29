package net.teamfruit.sushida.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.mode.GameMode;
import net.teamfruit.sushida.mode.GameModes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {
    public PlayerData owner;
    private String wordName = "word";
    private GameMode gameMode = GameModes.time.mode;
    private Word word = Sushida.logic.word.get(wordName);
    private Set<PlayerData> members = new HashSet<>();
    private ImmutableList<Map.Entry<String, String>> wordRequiredList;

    public Group(PlayerData owner) {
        this.owner = owner;
    }

    public boolean hasPermission(PlayerData player) {
        return player.equals(owner);
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
    }
}
