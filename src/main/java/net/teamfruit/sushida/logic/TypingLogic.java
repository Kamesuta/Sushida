package net.teamfruit.sushida.logic;

import com.google.common.collect.ImmutableList;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.MojiExtractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypingLogic {
    private List<Map.Entry<String, String>> wordRequiredList;
    private Map.Entry<String, String> wordRequired;
    private String wordRemainingRequiredHiragana;
    private String wordTypedRomaji = "";
    private String wordTypedTotalRomaji = "";

    public TypingLogic() {
        init();
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

    public void genNextWord() {
        if (wordRequiredList.isEmpty())
            return;
        wordRequired = wordRequiredList.remove(0);
        //wordRequired = new AbstractMap.SimpleEntry<>("きんにくつう", "筋肉痛");
        wordRemainingRequiredHiragana = wordRequired.getKey();
        wordTypedTotalRomaji = "";
        wordTypedRomaji = "";
    }

    public void init() {
        wordRequiredList = Sushida.logic.word.mappings.entrySet().stream()
                .flatMap(e -> e.getValue().entrySet().stream())
                .collect(Collectors.toList());
        Collections.shuffle(wordRequiredList);

        genNextWord();
    }

    public boolean type(String typed) {
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
            });
            if (wordRemainingRequiredHiragana.isEmpty()) {
                genNextWord();
            }
            return true;
        } else {
            // Miss
            return false;
        }
    }
}
