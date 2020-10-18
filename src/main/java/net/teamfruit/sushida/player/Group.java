package net.teamfruit.sushida.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.util.ShuffleCollectors;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Group {
    public PlayerData owner;
    private Word word = Sushida.logic.word.get("word");
    private Set<PlayerData> members = new HashSet<>();

    public Group(PlayerData owner) {
        this.owner = owner;
    }

    public boolean hasPermission(Player player) {
        return player.equals(owner);
    }

    public boolean setWord(String name) {
        Word word = Sushida.logic.word.get(name);
        if (word == null)
            return false;
        this.word = word;
        return true;
    }

    public void addMember(PlayerData player) {
        members.add(player);
    }

    public void removeMember(PlayerData player) {
        members.remove(player);
    }

    public Set<PlayerData> getMembers() {
        return members;
    }

    public ImmutableSet<PlayerData> getPlayers() {
        return ImmutableSet.<PlayerData>builder().add(owner).addAll(members).build();
    }

    public ImmutableList<Map.Entry<String, String>> getWordList() {
        throw new NotImplementedException();
    }

    public void init() {
        List<?> wordRequiredList = word.mappings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(e -> e.getValue().entrySet().stream().collect(ShuffleCollectors.toShuffledList()).stream())
                .collect(Collectors.toList());
        Collections.shuffle(wordRequiredList);
    }
}
