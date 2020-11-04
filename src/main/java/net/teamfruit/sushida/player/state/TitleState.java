package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.teamfruit.sushida.player.StateContainer;
import net.teamfruit.sushida.util.TitleUtils;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.stream.IntStream;

public class TitleState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        IntStream.range(0, 9).forEachOrdered(e -> player.sendMessage(""));

        TitleUtils.showTitle(player);
        player.sendMessage(new ComponentBuilder()
                .append("    画面中央の文字の指示に従ってください").color(ChatColor.GREEN)
                .create()
        );
        player.sendMessage(new ComponentBuilder()
                .append("    入力モードを半角にしてください").color(ChatColor.GREEN)
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
        boolean declined = player.getResourcePackStatus() == PlayerResourcePackStatusEvent.Status.DECLINED;
        player.sendMessage(new ComponentBuilder()
                .append("    ")
                .append(new TextComponent(
                        declined
                                ? new ComponentBuilder("[").reset()
                                .append("(").color(ChatColor.YELLOW).bold(true)
                                .append("(").color(ChatColor.RED).bold(true)
                                .append("!").color(ChatColor.YELLOW).bold(true)
                                .append(")").color(ChatColor.RED).bold(true)
                                .append(")").color(ChatColor.YELLOW).bold(true)
                                .append("音がならない場合はこちら").reset()
                                .append("]").create()
                                : new ComponentBuilder("[音がならない場合はこちら]").create()
                )).color(ChatColor.BLUE).underlined(declined)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        declined
                                ? new ComponentBuilder()
                                .append("あなたはサーバーリソースパックを無効にしています。").color(ChatColor.RED)
                                .append("一旦ログアウトし、サーバーのアドレスを入力する画面でサーバーリソースパックを有効にしてください。").color(ChatColor.GREEN)
                                .create()
                                : new ComponentBuilder()
                                .append("クリックでサーバーリソースパックを読み込みます。").color(ChatColor.GREEN)
                                .create()
                ))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida resourcepack"))
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
    public IState onReady(StateContainer state, int total, int ready) {
        Player player = state.data.player;

        player.sendActionBar(String.format("他のプレイヤーがあなたの準備を待機しています (%d/%d)", ready, total));

        // 人数が揃ったら開始
        if (ready >= total)
            return new CountdownState();

        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed, String buffer) {
        if ("".equals(typed))
            return new CountdownWaitState();

        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        player.sendTitle(new Title(
                new ComponentBuilder("ⓘ チャット画面を使います").bold(true).color(ChatColor.BLUE).create(),
                new ComponentBuilder("「/ 」スラッシュを押してからスペースを押すとスタートします").bold(false).color(ChatColor.AQUA).create(),
                10, 0, 10));

        if (state.titleBgmCount++ >= 7) {
            state.titleBgmCount = 0;
            player.playSound(player.getLocation(), "sushida:sushida.op", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
