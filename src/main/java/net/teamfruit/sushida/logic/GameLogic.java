package net.teamfruit.sushida.logic;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import net.teamfruit.sushida.data.ConversionTable;
import net.teamfruit.sushida.data.ConversionTableLoader;
import net.teamfruit.sushida.data.Word;
import net.teamfruit.sushida.player.PlayerDataContainer;
import net.teamfruit.sushida.ranking.RankingSetting;

import java.util.Map;

public class GameLogic {
    public final PlayerDataContainer states;
    public final ConversionTable romajiToHiragana;
    public final ConversionTable hiraganaToRomaji;
    public final Map<String, Word> word;
    public final Map<String, RankingSetting> ranking;

    private GameLogic(ConversionTable romajiToHiragana, ConversionTable hiraganaToRomaji, Map<String, Word> word, Map<String, RankingSetting> ranking) {
        this.states = new PlayerDataContainer();
        this.romajiToHiragana = romajiToHiragana;
        this.hiraganaToRomaji = hiraganaToRomaji;
        this.word = word;
        this.ranking = ranking;
    }

    public static class GameLogicBuilder {
        private ConversionTable romajiToHiragana = new ConversionTable(ArrayListMultimap.create());
        private ConversionTable hiraganaToRomaji = new ConversionTable(ArrayListMultimap.create());
        private Map<String, Word> word = ImmutableMap.of();
        private Map<String, RankingSetting> ranking = ImmutableMap.of();

        public GameLogicBuilder romaji(ConversionTable romajiToHiragana, ConversionTable hiraganaToRomaji) {
            this.romajiToHiragana = romajiToHiragana;
            this.hiraganaToRomaji = hiraganaToRomaji;
            return this;
        }

        public GameLogicBuilder romaji(ConversionTableLoader loader) {
            return romaji(loader.romajiToHiraganaTable, loader.hiraganaToRomajiTable);
        }

        public GameLogicBuilder word(Map<String, Word> word) {
            this.word = word;
            return this;
        }

        public GameLogicBuilder ranking(Map<String, RankingSetting> ranking) {
            this.ranking = ranking;
            return this;
        }

        public GameLogic build() {
            return new GameLogic(romajiToHiragana, hiraganaToRomaji, word, ranking);
        }
    }
}
