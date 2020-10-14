package net.teamfruit.sushida.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.teamfruit.sushida.logic.GameLogic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TypeEventListener implements Listener {

    private final GameLogic logic;

    public TypeEventListener(GameLogic logic) {
        this.logic = logic;
    }

    @EventHandler
    public void onType(AsyncTabCompleteEvent event) {
        String buffer = event.getBuffer();
        if (!buffer.startsWith("/ "))
            return;

        CommandSender sender = event.getSender();
        if (!(sender instanceof Player))
            return;
        Player player = (Player) sender;

        player.sendTitle("ローマ字", buffer, 0, 100, 0);
    }

}
