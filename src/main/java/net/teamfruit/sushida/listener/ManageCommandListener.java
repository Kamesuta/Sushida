package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.mode.GameMode;
import net.teamfruit.sushida.mode.GameModes;
import net.teamfruit.sushida.mode.GameSettingType;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.PlayerData;
import net.teamfruit.sushida.util.TitleUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManageCommandListener implements CommandExecutor, TabCompleter {

    private String get(List<String> args, int index) {
        if (args.size() > index)
            return args.get(index);
        return null;
    }

    private List<String> getFrom(List<String> args, int index) {
        if (args.size() > index)
            return args.subList(index, args.size());
        return Collections.emptyList();
    }

    private boolean validateGroupOwner(PlayerData state, String actionName) {
        if (!state.getGroup().hasPermission(state)) {
            state.player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("グループのホストのみ" + actionName + "ができます").color(ChatColor.RED)
                    .create()
            );
            return false;
        }
        return true;
    }

    private boolean validateSession(PlayerData state, String actionName) {
        if (state.hasSession()) {
            state.player.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("プレイ中に" + actionName + "はできません").color(ChatColor.RED)
                    .create()
            );
            return false;
        }
        return true;
    }

    private List<Player> getPlayers(CommandSender sender, List<String> args) {
        List<Player> players = new ArrayList<>();
        if (args.size() > 0) {
            if ("@a".equals(args.get(0)))
                players.addAll(Bukkit.getOnlinePlayers());
            else
                players.addAll(args.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList()));
        } else {
            players.add((Player) sender);
        }
        return players;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append("プレイヤーのみ実行可能です。").color(ChatColor.RED)
                    .create()
            );
            return true;
        }
        Player player = (Player) sender;
        PlayerData state = Sushida.logic.states.getPlayerState(player);

        player.sendMessage("");

        List<String> args = Arrays.asList(arg);
        String arg0 = get(args, 0);
        String arg1 = get(args, 1);
        String arg2 = get(args, 2);
        if (arg0 != null) {
            switch (arg0) {
                case "assign": {
                    if (!validateGroupOwner(state, "メンバーの変更"))
                        return true;
                    if (!validateSession(state, "メンバーの変更"))
                        return true;
                    if (!player.hasPermission("sushida.other")) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("他人の強制追加するためには権限が足りません").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    List<Player> players = getPlayers(player, getFrom(args, 1)).stream()
                            .filter(e -> !e.equals(player))
                            .collect(Collectors.toList());
                    List<String> errored = players.stream().map(Sushida.logic.states::getPlayerState)
                            .filter(e -> !e.join(state.getGroup()))
                            .map(e -> e.player.getName())
                            .collect(Collectors.toList());
                    if (!errored.isEmpty())
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append(errored.stream()
                                        .map(e -> new ComponentBuilder(e).color(ChatColor.YELLOW).create())
                                        .collect(TitleUtils.joining(new ComponentBuilder(", ").color(ChatColor.GRAY).create()))).color(ChatColor.WHITE)
                                .append("を参加させられませんでした").color(ChatColor.RED)
                                .create()
                        );
                    if (players.size() > errored.size())
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append((players.size() - errored.size()) + "人のプレイヤー").color(ChatColor.WHITE)
                                .append("を参加させました").color(ChatColor.GREEN)
                                .create()
                        );
                    break;
                }
                case "kick": {
                    if (!validateGroupOwner(state, "メンバーの変更"))
                        return true;
                    if (!validateSession(state, "メンバーの変更"))
                        return true;
                    List<Player> players = getPlayers(player, getFrom(args, 1));
                    players.stream().map(Sushida.logic.states::getPlayerState)
                            .filter(e -> e.getGroup().equals(state.getGroup()))
                            .forEach(PlayerData::leave);
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append(players.size() + "人").color(ChatColor.WHITE)
                            .append("を退出させました").color(ChatColor.GREEN)
                            .create()
                    );
                    break;
                }
                case "invite": {
                    if (!validateSession(state, "招待"))
                        return true;
                    List<Player> players = getPlayers(player, getFrom(args, 1)).stream()
                            .filter(e -> !e.equals(player))
                            .collect(Collectors.toList());
                    List<String> errored = players.stream().map(Sushida.logic.states::getPlayerState)
                            .filter(PlayerData::hasSession)
                            .map(e -> e.player.getName())
                            .collect(Collectors.toList());
                    if (!errored.isEmpty())
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append(errored.stream()
                                        .map(e -> new ComponentBuilder(e).color(ChatColor.YELLOW).create())
                                        .collect(TitleUtils.joining(new ComponentBuilder(", ").color(ChatColor.GRAY).create()))).color(ChatColor.WHITE)
                                .append("は既にプレイ中です").color(ChatColor.RED)
                                .create()
                        );
                    List<PlayerData> invites = players.stream().map(Sushida.logic.states::getPlayerState)
                            .filter(e -> !e.hasSession())
                            .collect(Collectors.toList());
                    invites.forEach(e -> {
                        e.player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append(player.getName()).color(ChatColor.WHITE)
                                .append(" があなたを寿司打に招待しています。").color(ChatColor.GREEN)
                                .create()
                        );
                        e.player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append(new TextComponent(
                                        new ComponentBuilder()
                                                .append("[クリックで参加]").color(ChatColor.GOLD).bold(true)
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new ComponentBuilder()
                                                                .append("クリックで ").color(ChatColor.GREEN)
                                                                .append(state.getGroup().owner.player.getName()).color(ChatColor.WHITE)
                                                                .append(" の部屋に参加します").color(ChatColor.GREEN)
                                                                .create()
                                                ))
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida join " + state.getGroup().owner.player.getName()))
                                                .create()
                                )).create()
                        );
                    });
                    if (invites.size() > 0)
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append((players.size() - errored.size()) + "人のプレイヤー").color(ChatColor.WHITE)
                                .append("を招待しました").color(ChatColor.GREEN)
                                .create()
                        );

                    break;
                }
                case "join": {
                    if (arg1 == null) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("/sushida join <プレイヤー名>").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    if (!validateSession(state, "メンバーの変更"))
                        return true;
                    Player groupPlayer = Bukkit.getPlayer(arg1);
                    if (groupPlayer == null) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("プレイヤーが見つかりません").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    PlayerData groupState = Sushida.logic.states.getPlayerState(groupPlayer);
                    if (!validateSession(state, "部屋の変更"))
                        return true;
                    Group group = groupState.getGroup();
                    state.destroy();
                    if (state.join(group))
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append(groupPlayer.getName()).color(ChatColor.WHITE)
                                .append("に参加しました").color(ChatColor.GREEN)
                                .create()
                        );
                    else
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append(groupPlayer.getName()).color(ChatColor.WHITE)
                                .append("の参加に失敗しました").color(ChatColor.RED)
                                .create()
                        );
                    break;
                }
                case "leave": {
                    if (!validateSession(state, "部屋の変更"))
                        return true;
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append(state.getGroup().owner.player.getName()).color(ChatColor.WHITE)
                            .append("から退出しました").color(ChatColor.GREEN)
                            .create()
                    );
                    state.destroy();
                    state.leave();
                    break;
                }
                case "word": {
                    if (!validateGroupOwner(state, "辞書の変更"))
                        return true;
                    if (!validateSession(state, "辞書の変更"))
                        return true;
                    if (!state.getGroup().setWord(arg1)) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("辞書「").color(ChatColor.RED)
                                .append(StringUtils.defaultString(arg1)).color(ChatColor.WHITE)
                                .append("」は存在しません").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append("辞書を「").color(ChatColor.GREEN)
                            .append(arg1).color(ChatColor.WHITE)
                            .append("」に設定しました").color(ChatColor.GREEN)
                            .create()
                    );
                    break;
                }
                case "rule": {
                    if (!validateGroupOwner(state, "ルールの変更"))
                        return true;
                    if (!validateSession(state, "ルールの変更"))
                        return true;
                    if (!state.getGroup().setMode(arg1)) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("ルール「").color(ChatColor.RED)
                                .append(StringUtils.defaultString(arg1)).color(ChatColor.WHITE)
                                .append("」は存在しません").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append("ルールを「").color(ChatColor.GREEN)
                            .append(state.getGroup().getMode().title()).color(ChatColor.WHITE)
                            .append("」に設定しました").color(ChatColor.GREEN)
                            .create()
                    );
                    break;
                }
                case "setting": {
                    if (!validateGroupOwner(state, "詳細設定の変更"))
                        return true;
                    if (!validateSession(state, "詳細設定の変更"))
                        return true;
                    GameSettingType type = state.getGroup().getMode().getSettingType(arg1);
                    if (type == null) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("設定「").color(ChatColor.RED)
                                .append(StringUtils.defaultString(arg1)).color(ChatColor.WHITE)
                                .append("」は存在しません").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    int value;
                    try {
                        value = Integer.parseInt(arg2);
                        if (value <= 0)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        player.sendMessage(new ComponentBuilder()
                                .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                                .append("数値が正しくありません").color(ChatColor.RED)
                                .create()
                        );
                        return true;
                    }
                    state.getGroup().getMode().setSetting(type, value);
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append("設定「").color(ChatColor.GREEN)
                            .append(type.title).color(ChatColor.WHITE)
                            .append("」を「").color(ChatColor.GREEN)
                            .append(String.valueOf(value)).color(ChatColor.WHITE)
                            .append("」に設定しました").color(ChatColor.GREEN)
                            .create()
                    );
                    break;
                }
                case "start": {
                    if (!validateGroupOwner(state, "ゲームの開始"))
                        return true;
                    state.getGroup().init();
                    state.getGroup().getPlayers().forEach(PlayerData::create);
                    return true;
                }
                case "stop": {
                    if (!validateGroupOwner(state, "ゲームの終了"))
                        return true;
                    state.getGroup().getPlayers().forEach(e -> {
                        e.destroy();
                        e.player.sendMessage("寿司打を終了しました。");
                    });
                    return true;
                }
                case "restart": {
                    if (!validateGroupOwner(state, "ゲームの開始"))
                        return true;
                    state.getGroup().getPlayers().forEach(e -> {
                        e.destroy();
                        e.player.sendMessage("寿司打を終了しました。");
                    });
                    state.getGroup().init();
                    state.getGroup().getPlayers().forEach(PlayerData::create);
                    return true;
                }
                default: {
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append("不明なコマンドです").color(ChatColor.RED)
                            .create()
                    );
                    player.sendMessage(new ComponentBuilder()
                            .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                            .append("/sushida").color(ChatColor.GRAY)
                            .append(" でメニューを表示します").color(ChatColor.GREEN)
                            .create()
                    );
                    return true;
                }
            }
        }

        // タイトル表示
        TitleUtils.showTitle(player);

        {
            player.sendMessage(new ComponentBuilder()
                    .append("▼ゲーム設定").color(ChatColor.GOLD).bold(true)
                    .append("").color(ChatColor.GREEN)
                    .create()
            );
            {
                ComponentBuilder cb = new ComponentBuilder()
                        .append("部屋: ").color(ChatColor.WHITE)
                        .append(state.getGroup().owner.player.getName()).color(ChatColor.YELLOW);
                if (state.getGroup().hasPermission(state))
                    cb.append(new TextComponent(
                            new ComponentBuilder("[+]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("他の部屋に参加します").create()))
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sushida join "))
                                    .create()
                    ));
                else
                    cb.append(new TextComponent(
                            new ComponentBuilder("[-]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("部屋から退出します").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida leave"))
                                    .create()
                    ));
                player.sendMessage(cb.create());
            }
            {
                ComponentBuilder cb = new ComponentBuilder()
                        .append("辞書: ").color(ChatColor.WHITE)
                        .append(state.getGroup().getWordName()).color(ChatColor.YELLOW);
                if (state.getGroup().hasPermission(state))
                    cb.append(new TextComponent(
                            new ComponentBuilder("[+]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("辞書を選択します").create()))
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sushida word "))
                                    .create()
                    ));
                player.sendMessage(cb.create());
            }
            {
                ComponentBuilder cb = new ComponentBuilder()
                        .append("ルール: ").color(ChatColor.WHITE);
                GameMode currentMode = state.getGroup().getMode();
                if (state.getGroup().hasPermission(state)) {
                    cb.append(
                            Arrays.stream(GameModes.values()).map(e -> {
                                if (e.mode.equals(currentMode))
                                    return new ComponentBuilder(new TextComponent(new ComponentBuilder(
                                            new TextComponent(
                                                    new ComponentBuilder("[✓]").color(ChatColor.GREEN).bold(true)
                                                            .append(e.mode.title()).color(ChatColor.YELLOW).bold(false)
                                                            .create()
                                            ))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    new ComponentBuilder().append("現在選択されています").create()))
                                            .create())).create();
                                else
                                    return new ComponentBuilder(new TextComponent(new ComponentBuilder(
                                            new TextComponent(
                                                    new ComponentBuilder("[+]").color(ChatColor.BLUE).bold(true)
                                                            .append(e.mode.title()).color(ChatColor.WHITE).bold(false)
                                                            .create()
                                            ))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    new ComponentBuilder().append("クリックして選択").create()))
                                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida rule " + e.name()))
                                            .create())).create();
                            }).collect(TitleUtils.joining(new ComponentBuilder(", ").color(ChatColor.GRAY).create()))
                    );
                } else {
                    cb.append(currentMode.title()).color(ChatColor.YELLOW);
                }
                player.sendMessage(cb.create());
            }
            for (GameSettingType type : state.getGroup().getMode().getSettingTypes()) {
                ComponentBuilder cb = new ComponentBuilder()
                        .append(type.title).color(ChatColor.WHITE)
                        .append(": ").color(ChatColor.WHITE)
                        .append(String.valueOf(state.getGroup().getMode().getSetting(type))).color(ChatColor.YELLOW);
                if (state.getGroup().hasPermission(state))
                    cb.append(new TextComponent(
                            new ComponentBuilder("[+]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("設定変更").create()))
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sushida setting " + type.name + " "))
                                    .create()
                    ));
                player.sendMessage(cb.create());
            }
            {
                boolean hasPermission = state.getGroup().hasPermission(state);
                ComponentBuilder cb = new ComponentBuilder()
                        .append("メンバー: ").color(ChatColor.WHITE);
                BaseComponent[] comps = state.getGroup().getMembers().stream()
                        .map(e -> e.player.getName())
                        .map(e -> {
                            ComponentBuilder c = new ComponentBuilder(e).color(ChatColor.YELLOW);
                            if (hasPermission)
                                c.append(new TextComponent(
                                        new ComponentBuilder("[-]").color(ChatColor.BLUE).bold(true)
                                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new ComponentBuilder().append(e).append("を追放します").create()))
                                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida kick " + e))
                                                .create()
                                ));
                            return c.create();
                        })
                        .collect(TitleUtils.joining(new ComponentBuilder(", ").color(ChatColor.GRAY).create()));
                if (comps.length > 0)
                    cb.append(comps).color(ChatColor.YELLOW);
                else
                    cb.append("なし").color(ChatColor.GRAY);
                if (hasPermission && player.hasPermission("sushida.other"))
                    cb.append(new TextComponent(
                            new ComponentBuilder("[+]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("メンバーを追加します").create()))
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sushida assign "))
                                    .create()
                    ));
                cb.append(new TextComponent(
                        new ComponentBuilder("[↑]").color(ChatColor.BLUE).bold(true)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder().append("プレイヤーを招待します").create()))
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sushida invite "))
                                .create()
                ));
                player.sendMessage(cb.create());
            }
            if (state.getGroup().hasPermission(state)) {
                ComponentBuilder cb = new ComponentBuilder()
                        .append("ゲーム: ").color(ChatColor.WHITE);
                if (!state.hasSession()) {
                    cb.append(new TextComponent(
                            new ComponentBuilder("[スタート]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("ゲームをスタートします").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida restart"))
                                    .create()
                    ));
                } else {
                    cb.append(new TextComponent(
                            new ComponentBuilder("[ゲーム中止]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("ゲームを強制終了します").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida stop"))
                                    .create()
                    ));
                    cb.append(new TextComponent(
                            new ComponentBuilder("[リスタート]").color(ChatColor.BLUE).bold(true)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder().append("ゲームをリスタートします").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida restart"))
                                    .create()
                    ));
                }
                player.sendMessage(cb.create());
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] arg) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        Player player = (Player) sender;
        PlayerData state = Sushida.logic.states.getPlayerState(player);

        List<String> args = Arrays.asList(arg);
        String arg0 = get(args, 0);
        String arg1 = get(args, 1);
        String arg2 = get(args, 2);
        switch (args.size()) {
            case 1:
                return Stream.of("assign", "invite", "kick", "join", "leave", "word", "rule", "setting", "start", "stop", "restart")
                        .filter(e -> !e.equals("assign") || player.hasPermission("sushida.other"))
                        .filter(e -> arg0 == null || e.startsWith(arg0))
                        .collect(Collectors.toList());
            case 2:
                if ("word".equals(arg0)) {
                    return Sushida.logic.word.keySet().stream()
                            .filter(e -> arg1 == null || e.startsWith(arg1))
                            .collect(Collectors.toList());
                } else if ("rule".equals(arg0)) {
                    return Arrays.stream(GameModes.values())
                            .map(Enum::name)
                            .filter(e -> arg1 == null || e.startsWith(arg1))
                            .collect(Collectors.toList());
                } else if ("setting".equals(arg0)) {
                    return state.getGroup().getMode().getSettingTypes().stream()
                            .map(e -> e.name)
                            .filter(e -> arg1 == null || e.startsWith(arg1))
                            .collect(Collectors.toList());
                } else if ("join".equals(arg0)) {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(e -> arg1 == null || e.startsWith(arg1))
                            .collect(Collectors.toList());
                }
                // Fall through
            case 3:
                if ("setting".equals(arg0)) {
                    GameSettingType type = state.getGroup().getMode().getSettingType(arg1);
                    if (type != null)
                        return type.candidates.stream()
                                .map(String::valueOf)
                                .filter(e -> arg2 == null || e.startsWith(arg2))
                                .collect(Collectors.toList());
                }
                // Fall through
            default:
                if ("assign".equals(arg0) || "invite".equals(arg0)) {
                    Set<PlayerData> members = state.getGroup().getPlayers();
                    return Stream.concat(
                            Stream.of("@a"),
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(e -> members.stream().noneMatch(x -> x.player.equals(e)))
                                    .map(Player::getName)
                                    .filter(e -> arg1 == null || e.startsWith(arg1))
                    ).collect(Collectors.toList());
                } else if ("kick".equals(arg0)) {
                    return state.getGroup().getMembers().stream()
                            .map(e -> e.player.getName())
                            .filter(e -> arg1 == null || e.startsWith(arg1))
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();
        }
    }

}
