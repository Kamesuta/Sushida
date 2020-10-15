package net.teamfruit.sushida.data;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Word {
    public final ImmutableMap<String, Map<String, String>> mappings;

    public Word(ImmutableMap<String, Map<String, String>> mappings) {
        this.mappings = mappings;
    }

    public static Word load(InputStream input) {
        try {
            Yaml cfg = new Yaml(new CustomClassLoaderConstructor(Word.class.getClassLoader()));
            WordData data = cfg.loadAs(new InputStreamReader(input, Charsets.UTF_8), WordData.class);
            return new Word(ImmutableMap.copyOf(data.word));
        } catch (Exception e) {
            throw new RuntimeException("Word load error", e);
        }
    }

    public static class WordData {
        public Map<String, Map<String, String>> word;
    }
}
