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
        String buffer = event.getBuffer();
        if (!buffer.startsWith("/ "))
            return;
        buffer = buffer.substring(2);

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
            state.inputCursor = buffer.length();
            newChar.chars().forEach(i -> state.apply((s, c) -> s.onType(c, String.valueOf((char) i))));
        } else {
            state.apply((s, c) -> s.onType(c, ""));
        }
    }

}
