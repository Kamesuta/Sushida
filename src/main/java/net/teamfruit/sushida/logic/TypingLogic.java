package net.teamfruit.sushida.logic;

import com.google.common.collect.ImmutableList;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.MojiExtractor;
import net.teamfruit.sushida.player.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypingLogic {
    private int wordInitialCount;
    private List<Map.Entry<String, String>> wordRequiredList;
    private Map.Entry<String, String> wordRequired;
    private String wordRemainingRequiredHiragana;
    private String wordTypedRomaji = "";
    private String wordTypedTotalRomaji = "";
    private boolean nextWordTiming;
    private boolean nextCharacterTiming;

    public TypingLogic(Group group) {
        wordRequiredList = new ArrayList<>(group.getWordList());
        wordInitialCount = wordRequiredList.size();
        init();
    }

    public int wordTotalCount() {
        return wordInitialCount;
    }

    public int wordDoneCount() {
        return wordInitialCount - wordRequiredList.size() - 1;
    }

    public int wordRemainingCount() {
        return wordRequiredList.size();
    }

    public void init() {
        genNextWord();
    }

    public String getRequiredHiragana() {
        return wordRequired.getKey();
    }

    public String getRequiredKanji() {
        return wordRequired.getValue();
    }

    public String getRemainingRequiredHiragana() {
        return wordRemainingRequiredHiragana;
    }

    public String getRemainingRequiredHiraganaVisual() {
        if (MojiExtractor.hasDoubleConsonantWithSokuon(wordTypedRomaji) || MojiExtractor.hasConsonantNextToN(wordTypedRomaji))
            return wordRemainingRequiredHiragana.substring(1);
        return wordRemainingRequiredHiragana;
    }

    public String getTypedTotalRomaji() {
        return wordTypedTotalRomaji;
    }

    public String getTypedRomaji() {
        return wordTypedRomaji;
    }

    public boolean isNextWordTiming() {
        return nextWordTiming;
    }

    public boolean isNextCharacterTiming() {
        return nextCharacterTiming;
    }

    public void genNextWord() {
        if (wordRequiredList.isEmpty())
            return;
        wordRequired = wordRequiredList.remove(0);
        //wordRequired = new AbstractMap.SimpleEntry<>("きんにくつう", "筋肉痛");
        wordRemainingRequiredHiragana = wordRequired.getKey();
        wordTypedTotalRomaji = "";
        wordTypedRomaji = "";
    }

    public boolean type(String typed) {
        nextWordTiming = false;
        nextCharacterTiming = false;
        String wordTypedRomajiNext = wordTypedRomaji + typed;
        List<String> candidate = MojiExtractor.getRomajiCandidate(wordRemainingRequiredHiragana, Sushida.logic.hiraganaToRomaji);
        if (candidate.stream().anyMatch(s -> s.startsWith(wordTypedRomajiNext))) {
            // OK
            wordTypedRomaji = wordTypedRomajiNext;
            ImmutableList<String> hiragana = MojiExtractor.getHiraganaCandidate(wordTypedRomajiNext, Sushida.logic.romajiToHiragana);
            hiragana.stream().filter(wordRemainingRequiredHiragana::startsWith).findFirst().ifPresent(e -> {
                wordTypedTotalRomaji += wordTypedRomaji;
                wordTypedRomaji = "";
                wordRemainingRequiredHiragana = wordRemainingRequiredHiragana.substring(e.length());
                nextCharacterTiming = true;
            });
            if (wordRemainingRequiredHiragana.isEmpty()) {
                genNextWord();
                nextWordTiming = true;
            }
            return true;
        } else {
            // Miss
            return false;
        }
    }
}
