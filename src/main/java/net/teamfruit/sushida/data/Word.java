package net.teamfruit.sushida.data;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Word {
    @Nullable
    public final String title;
    public final ImmutableMap<String, ImmutableList<ImmutableList<Map.Entry<String, String>>>> mappings;

    public Word(@Nullable String title, ImmutableMap<String, ImmutableList<ImmutableList<Map.Entry<String, String>>>> mappings) {
        this.title = title;
        this.mappings = mappings;
    }

    @SuppressWarnings("unchecked")
    public static Word load(InputStream input) {
        try {

            Yaml cfg = new Yaml(new CustomClassLoaderConstructor(Word.class.getClassLoader(), new LoaderOptions()));
            WordData data = cfg.loadAs(new InputStreamReader(input, Charsets.UTF_8), WordData.class);
            return new Word(
                    data.title,
                    data.word.entrySet().stream()
                            .collect(ImmutableMap.toImmutableMap(
                                    Map.Entry::getKey,
                                    e -> e.getValue().stream()
                                            .map(f -> ((f instanceof List)
                                                            ? ((List<Map<String, String>>) f).stream()
                                                            : Stream.of((Map<String, String>) f)
                                                    )
                                                            .map(g -> g.entrySet().stream().findFirst().get())
                                                            .collect(ImmutableList.toImmutableList())
                                            )
                                            .collect(ImmutableList.toImmutableList())
                            ))
            );
        } catch (Exception e) {
            throw new RuntimeException("Word load error", e);
        }
    }

    public static class WordData {
        public String title;
        public Map<String, List<Object>> word;
    }
}
