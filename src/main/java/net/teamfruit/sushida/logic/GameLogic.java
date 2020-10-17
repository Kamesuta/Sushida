package net.teamfruit.sushida.logic;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import net.teamfruit.sushida.data.ConversionTable;
import net.teamfruit.sushida.data.ConversionTableLoader;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.player.PlayerDataContainer;

public class GameLogic {
    public final PlayerDataContainer states;
    public final ConversionTable romajiToHiragana;
    public final ConversionTable hiraganaToRomaji;
    public final Word word;

    private GameLogic(ConversionTable romajiToHiragana, ConversionTable hiraganaToRomaji, Word word) {
        this.states = new PlayerDataContainer();
        this.romajiToHiragana = romajiToHiragana;
        this.hiraganaToRomaji = hiraganaToRomaji;
        this.word = word;
    }

    public static class GameLogicBuilder {
        private ConversionTable romajiToHiragana = new ConversionTable(ArrayListMultimap.create());
        private ConversionTable hiraganaToRomaji = new ConversionTable(ArrayListMultimap.create());
        private Word word = new Word(ImmutableMap.of());

        public GameLogicBuilder romaji(ConversionTable romajiToHiragana, ConversionTable hiraganaToRomaji) {
            this.romajiToHiragana = romajiToHiragana;
            this.hiraganaToRomaji = hiraganaToRomaji;
            return this;
        }

        public GameLogicBuilder romaji(ConversionTableLoader loader) {
            return romaji(loader.romajiToHiraganaTable, loader.hiraganaToRomajiTable);
        }

        public GameLogicBuilder word(Word word) {
            this.word = word;
            return this;
        }

        public GameLogic build() {
            return new GameLogic(romajiToHiragana, hiraganaToRomaji, word);
        }
    }
}
