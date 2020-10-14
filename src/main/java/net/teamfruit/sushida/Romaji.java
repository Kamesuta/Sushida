package net.teamfruit.sushida;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Romaji {
    private final ListMultimap<String, String> mappings;

    public Romaji(ListMultimap<String, String> mappings) {
        this.mappings = mappings;
    }

    public static Romaji load(InputStream input) {
        try {
            JsonElement je = new JsonParser().parse(new JsonReader(new InputStreamReader(input, Charsets.UTF_8)));
            ListMultimap<String, String> multimap = ArrayListMultimap.create();
            je.getAsJsonObject().getAsJsonArray("mapping").forEach(e ->
                    e.getAsJsonObject().entrySet().forEach(a -> multimap.put(a.getKey(), a.getValue().getAsString())));
            return new Romaji(multimap);
        } catch (Exception e) {
            throw new RuntimeException("Romaji load error", e);
        }
    }
}
