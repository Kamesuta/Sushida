package net.teamfruit.sushida.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.Group;
import net.teamfruit.sushida.player.PlayerData;
import net.teamfruit.sushida.player.StateContainer;
import net.teamfruit.sushida.player.state.TitleState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerEventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // player変数を新しいエンティティに更新
        Player player = event.getPlayer();
        PlayerData state = Sushida.logic.states.getPlayerState(player);
        state.player = player;

        // ネーム生成
        new BukkitRunnable() {
            @Override
            public void run() {
                Sushida.belowName.spawn(state);
            }
        }.runTaskLater(Sushida.plugin, 10);

        // リソースパック
        if (state.hasSession() && !player.hasResourcePack())
            Sushida.resourcePack.apply(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // 部屋に参加者がいたらホストを変更
        PlayerData state = Sushida.logic.states.getPlayerState(event.getPlayer());
        Group group = state.getGroup();
        if (group.isOwner(state))
            group.getMembers().stream().findFirst().ifPresent(group::setOwner);

        // ネーム削除
        //Sushida.belowName.despawn(state);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        // ネーム削除
        Sushida.belowName.checkAndRemove(event.getEntity());
    }

    @EventHandler
    public void onResourcePack(PlayerResourcePackStatusEvent event) {
        // リソースパック警告
        if (event.getStatus() != PlayerResourcePackStatusEvent.Status.DECLINED)
            return;

        Player player = event.getPlayer();
        PlayerData state = Sushida.logic.states.getPlayerState(event.getPlayer());

        if (!state.hasSession())
            return;

        state.getSession().apply(StateContainer.ifClass(TitleState.class, (s, c) -> {
            player.sendMessage(new ComponentBuilder()
                    .append("    ")
                    .append(new TextComponent(
                            new ComponentBuilder("[").reset()
                                    .append("(").color(ChatColor.RED).bold(true)
                                    .append("(").color(ChatColor.YELLOW).bold(true)
                                    .append("!").color(ChatColor.RED).bold(true)
                                    .append(")").color(ChatColor.YELLOW).bold(true)
                                    .append(")").color(ChatColor.RED).bold(true)
                                    .append("サーバーリソースパックを有効にしてください！").reset()
                                    .append("]").create()
                    )).color(ChatColor.YELLOW).underlined(true).bold(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder()
                                    .append("あなたはサーバーリソースパックを無効にしています。").color(ChatColor.RED)
                                    .append("一旦ログアウトし、サーバーのアドレスを入力する画面でサーバーリソースパックを有効にしてください。").color(ChatColor.GREEN)
                                    .create()
                    ))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sushida resourcepack"))
                    .create()
            );
            player.sendMessage("");

            return null;
        }));
    }

}
