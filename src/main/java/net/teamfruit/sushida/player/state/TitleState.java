package net.teamfruit.sushida.player.state;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.entity.Player;

public class TitleState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        for (int i = 0; i < 8; i++)
            player.sendMessage("");

        player.sendMessage(new ComponentBuilder()
                .append("≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)≡)").color(ChatColor.GRAY)
                .create()
        );
        player.sendMessage("");
        player.sendMessage(new ComponentBuilder()
                .append("                                ")
                .append(new ComponentBuilder("寿司打").bold(true).create()).color(ChatColor.YELLOW)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("                                                  ")
                .append("制作: かめすた").color(ChatColor.LIGHT_PURPLE)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡(≡").color(ChatColor.GRAY)
                .create()
        );
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage(new ComponentBuilder()
                .append("    画面中央の文字の指示に従ってください").color(ChatColor.GREEN)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("    ※快適なプレイのために一度チャット画面を閉じ、").color(ChatColor.GREEN)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append(new TextComponent(new ComponentBuilder("    【F3+D】").bold(true).create())).color(ChatColor.GRAY)
                .append("を押すことを推奨します").color(ChatColor.GREEN)
                .create()
        );
        player.sendMessage("");
        player.sendMessage("");

        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed) {
        if ("".equals(typed))
             return new PlayState();
        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        state.data.player.sendTitle("ⓘ タイピング中はチャット画面を使います", "「/ 」スラッシュを押してからスペースを押すとスタートします", 10, 0, 10);
        return null;
    }
}
