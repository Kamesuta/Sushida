package net.teamfruit.sushida.player.state;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.mode.GameMode;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

public class PlayState implements IState {
    private KeyedBossBar bossBar;
    private KeyedBossBar progressBar;

    private boolean koto;

    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        // チーム
        state.data.getGroup().getGroupTeamPlay().addEntry(player.getName());

        if (progressBar == null)
            progressBar = Bukkit.getBossBar(state.progressKey);
        if (progressBar == null)
            progressBar = Bukkit.createBossBar(state.progressKey, "", BarColor.BLUE, BarStyle.SEGMENTED_12);
        progressBar.addPlayer(state.data.player);
        progressBar.setVisible(true);

        if (bossBar == null)
            bossBar = Bukkit.getBossBar(state.bossKey);
        if (bossBar == null)
            bossBar = Bukkit.createBossBar(state.bossKey, "", BarColor.GREEN, BarStyle.SEGMENTED_12);
        bossBar.addPlayer(state.data.player);
        bossBar.setVisible(true);

        state.timer.resume();
        state.realTimer.resume();
        state.sushiTimer.resume();

        onType(state, "", "");

        return null;
    }

    @Override
    public void onExit(StateContainer state) {
        progressBar.setVisible(false);
        progressBar.removeAll();
        Bukkit.removeBossBar(state.progressKey);

        bossBar.setVisible(false);
        bossBar.removeAll();
        Bukkit.removeBossBar(state.bossKey);

        state.data.player.sendExperienceChange(state.data.player.getExp(), state.data.player.getLevel());
    }

    @Override
    public IState onType(StateContainer state, String typed, String buffer) {
        Player player = state.data.player;

        int timeout = state.data.getGroup().getMode().getSetting(state.data.getGroup().getSettings(), GameMode.SettingTimeout);

        // 寿司の時間制限
        if (timeout > 0 && state.sushiTimer.getTime() >= timeout) {
            state.sushiTimer.reset();

            state.typingLogic.genNextWord();
            koto = true;

            if (buffer.length() > 220)
                return new PlayEnterState();
        }

        String word = state.typingLogic.getRequiredHiragana();

        boolean type = state.typingLogic.type(typed);
        if (!"".equals(typed)) {
            if (type) {
                // OK
                SoundManager.playSoundAround(player, "sushida:sushida.k", SoundCategory.PLAYERS, 1, 1);

                state.scoreCombo++;

                if (state.scoreCombo % 30 == 0) {
                    SoundManager.playSoundAround(player, "sushida:sushida.kin", SoundCategory.PLAYERS, 1, 1);
                    if (state.scoreCombo >= 120) {
                        state.scoreCombo = 0;
                        //state.scoreCount += 3;
                        state.timer.set(state.timer.getTime() - 3);
                    } else if (state.scoreCombo >= 90) {
                        //state.scoreCount += 2;
                        state.timer.set(state.timer.getTime() - 2);
                    } else {
                        //state.scoreCount++;
                        state.timer.set(state.timer.getTime() - 1);
                    }
                }

                if (state.typingLogic.isNextCharacterTiming())
                    state.typeCount++;
            } else {
                // NG
                SoundManager.playSoundAround(player, "sushida:sushida.miss", SoundCategory.PLAYERS, 1, 1);

                state.scoreCombo = 0;
                state.missCount++;
            }
        }

        if (state.typingLogic.isNextWordTiming()) {
            // Next
            SoundManager.playSoundAround(player, "sushida:sushida.coin", SoundCategory.PLAYERS, 1, 1);
            koto = true;

            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 30, .2, .1, .2);

            state.sushiTimer.reset();

            state.clearCount++;
            state.moneyCount += (word.length() >= 13) ? 500 : (word.length() >= 10) ? 380 : (word.length() >= 7) ? 240 : (word.length() >= 5) ? 180 : 100;

            if (buffer.length() > 220)
                return new PlayEnterState();
        }

        if (state.data.getGroup().getMode().isGameOver(state))
            return new ResultWaitState();

        progressBar.setTitle(String.format("%d / %d",
                state.typingLogic.wordDoneCount(),
                state.typingLogic.wordTotalCount()));
        progressBar.setProgress(state.typingLogic.wordDoneCount() / (double) state.typingLogic.wordTotalCount());

        bossBar.setTitle(state.typingLogic.getRequiredKanji());
        bossBar.setProgress(state.scoreCombo / 30d / 4);

        state.data.entity.setTypingText(
                ChatColor.WHITE + state.typingLogic.getTypedHiragana()
                        + ChatColor.WHITE + ChatColor.UNDERLINE + state.typingLogic.getTypedRomaji());

        player.sendTitle(new Title(
                new ComponentBuilder()
                        .append(state.typingLogic.getRequiredHiragana().substring(0, state.typingLogic.getRequiredHiragana().length() - state.typingLogic.getRemainingRequiredHiraganaVisual().length())).color(ChatColor.GRAY)
                        .append(state.typingLogic.getRemainingRequiredHiraganaVisual()).color(ChatColor.WHITE)
                        .create(),
                new ComponentBuilder()
                        .append(state.typingLogic.getTypedTotalRomaji()).color(ChatColor.GRAY)
                        .append(state.typingLogic.getTypedRomaji()).color(ChatColor.WHITE)
                        .create(),
                0, 10000, 0));

        updateActionBar(state);

        return null;
    }

    @Override
    public IState onPause(StateContainer state) {
        return new PlayPauseState();
    }

    private void updateActionBar(StateContainer state) {
        state.data.player.sendActionBar(state.data.getGroup().getMode().getScoreString(state));
        state.data.entity.setScoreText(state.data.getGroup().getMode().getScoreBelowName(state));
    }

    @Override
    public IState onTick(StateContainer state) {
        Player player = state.data.player;

        state.data.getGroup().getScoreLeaderboard()
                .getScore(state.data.player.getName())
                .setScore(state.data.getGroup().getMode().getDynamicScore(state));

        if (state.data.getGroup().getMode().isGameOver(state))
            return new ResultWaitState();

        if (koto) {
            koto = false;
            SoundManager.playSoundAround(player, "sushida:sushida.koto", SoundCategory.PLAYERS, 1, 1);
        }

        updateActionBar(state);

        if (state.bgmCount++ >= 4) {
            state.bgmCount = 0;
            SoundManager.playSound(player, "sushida:sushida.bgm", SoundCategory.RECORDS, 1, 1);
        }

        IState newState = null;

        int timeout = state.data.getGroup().getMode().getSetting(state.data.getGroup().getSettings(), GameMode.SettingTimeout);

        // 寿司の時間制限
        if (timeout > 0) {
            if (state.sushiTimer.getTime() >= timeout)
                newState = onType(state, "", "");
            state.data.player.sendExperienceChange(Math.min((float) Math.floor(state.sushiTimer.getTime() + .1f) / timeout, 1), 0);
        }

        return newState;
    }
}
