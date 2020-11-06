package net.teamfruit.sushida.player.state;

import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.mode.GameMode;
import net.teamfruit.sushida.player.PlayerData;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ResultState implements IState {
    private Iterator<Consumer<StateContainer>> showMessage;

    @Override
    public IState onEnter(StateContainer state) {
        Player player = state.data.player;

        // リーダーボード更新
        String name = player.getName();
        int score = state.data.getGroup().getMode().getScore(state);
        state.data.getGroup().getScoreLeaderboard()
                .getScore(name)
                .setScore(score);
        state.data.getGroup().getTabLeaderboard()
                .getScore(name)
                .setScore(score);

        // グローバルランキング更新
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        state.rankingUpdated = state.data.getGroup().getRanking()
                .map(ranking -> ranking.getOrCreateObjective(sc).getScore(name))
                .map(scoreObject -> {
                    if (scoreObject.getScore() < score) {
                        scoreObject.setScore(score);
                        return true;
                    }
                    return false;
                })
                .orElse(false);

        // ランキング算出
        GameMode mode = state.data.getGroup().getMode();
        List<Integer> board = state.data.getGroup().getPlayers().stream()
                .map(e -> mode.getScore(e.getSession()))
                .sorted(mode.getScoreComparator())
                .collect(Collectors.toList());
        int myScore = mode.getScore(state);
        state.ranking = board.indexOf(myScore) + 1;

        // クリア
        player.sendTitle("", "", 0, 0, 0);
        IntStream.range(0, 20).forEachOrdered(e -> player.sendMessage(""));

        // ガッ
        SoundManager.playSound(player, "sushida:sushida.gan", SoundCategory.PLAYERS, 1, 1);

        showMessage = state.data.getGroup().getMode().getResultMessageTasks();

        return null;
    }

    @Override
    public IState onTick(StateContainer state) {
        if (showMessage.hasNext())
            showMessage.next().accept(state);
        else if (state.data.getGroup().isOwner(state.data)) {
            // ゲーム終了
            state.data.getGroup().getPlayers().forEach(PlayerData::destroy);
            return new NoneState();
        }
        return null;
    }
}
