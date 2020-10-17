package net.teamfruit.sushida.player.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.moji.MojiExtractor;
import net.teamfruit.sushida.player.StateContainer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayState implements IState {
    private ImmutableListMultimap<String, String> romaji;

    private List<Map.Entry<String, String>> wordRequiredList;
    private Map.Entry<String, String> wordRequired;
    private String wordRemainingRequiredHiragana;
    private String wordTypedRomaji = "";

    public void genNextWord() {
        if (wordRequiredList.isEmpty())
            return;
        wordRequired = wordRequiredList.remove(0);
        wordRemainingRequiredHiragana = wordRequired.getKey();
    }

    @Override
    public IState onEnter(StateContainer state) {
        romaji = Sushida.logic.romaji.mappings;
        wordRequiredList = Sushida.logic.word.mappings.entrySet().stream()
                .flatMap(e -> e.getValue().entrySet().stream())
                .collect(Collectors.toList());
        Collections.shuffle(wordRequiredList);

        genNextWord();

        onType(state, "");
        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        String wordTypedRomajiNext = wordTypedRomaji + typed;
        List<String> candidate = MojiExtractor.getRomajiCandidate(wordRemainingRequiredHiragana);
        if (candidate.stream().anyMatch(s -> s.startsWith(wordTypedRomajiNext))) {
            // OK
            wordTypedRomaji = wordTypedRomajiNext;
            ImmutableList<String> hiragana = MojiExtractor.getHiraganaCandidate(wordTypedRomajiNext);
            hiragana.stream().filter(wordRemainingRequiredHiragana::startsWith)
                    .findFirst().ifPresent(e -> {
                wordRemainingRequiredHiragana = wordRemainingRequiredHiragana.substring(e.length());
                wordTypedRomaji = "";
            });
            if (wordRemainingRequiredHiragana.isEmpty()) {
                genNextWord();
            }
        } else {
            // Miss
        }
//        Sushida.logger.info(wordRemainingRequiredHiragana + "(" + charTypedHiraganaPosition + "): " + charTypedRomaji + " += " + typed + " [ " + charRequiredRomaji + " ]");

        state.data.player.sendTitle(wordRemainingRequiredHiragana, wordTypedRomaji, 0, 10000, 0);
        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PauseState();
    }
}
