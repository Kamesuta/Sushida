package net.teamfruit.sushida.moji;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ConversionTable {

    private final ListMultimap<String, String> conversionMap;
    private int maxKeyLength;

    private ConversionTable(ListMultimap<String, String> conversionMap) {
        this.conversionMap = conversionMap;
        this.maxKeyLength = conversionMap.keySet().stream().mapToInt(String::length).max().orElse(0);
    }

    public int getMaxKeyLength() {
        return maxKeyLength;
    }

    public List<String> get(String key) {
        return conversionMap.get(key);
    }

    private static class ConversionTableCache {
        private static final String ROMAJI_TO_HIRAGANA_FILE = "/romaji_to_hiragana.csv";

        public static final ConversionTable romajiToHiraganaTable;
        public static final ConversionTable hiraganaToRomajiTable;

        static {
            romajiToHiraganaTable = createConversionTableFromResource(ROMAJI_TO_HIRAGANA_FILE);
            hiraganaToRomajiTable = new ConversionTable(Multimaps.invertFrom(romajiToHiraganaTable.conversionMap, ArrayListMultimap.create()));
        }

        private static ConversionTable createConversionTableFromResource(String resourceName) {

            try (InputStream inputStream = ConversionTable.class.getResourceAsStream(resourceName)) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                ListMultimap<String, String> conversionMap = ArrayListMultimap.create();

                String line;
                while ((line = reader.readLine()) != null) {

                    int delimiterIndex = line.indexOf(',');

                    String key = line.substring(0, delimiterIndex);
                    String value = line.substring(delimiterIndex + 1);

                    conversionMap.put(key, value);
                }

                return new ConversionTable(conversionMap);

            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static ConversionTable getRomajiToHiragana() {
        return ConversionTableCache.romajiToHiraganaTable;
    }

    public static ConversionTable getHiraganaToRomaji() {
        return ConversionTableCache.hiraganaToRomajiTable;
    }
}
