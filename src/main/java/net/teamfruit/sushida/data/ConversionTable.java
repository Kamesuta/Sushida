package net.teamfruit.sushida.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.util.List;

public class ConversionTable {

    private final ListMultimap<String, String> conversionMap;
    private final int maxKeyLength;

    public ConversionTable(ListMultimap<String, String> conversionMap) {
        this.conversionMap = conversionMap;
        this.maxKeyLength = conversionMap.keySet().stream().mapToInt(String::length).max().orElse(0);
    }

    public int getMaxKeyLength() {
        return maxKeyLength;
    }

    public List<String> get(String key) {
        return conversionMap.get(key);
    }

    public static ConversionTable invert(ConversionTable table) {
        return new ConversionTable(Multimaps.invertFrom(table.conversionMap, ArrayListMultimap.create()));
    }
}
