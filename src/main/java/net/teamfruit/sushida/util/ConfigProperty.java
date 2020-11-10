package net.teamfruit.sushida.util;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigProperty<T> {
    protected String path;
    protected Function<String, T> getter;
    protected BiConsumer<String, T> setter;
    protected T cachedValue;

    public ConfigProperty(String path, Function<String, T> getter, BiConsumer<String, T> setter) {
        this.path = path;
        this.getter = getter;
        this.setter = setter;
    }

    public void set(T value) {
        setter.accept(path, value);
        cachedValue = value;
    }

    public T get() {
        if (cachedValue == null)
            cachedValue = getter.apply(path);
        return cachedValue;
    }
}
