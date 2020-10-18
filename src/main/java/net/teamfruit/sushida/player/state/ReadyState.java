package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class ReadyState implements IState {
    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        // シングルプレイのときは飛ばす
        if (state.data.getGroup().getMembers().isEmpty())
            return new PlayState();

        player.playSound(player.getLocation(), "sushida:sushida.open", SoundCategory.PLAYERS, 1, 1);

        // 準備状況計算
        int total = state.data.getGroup().getPlayers().size();
        int ready = (int) state.data.getGroup().getPlayers().stream()
                .filter(e -> e.getSession().getState() instanceof ReadyState)
                .count();

        // 他プレイヤーにイベント通知
        state.data.getGroup().getPlayers().stream()
                .filter(e -> e != state.data)
                .forEach(e -> e.getSession().apply((s, c) -> s.onReady(c, total, ready)));

        // 自プレイヤーにイベント通知
        return onReady(state, total, ready);
    }

    @Override
    public void onExit(StateContainer state) {
        Player player = state.data.player;

        player.stopSound("sushida:sushida.op", SoundCategory.RECORDS);
    }

    @Override
    public IState onReady(StateContainer state, int total, int ready) {
        Player player = state.data.player;

        boolean isOwner = state.data.getGroup().hasPermission(state.data);
        player.sendTitle(new Title(
                new ComponentBuilder(String.format("他のプレイヤーを待機中 (%d/%d)", ready, total)).bold(true).color(ChatColor.BLUE).create(),
                new ComponentBuilder(isOwner ? "スペースキーで強制開始" : "しばらくお待ち下さい").bold(false).color(ChatColor.GREEN).create(),
                0, 10000, 0));

        // 人数が揃ったら開始
        if (ready >= total)
            return new CountdownState();

        return null;
    }

    @Override
    public IState onType(StateContainer state, String typed, String buffer) {
        Player player = state.data.player;

        boolean isOwner = state.data.getGroup().hasPermission(state.data);
        if (isOwner && " ".equals(typed)) {
            player.playSound(player.getLocation(), "sushida:sushida.open", SoundCategory.PLAYERS, 1, 1);

            // 他プレイヤー開始
            state.data.getGroup().getPlayers().stream()
                    .filter(e -> e != state.data)
                    .forEach(e -> e.getSession().apply(StateContainer.supply(CountdownState::new)));

            // 自プレイヤー開始
            return new CountdownState();
        }

        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        if (state.titleBgmCount++ >= 7) {
            state.titleBgmCount = 0;
            player.playSound(player.getLocation(), "sushida:sushida.op", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}