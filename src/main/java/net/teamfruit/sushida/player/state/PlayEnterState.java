package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

public class PlayEnterState implements IState {
    private KeyedBossBar bossBar;

    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        if (bossBar == null)
            bossBar = Bukkit.getBossBar(state.bossKey);
        if (bossBar == null)
            bossBar = Bukkit.createBossBar(state.bossKey, "Enter", BarColor.RED, BarStyle.SEGMENTED_12);

        bossBar.addPlayer(state.data.player);
        bossBar.setVisible(true);

        player.sendTitle(new Title(
                new ComponentBuilder("Enter").bold(true).color(ChatColor.RED).create(),
                new ComponentBuilder("エンターキーを押してください").bold(false).color(ChatColor.GREEN).create(),
                0, 10000, 0));

        SoundManager.playSound(player, "sushida:sushida.poke", SoundCategory.PLAYERS, 1, 1);

        return null;
    }

    @Override
    public void onExit(StateContainer state) {
        bossBar.setVisible(false);
        bossBar.removeAll();

        Bukkit.removeBossBar(state.bossKey);
    }

    @Override
    public IState onType(StateContainer state, String typed, String buffer) {
        if ("".equals(typed) && buffer.length() < 50)
            return new PlayState();
        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PlayPauseState();
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        if (state.data.getGroup().getMode().isGameOver(state))
            return new ResultWaitState();

        player.sendTitle(new Title(
                new ComponentBuilder("Enter").bold(true).color(ChatColor.RED).create(),
                new ComponentBuilder("エンターキーを押してください").bold(false).color(ChatColor.GREEN).create(),
                5, 10, 5));

        if (state.bgmCount++ >= 4) {
            state.bgmCount = 0;
            SoundManager.playSound(player, "sushida:sushida.bgm", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
