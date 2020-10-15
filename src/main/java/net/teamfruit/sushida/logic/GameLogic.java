package net.teamfruit.sushida.logic;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import net.teamfruit.sushida.data.Romaji;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.player.PlayerDataContainer;

public class GameLogic {
    public final PlayerDataContainer states;
    public final Romaji romaji;
    public final Word word;

    private GameLogic(Romaji romaji, Word word) {
        this.states = new PlayerDataContainer();
        this.romaji = romaji;
        this.word = word;
    }

    public static class GameLogicBuilder {
        private Romaji romaji = new Romaji(ImmutableListMultimap.of());
        private Word word = new Word(ImmutableMap.of());

        public GameLogicBuilder romaji(Romaji romaji) {
            this.romaji = romaji;
            return this;
        }

        public GameLogicBuilder word(Word word) {
            this.word = word;
            return this;
        }

        public GameLogic build() {
            return new GameLogic(romaji, word);
        }
    }
}
