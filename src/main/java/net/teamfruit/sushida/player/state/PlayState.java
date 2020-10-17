package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.data.MojiExtractor;
import net.teamfruit.sushida.player.StateContainer;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayState implements IState {
    private List<Map.Entry<String, String>> wordRequiredList;
    private Map.Entry<String, String> wordRequired;
    private String wordRemainingRequiredHiragana;
    private String wordTypedRomaji = "";
    private String wordTypedTotalRomaji = "";

    public void genNextWord() {
        if (wordRequiredList.isEmpty())
            return;
        wordRequired = wordRequiredList.remove(0);
        wordRemainingRequiredHiragana = wordRequired.getKey();
        wordTypedTotalRomaji = "";
    }

    @Override
    public IState onEnter(StateContainer state) {
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
        List<String> candidate = MojiExtractor.getRomajiCandidate(wordRemainingRequiredHiragana, Sushida.logic.hiraganaToRomaji);
        if (candidate.stream().anyMatch(s -> s.startsWith(wordTypedRomajiNext))) {
            // OK
            wordTypedRomaji = wordTypedRomajiNext;
            ImmutableList<String> hiragana = MojiExtractor.getHiraganaCandidate(wordTypedRomajiNext, Sushida.logic.romajiToHiragana);
            hiragana.stream().filter(wordRemainingRequiredHiragana::startsWith)
                    .findFirst().ifPresent(e -> {
                wordRemainingRequiredHiragana = wordRemainingRequiredHiragana.substring(e.length());
                wordTypedTotalRomaji += wordTypedRomaji;
                wordTypedRomaji = "";
            });
            if (wordRemainingRequiredHiragana.isEmpty()) {
                genNextWord();
            }
        } else {
            // Miss
        }

        state.data.player.sendTitle(new Title(
                new ComponentBuilder()
                        .append(StringUtils.substringBefore(wordRequired.getKey(), wordRemainingRequiredHiragana)).color(ChatColor.WHITE)
                        .append(wordRemainingRequiredHiragana).color(ChatColor.GRAY)
                        .create(),
                new ComponentBuilder()
                        .append(wordTypedTotalRomaji).color(ChatColor.WHITE)
                        .append(wordTypedRomaji).color(ChatColor.GRAY)
                        .create(),
                0, 10000, 0));
        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PauseState();
    }
}
