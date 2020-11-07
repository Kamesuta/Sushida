package net.teamfruit.sushida.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.stream.Collector;

public class TitleUtils {
    public static Collector<BaseComponent[], ?, BaseComponent[]> joining() {
        return Collector.of(
                ComponentBuilder::new,
                ComponentBuilder::append,
                (r1, r2) -> r1.append(r2.create()),
                ComponentBuilder::create);
    }

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

    public static void showTitle(Player player) {
        player.sendMessage(new ComponentBuilder()
                .append("  ≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)").color(ChatColor.GRAY)
                .create()
        );
        player.sendMessage("");
        player.sendMessage(new ComponentBuilder()
                .append("      ")
                .append(new ComponentBuilder("寿司打").bold(true).create()).color(ChatColor.YELLOW)
                .append("  -  ").color(ChatColor.GRAY)
                .append("制作: かめすた").color(ChatColor.LIGHT_PURPLE)
                .append(" / ").color(ChatColor.GRAY)
                .append("制作時間: 85時間").color(ChatColor.RED)
                .create()
        );
        player.sendMessage("");
        player.sendMessage(new ComponentBuilder()
                .append("  (≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡").color(ChatColor.GRAY)
                .create()
        );
        player.sendMessage("");
    }
}
