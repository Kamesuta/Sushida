package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

public class PlayState implements IState {
    private KeyedBossBar bossBar;

    @Override
    public IState onEnter(StateContainer state) {
        if (bossBar == null)
            bossBar = Bukkit.getBossBar(state.bossKey);
        if (bossBar == null)
            bossBar = Bukkit.createBossBar(state.bossKey, state.data.player.getName(), BarColor.GREEN, BarStyle.SEGMENTED_12);

        bossBar.addPlayer(state.data.player);
        bossBar.setVisible(true);

        state.timer.resume();

        onType(state, "", "");

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
        Player player = state.data.player;

        boolean type = state.typingLogic.type(typed);
        if (!"".equals(typed)) {
            if (type) {
                // OK
                player.playSound(player.getLocation(), "sushida:sushida.k", SoundCategory.PLAYERS, 1, 1);

                state.scoreCombo++;

                if (state.scoreCombo % 30 == 0) {
                    player.playSound(player.getLocation(), "sushida:sushida.kin", SoundCategory.PLAYERS, 1, 1);

                    state.scoreCount++;
                }

                if (state.scoreCombo >= 120) {
                    state.scoreCombo = 0;
                    state.scoreCount += 3;
                }
            } else {
                // NG
                player.playSound(player.getLocation(), "sushida:sushida.miss", SoundCategory.PLAYERS, 1, 1);

                state.scoreCombo = 0;
                state.missCount++;
            }
        }

        if (state.typingLogic.isNextTiming()) {
            // Next
            player.playSound(player.getLocation(), "sushida:sushida.coin", SoundCategory.PLAYERS, 1, 1);

            state.doneCount++;

            if (buffer.length() > 220)
                return new EnterState();
        }

        bossBar.setTitle(state.typingLogic.getRequiredKanji());
        bossBar.setProgress(state.scoreCombo / 30d / 4);

        player.sendTitle(new Title(
                new ComponentBuilder()
                        .append(state.typingLogic.getRequiredHiragana().substring(0, state.typingLogic.getRequiredHiragana().length() - state.typingLogic.getRemainingRequiredHiraganaVisual().length())).color(ChatColor.WHITE)
                        .append(state.typingLogic.getRemainingRequiredHiraganaVisual()).color(ChatColor.GRAY)
                        .create(),
                new ComponentBuilder()
                        .append(state.typingLogic.getTypedTotalRomaji()).color(ChatColor.WHITE)
                        .append(state.typingLogic.getTypedRomaji()).color(ChatColor.GRAY)
                        .create(),
                0, 10000, 0));

        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PauseState();
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        //player.sendActionBar(ChatColor.WHITE + state.typingLogic.getTypedTotalRomaji() + ChatColor.GRAY + state.typingLogic.getTypedRomaji());

        if (state.bgmCount++ >= 4) {
            state.bgmCount = 0;
            player.playSound(player.getLocation(), "sushida:sushida.bgm", SoundCategory.RECORDS, 1, 1);
        }

        return null;
    }
}
