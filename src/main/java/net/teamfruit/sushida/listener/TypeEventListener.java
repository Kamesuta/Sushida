package net.teamfruit.sushida.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.player.PlayerData;
import net.teamfruit.sushida.player.StateContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TypeEventListener implements Listener {

    @EventHandler
    public void onType(AsyncTabCompleteEvent event) {
        String buffer0 = event.getBuffer();
        if (!buffer0.startsWith("/ "))
            return;
        buffer0 = buffer0.substring(2);
        String buffer = buffer0;

        CommandSender sender = event.getSender();
        if (!(sender instanceof Player))
            return;
        Player player = (Player) sender;

        PlayerData playerState = Sushida.logic.states.getPlayerState(player);
        if (!playerState.hasSession())
            return;
        StateContainer state = playerState.getSession();

        if (buffer.length() > state.inputCursor) {
            String newChar = buffer.substring(state.inputCursor);
            // チート対策
            // newChar.chars().forEach(i -> state.apply((s, c) -> s.onType(c, String.valueOf((char) i))));
            state.apply((s, c) -> s.onType(c, String.valueOf(newChar.charAt(0)), buffer));
        } else {
            state.apply((s, c) -> s.onType(c, "", buffer));
        }
        state.inputCursor = buffer.length();
    }

}
