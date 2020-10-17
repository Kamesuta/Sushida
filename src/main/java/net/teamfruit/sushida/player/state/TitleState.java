package net.teamfruit.sushida.player.state;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

public class TitleState implements IState {
    private int bgmCount = 100;

    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        IntStream.range(0, 9).forEachOrdered(e -> player.sendMessage(""));

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
                .create()
        );
        player.sendMessage("");
        player.sendMessage(new ComponentBuilder()
                .append("  (≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡").color(ChatColor.GRAY)
                .create()
        );
        player.sendMessage("");
        player.sendMessage(new ComponentBuilder()
                .append("    画面中央の文字の指示に従ってください").color(ChatColor.GREEN)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("    ※推奨設定: 全画面、GUIの大きさ→3、Unicodeフォント強制→OFF").color(ChatColor.GREEN)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("    ※快適なプレイのために一度チャット画面を閉じ、").color(ChatColor.GOLD)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append(new TextComponent(new ComponentBuilder("   【F3+D】").bold(true).create())).color(ChatColor.GRAY)
                .append("を押すことを推奨します").color(ChatColor.GOLD)
                .create()
        );
        player.sendMessage("");

        player.playSound(player.getLocation(), "sushida:sushida.open", SoundCategory.PLAYERS, 1, 1);

        return null;
    }

    @Override
    public void onExit(StateContainer state) {
        Player player = state.data.player;

        player.stopSound("sushida:sushida.op", SoundCategory.RECORDS);
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        Player player = state.data.player;

        if ("".equals(typed)) {
            player.playSound(player.getLocation(), "sushida:sushida.whistle1", SoundCategory.PLAYERS, 1, 1);

            return new PlayState();
        }

        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        player.sendTitle("ⓘ チャット画面を使います", "「/ 」スラッシュを押してからスペースを押すとスタートします", 10, 0, 10);

        if (bgmCount++ >= 7) {
            bgmCount = 0;
            player.playSound(player.getLocation(), "sushida:sushida.op", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
