package net.teamfruit.sushida.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConversionTableLoader {
    public final ConversionTable romajiToHiraganaTable;
    public final ConversionTable hiraganaToRomajiTable;

    public ConversionTableLoader(ConversionTable romajiToHiraganaTable, ConversionTable hiraganaToRomajiTable) {
        this.romajiToHiraganaTable = romajiToHiraganaTable;
        this.hiraganaToRomajiTable = hiraganaToRomajiTable;
    }

    public static ConversionTableLoader createFromStream(InputStream stream) {
        try (InputStream inputStream = stream) {
            ConversionTable romajiToHiraganaTable = createConversionTableFromStream(inputStream);
            ConversionTable hiraganaToRomajiTable = ConversionTable.invert(romajiToHiraganaTable);
            return new ConversionTableLoader(romajiToHiraganaTable, hiraganaToRomajiTable);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static ConversionTable createConversionTableFromStream(InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            ListMultimap<String, String> conversionMap = ArrayListMultimap.create();

            String line;
            while ((line = reader.readLine()) != null) {

                int delimiterIndex = line.lastIndexOf(',');

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
