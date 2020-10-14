package net.teamfruit.sushida;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Romaji {
    private final ListMultimap<String, String> mappings;

    public Romaji(ListMultimap<String, String> mappings) {
        this.mappings = mappings;
    }

    public static Romaji load(InputStream input) {
        try {
            Yaml cfg = new Yaml(new CustomClassLoaderConstructor(Romaji.class.getClassLoader()));
            RomajiData data = cfg.loadAs(new InputStreamReader(input, Charsets.UTF_8), RomajiData.class);
            ListMultimap<String, String> multimap = data.romaji.stream()
                    .flatMap(e -> e.entrySet().stream())
                    .collect(ArrayListMultimap::create,
                            (m, e) -> m.put(e.getKey(), e.getValue()),
                            (a, b) -> a.putAll(b));
            return new Romaji(multimap);
        } catch (Exception e) {
            throw new RuntimeException("Romaji load error", e);
        }
    }

    public static class RomajiData {
        public List<Map<String, String>> romaji;
    }
}
