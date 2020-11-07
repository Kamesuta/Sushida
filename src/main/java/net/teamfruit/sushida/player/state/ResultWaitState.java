package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class ResultWaitState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        // チーム
        state.data.getGroup().getGroupTeamResultWait().addEntry(player.getName());

        state.timer.pause();
        state.realTimer.pause();
        state.sushiTimer.pause();

        // クリア
        player.sendTitle("", "", 0, 0, 0);
        player.sendActionBar(" ");

        player.stopSound("sushida:sushida.bgm", SoundCategory.RECORDS);

        // 終了音
        SoundManager.playSoundAround(player, "sushida:sushida.whistle2", SoundCategory.PLAYERS, 1, 1);

        // シングルプレイのときは飛ばす
        if (state.data.getGroup().getMembers().isEmpty())
            return new ResultState();

        // 準備状況計算
        int total = state.data.getGroup().getPlayers().size();
        int ready = (int) state.data.getGroup().getPlayers().stream()
                .map(e -> e.getSession().getState())
                .filter(e -> e instanceof ResultWaitState || e instanceof ResultState)
                .count();

        // 他プレイヤーにイベント通知
        state.data.getGroup().getPlayers().stream()
                .filter(e -> e != state.data)
                .forEach(e -> e.getSession().apply((s, c) -> s.onReady(c, total, ready)));

        // 自プレイヤーにイベント通知
        return onReady(state, total, ready);
    }

    @Override
    public IState onReady(StateContainer state, int total, int ready) {
        Player player = state.data.player;

        player.sendTitle(new Title(
                new ComponentBuilder(String.format("他のプレイヤーを待機中 (%d/%d)", ready, total)).bold(true).color(ChatColor.BLUE).create(),
                new ComponentBuilder("しばらくお待ち下さい").bold(false).color(ChatColor.GREEN).create(),
                0, 10000, 0));

        // 人数が揃ったら開始
        if (ready >= total)
            return new ResultState();

        return null;
    }
}
