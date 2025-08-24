package net.teamfruit.sushida.mode;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.SoundManager;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.StateContainer;
import net.teamfruit.sushida.util.CustomCollectors;
import net.teamfruit.sushida.util.SimpleTask;
import net.teamfruit.sushida.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeLimitMode implements GameMode {
    public static final GameSettingType SettingTime = new GameSettingType("time", "制限時間", "ゲームの制限時間", 120, Arrays.asList(60, 90, 120));
    public static final GameSettingType SettingLevel = new GameSettingType("level", "コース", "1:お手軽コース(2～7文字), 2:おすすめコース(5～10文字), 3:高級コース(9～14文字以上), 0:すべて", 0, Arrays.asList(0, 1, 2, 3));

    @Override
    public String title() {
        return "時間制";
    }

    @Override
    public List<GameSettingType> getSettingTypes() {
        return Arrays.asList(SettingTime, SettingLevel, SettingTimeout);
    }

    @Override
    public boolean isGameOver(StateContainer state) {
        return state.timer.getTime() > getSetting(state.data.getGroup().getSettings(), SettingTime);
    }

    @Override
    public String getScoreBelowName(StateContainer state) {
        return String.format(
                "%,d円 (" + ChatColor.GREEN + "✔%d "
                        + ChatColor.RED + "✗%d" + ChatColor.WHITE + ")",
                getDynamicScore(state),
                state.clearCount,
                state.typingLogic.wordDoneCount() - state.clearCount
        );
    }

    @Override
    public int getScore(StateContainer state) {
        return state.moneyCount;
    }

    @Override
    public String getScoreString(StateContainer state) {
        return ChatColor.WHITE + "残り時間:"
                + (state.timer.getTime() < 10 ? ChatColor.YELLOW : ChatColor.GREEN)
                + ChatColor.BOLD + String.format("%.0f秒", getSetting(state.data.getGroup().getSettings(), SettingTime) - state.timer.getTime())
                + ChatColor.GRAY + ", "
                + ChatColor.WHITE + "金額:"
                + ChatColor.GREEN + ChatColor.BOLD + String.format("%,d円", state.moneyCount);
    }

    private final SimpleTask<StateContainer> messageTask = SimpleTask.<StateContainer>builder()
            .append(state -> {
                Player player = state.data.player;
                player.sendMessage("");
                player.sendMessage(new ComponentBuilder()
                        .append("  -------------------------").color(ChatColor.GRAY)
                        .create()
                );
                player.sendMessage("");
                int time = getSetting(state.data.getGroup().getSettings(), SettingTime);
                int level = getSetting(state.data.getGroup().getSettings(), SettingLevel);
                int levelMoney = level == 1 ? 3000 : level == 3 ? 10000 : 5000;
                String levelName = level == 1 ? ChatColor.GOLD + "お手軽" : level == 3 ? ChatColor.DARK_RED + "高級" : ChatColor.BLUE + "お勧め";
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE)
                        .append(levelName).color(ChatColor.WHITE)
                        .append(" ").color(ChatColor.WHITE)
                        .append(String.format("%,d円コース", levelMoney)).color(ChatColor.WHITE)
                        .append(String.format(" 【%d秒】", time)).color(ChatColor.GOLD)
                        .create()
                );
                player.sendMessage("");
                player.sendMessage(new ComponentBuilder()
                        .append("  -------------------------").color(ChatColor.GRAY)
                        .create()
                );
                player.sendMessage("");
                player.sendMessage(new ComponentBuilder()
                        .append("    ▼成績").color(ChatColor.GOLD)
                        .create()
                );
            }).append(state -> {
                Player player = state.data.player;
                SoundManager.playSound(player, "sushida:sushida.cacher", SoundCategory.PLAYERS, 1, 1);
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE)
                        .append(String.format("%,d", state.moneyCount)).color(ChatColor.YELLOW)
                        .append(" 円分のお寿司をゲット！").color(ChatColor.WHITE)
                        .create()
                );
            }).append(state -> {
                Player player = state.data.player;
                SoundManager.playSound(player, "sushida:sushida.cacher", SoundCategory.PLAYERS, 1, 1);
                int level = getSetting(state.data.getGroup().getSettings(), SettingLevel);
                int levelMoney = level == 1 ? 3000 : level == 3 ? 10000 : 5000;
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE)
                        .append(String.format("%,d 円 払って・・・", levelMoney)).color(ChatColor.GRAY)
                        .create()
                );
            }).append(state -> {
                Player player = state.data.player;
                SoundManager.playSound(player, "sushida:sushida.chin", SoundCategory.PLAYERS, 1, 1);
                int level = getSetting(state.data.getGroup().getSettings(), SettingLevel);
                int levelMoney = level == 1 ? 3000 : level == 3 ? 10000 : 5000;
                if (state.moneyCount > levelMoney)
                    player.sendMessage(new ComponentBuilder()
                            .append("      ").color(ChatColor.WHITE).underlined(false)
                            .append(String.format("%,d", state.moneyCount - levelMoney)).color(ChatColor.GREEN).underlined(true)
                            .append(" 円分お得でした！").color(ChatColor.GREEN).underlined(true)
                            .create()
                    );
                else
                    player.sendMessage(new ComponentBuilder()
                            .append("      ").color(ChatColor.WHITE).underlined(false)
                            .append(String.format("%,d", -(state.moneyCount - levelMoney))).color(ChatColor.GRAY).underlined(true)
                            .append(" 円分損でした・・・").color(ChatColor.GRAY).underlined(true)
                            .create()
                    );
            }).append(state -> {
                Player player = state.data.player;
                player.sendMessage("");
                player.sendMessage(new ComponentBuilder()
                        .append("      正しく打ったキーの数: ").color(ChatColor.WHITE)
                        .append(String.valueOf(state.typeCount)).color(ChatColor.YELLOW)
                        .append(" 回").color(ChatColor.GRAY)
                        .create()
                );
                player.sendMessage(new ComponentBuilder()
                        .append("      平均キータイプ数: ").color(ChatColor.WHITE)
                        .append(String.format("%.1f", state.typeCount / state.realTimer.getTime())).color(ChatColor.YELLOW)
                        .append(" 回/秒").color(ChatColor.GRAY)
                        .create()
                );
                player.sendMessage(new ComponentBuilder()
                        .append("      ミスタイプ数: ").color(ChatColor.WHITE)
                        .append(String.valueOf(state.missCount)).color(ChatColor.YELLOW)
                        .append(" 回").color(ChatColor.GRAY)
                        .create()
                );
                int members = state.data.getGroup().getPlayers().size();
                if (members >= 2)
                    player.sendMessage(new ComponentBuilder()
                            .append("      ランキング: ").color(ChatColor.WHITE)
                            .append(String.valueOf(state.ranking)).color(ChatColor.YELLOW)
                            .append("位").color(ChatColor.GRAY)
                            .append("(").color(ChatColor.GRAY)
                            .append(String.valueOf(members)).color(ChatColor.WHITE)
                            .append("人中)").color(ChatColor.GRAY)
                            .create()
                    );
                // グローバルランキング算出
                state.data.getGroup().getRanking().ifPresent(ranking -> {
                    Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
                    Objective objective = ranking.getOrCreateObjective(sc);
                    GameMode mode = state.data.getGroup().getMode();
                    Set<String> entries = sc.getEntries();
                    List<Integer> board = entries.stream()
                            .map(objective::getScore)
                            .filter(Score::isScoreSet)
                            .map(Score::getScore)
                            .sorted(mode.getScoreComparator())
                            .collect(Collectors.toList());
                    int myScore = objective.getScore(state.data.player.getName()).getScore();
                    int rank = board.indexOf(myScore) + 1;
                    player.sendMessage(new ComponentBuilder()
                            .append("      グローバルランキング: ").color(ChatColor.WHITE)
                            .append(String.valueOf(rank)).color(ChatColor.YELLOW)
                            .append("位").color(ChatColor.GRAY)
                            .append("(").color(ChatColor.GRAY)
                            .append(String.valueOf(board.size())).color(ChatColor.WHITE)
                            .append("人中)").color(ChatColor.GRAY)
                            .append(state.rankingUpdated
                                    ? new ComponentBuilder(" 自己新記録！").color(ChatColor.YELLOW).create()
                                    : new ComponentBuilder("").create())
                            .create()
                    );
                });
                player.sendMessage("");
            });

    @Override
    public Iterator<Consumer<StateContainer>> getResultMessageTasks() {
        return messageTask.build();
    }

    @Override
    public ImmutableList<Map.Entry<String, String>> getWords(Group group) {
        int level = getSetting(group.getSettings(), SettingLevel);
        List<Map.Entry<String, ImmutableList<ImmutableList<Map.Entry<String, String>>>>> wordRequiredListByLevel = group.getWord().mappings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
        switch (level) {
            case 1: {
                // 60秒: 2～7文字
                wordRequiredListByLevel = wordRequiredListByLevel.stream()
                        .filter(e -> {
                            int cnt = NumberUtils.toInt(CharMatcher.inRange('0', '9').retainFrom(e.getKey()), -1);
                            return (cnt <= 7);
                        })
                        .collect(Collectors.toList());
                break;
            }
            case 2: {
                // 90秒: 5～10文字
                wordRequiredListByLevel = wordRequiredListByLevel.stream()
                        .filter(e -> {
                            int cnt = NumberUtils.toInt(CharMatcher.inRange('0', '9').retainFrom(e.getKey()), -1);
                            return (cnt < 0 || (5 <= cnt && cnt <= 10));
                        })
                        .collect(Collectors.toList());
                break;
            }
            case 3: {
                // 120秒: 9～文字
                wordRequiredListByLevel = wordRequiredListByLevel.stream()
                        .filter(e -> {
                            int cnt = NumberUtils.toInt(CharMatcher.inRange('0', '9').retainFrom(e.getKey()), -1);
                            return (cnt < 0 || (9 <= cnt));
                        })
                        .collect(Collectors.toList());
                break;
            }
            case 100: {
                // デバッグ用
                return Stream.of("かっぱ", "にっぱー", "っ", "っぱ", "ぴっ").map(e -> new AbstractMap.SimpleEntry<>(e, e)).collect(ImmutableList.toImmutableList());
            }
        }
        return wordRequiredListByLevel.stream()
                .flatMap(e -> e.getValue().stream())
                .collect(CustomCollectors.toShuffledList()).stream()
                .flatMap(Collection::stream)
                .collect(ImmutableList.toImmutableList());
    }
}
