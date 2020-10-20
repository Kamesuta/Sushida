package net.teamfruit.sushida.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.util.ShuffleCollectors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {
    public PlayerData owner;
    private String wordName = "word";
    private String wordName = "word";
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

    public Set<PlayerData> getMembers() {
        return members;
    }

    public ImmutableSet<PlayerData> getPlayers() {
        return ImmutableSet.<PlayerData>builder().add(owner).addAll(members).build();
    }

    public String getWordName() {
        return wordName;
    }

    public ImmutableList<Map.Entry<String, String>> getWordList() {
        return wordRequiredList;
    }

    public void init() {
        wordRequiredList = word.mappings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(e -> e.getValue().entrySet().stream().collect(ShuffleCollectors.toShuffledList()).stream())
                .collect(ImmutableList.toImmutableList());
    }
}
