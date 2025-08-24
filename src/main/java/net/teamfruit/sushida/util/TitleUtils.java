package net.teamfruit.sushida.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.stream.Collector;

public class TitleUtils {

    public static Collector<BaseComponent[], ?, BaseComponent[]> joining(BaseComponent[] joiner) {
        return Collector.of(
                ComponentBuilder::new,
                (r1, r2) -> {
                    if (!r1.getParts().isEmpty())
                        r1.append(joiner);
                    r1.append(r2);
                },
                (r1, r2) -> r1.append(r2.create()),
                ComponentBuilder::create);
    }
}
