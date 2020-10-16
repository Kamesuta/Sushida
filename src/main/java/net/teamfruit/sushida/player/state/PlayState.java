package net.teamfruit.sushida.player.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.StateContainer;

import java.util.*;
import java.util.stream.Collectors;

public class PlayState implements IState {
    private ImmutableListMultimap<String, String> romaji;

    private List<Map.Entry<String, String>> wordRequired;
    private String wordRequiredKanji;
    private String wordRequiredHiragana;
    private String wordRequiredRomaji;

    private int charTypedHiraganaPosition = 100;
    private String charRequiredHiragana;

    private Map<String, String> charRequiredRomaji = new HashMap<>();
    private String charTypedRomaji = "";

    public String intToString(int c) {
        return String.valueOf((char) c);
    }

    public String hiraganaToRomaji(String str) {
        return romaji.get(str).get(0);
    }

    public void genNextWord() {
        if (wordRequired.isEmpty())
            return;
        Map.Entry<String, String> entry = wordRequired.remove(0);
        wordRequiredKanji = entry.getValue();
        wordRequiredHiragana = entry.getKey();
        wordRequiredRomaji = entry.getKey().chars()
                .mapToObj(this::intToString)
                .map(this::hiraganaToRomaji)
                .collect(Collectors.joining());
    }

    public void nextCharHiragana() {
        charTypedHiraganaPosition++;
        if (charTypedHiraganaPosition >= wordRequiredHiragana.length()) {
            genNextWord();
            charTypedHiraganaPosition = 0;
        }
        charRequiredHiragana = intToString(wordRequiredHiragana.charAt(charTypedHiraganaPosition));
    }

    public void nextCharRomaji() {
        ImmutableList<String> candidate = romaji.get(charRequiredHiragana);
        if (candidate.contains(charTypedRomaji)) {
            nextCharHiragana();
            candidate = romaji.get(charRequiredHiragana);
            charTypedRomaji = "";
        }
        charRequiredRomaji = candidate.stream()
                .filter(e -> e.startsWith(charTypedRomaji))
                .collect(Collectors.toMap(e -> e, e -> intToString(e.charAt(charTypedRomaji.length()))));
    }

    @Override
    public IState onEnter(StateContainer state) {
        romaji = Sushida.logic.romaji.mappings;
        wordRequired = Sushida.logic.word.mappings.entrySet().stream()
                .flatMap(e -> e.getValue().entrySet().stream())
                .collect(Collectors.toList());
        Collections.shuffle(wordRequired);

        genNextWord();
        nextCharHiragana();
        nextCharRomaji();

        onType(state, "");
        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        Sushida.logger.info(charRequiredHiragana + "(" + charTypedHiraganaPosition + "): " + charTypedRomaji + " += " + typed + " [ " + charRequiredRomaji + " ]");
        if (charRequiredRomaji.containsValue(typed)) {
            // OK
            charTypedRomaji += typed;
            nextCharRomaji();
        } else {
            // Miss
        }
        Sushida.logger.info(charRequiredHiragana + "(" + charTypedHiraganaPosition + "): " + charTypedRomaji + " += " + typed + " [ " + charRequiredRomaji + " ]");

        state.data.player.sendTitle(wordRequiredHiragana, charTypedRomaji, 0, 100, 0);
        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PauseState();
    }
}
