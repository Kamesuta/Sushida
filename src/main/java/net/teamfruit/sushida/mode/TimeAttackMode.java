package net.teamfruit.sushida.mode;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.StateContainer;
import net.teamfruit.sushida.util.CustomCollectors;
import net.teamfruit.sushida.util.SimpleTask;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeAttackMode implements GameMode {
    public static final GameSettingType SettingCount = new GameSettingType("count", "問題数", "問題数", 30, Arrays.asList(10, 20, 30, 60, 120));

    @Override
    public String title() {
        return "タイムアタック";
    }

    @Override
    public List<GameSettingType> getSettingTypes() {
        return Arrays.asList(SettingCount, SettingTimeout);
    }

    @Override
    public boolean isGameOver(StateContainer state) {
        return state.typingLogic.wordRemainingCount() <= 0;
    }

    @Override
    public String getScoreBelowName(StateContainer state) {
        return String.format(
                "%d点 (%d / %d)",
                getDynamicScore(state),
                state.typingLogic.wordDoneCount(),
                state.typingLogic.wordTotalCount()
        );
    }

    @Override
    public int getDynamicScore(StateContainer state) {
        int timeout = state.data.getGroup().getMode().getSetting(state.data.getGroup().getSettings(), SettingTimeout);
        if (timeout > 0)
            return state.clearCount * timeout - Math.round(state.timer.getTime());
        return state.clearCount;
    }

    @Override
    public int getScore(StateContainer state) {
        int timeout = state.data.getGroup().getMode().getSetting(state.data.getGroup().getSettings(), SettingTimeout);
        if (timeout > 0)
            return state.clearCount * timeout - Math.round(state.timer.getTime());
        return -Math.round(state.timer.getTime());
    }

    @Override
    public String getScoreString(StateContainer state) {
        return ChatColor.WHITE + "経過時間:"
                + ChatColor.GREEN + ChatColor.BOLD + String.format("%.0f秒", state.timer.getTime())
                + ChatColor.GRAY + ", "
                + ChatColor.WHITE + "残り枚数:"
                + ChatColor.GREEN + ChatColor.BOLD + String.format("%d皿", state.typingLogic.wordRemainingCount());
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
                int count = getSetting(state.data.getGroup().getSettings(), SettingCount);
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE)
                        .append("タイムアタックコース").color(ChatColor.BLUE)
                        .append(String.format(" 【%d皿】", count)).color(ChatColor.GOLD)
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
                player.playSound(player.getLocation(), "sushida:sushida.cacher", SoundCategory.PLAYERS, 1, 1);
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE)
                        .append(String.valueOf(state.typingLogic.wordTotalCount())).color(ChatColor.GRAY)
                        .append(" 皿のうち・・・").color(ChatColor.GRAY)
                        .create()
                );
            }).append(state -> {
                Player player = state.data.player;
                player.playSound(player.getLocation(), "sushida:sushida.cacher", SoundCategory.PLAYERS, 1, 1);
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE)
                        .append(String.valueOf(state.clearCount)).color(ChatColor.YELLOW)
                        .append(" 皿分のお寿司をゲット！").color(ChatColor.WHITE)
                        .create()
                );
            }).append(state -> {
                Player player = state.data.player;
                player.playSound(player.getLocation(), "sushida:sushida.chin", SoundCategory.PLAYERS, 1, 1);
                player.sendMessage(new ComponentBuilder()
                        .append("      ").color(ChatColor.WHITE).underlined(false)
                        .append(String.format("%.1f", state.timer.getTime())).color(ChatColor.GREEN).underlined(true)
                        .append(" 秒かかりました！").color(ChatColor.WHITE).underlined(true)
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
                player.sendMessage("");
            });

    @Override
    public Iterator<Consumer<StateContainer>> getResultMessageTasks() {
        return messageTask.build();
    }

    @Override
    public ImmutableList<Map.Entry<String, String>> getWords(Group group) {
        int setting = getSetting(group.getSettings(), SettingCount);
        List<Integer> splits = CustomCollectors.splitInt(setting, group.getWord().mappings.size());
        List<Map.Entry<String, ImmutableList<ImmutableList<Map.Entry<String, String>>>>> wordRequiredListByLevel = group.getWord().mappings.entrySet().stream()
                .sorted((a, b) -> {
                    int ai = NumberUtils.toInt(CharMatcher.inRange('0', '9').retainFrom(a.getKey()), -1);
                    int bi = NumberUtils.toInt(CharMatcher.inRange('0', '9').retainFrom(b.getKey()), -1);
                    return Comparator.<Integer>naturalOrder().compare(ai, bi);
                })
                .collect(Collectors.toList());
        return IntStream.range(0, splits.size())
                .mapToObj(i ->
                        wordRequiredListByLevel.get(i).getValue().stream()
                                .collect(CustomCollectors.toRandomPickList(splits.get(i)))
                )
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(ImmutableList.toImmutableList());
    }
}
