package net.teamfruit.sushida.mode;

import java.util.List;

public class GameSettingType {
    public final String name;
    public final String title;
    public final String description;
    public final int defaultValue;
    public final List<Integer> candidates;

    public GameSettingType(String name, String title, String description, int defaultValue, List<Integer> candidates) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.defaultValue = defaultValue;
        this.candidates = candidates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameSettingType that = (GameSettingType) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
