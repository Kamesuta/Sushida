package net.teamfruit.sushida;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class GameLogic implements CommandExecutor, Listener {
    private final Sushida sushida;

    public GameLogic(Sushida sushida) {
        this.sushida = sushida;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("プレイヤーのみ実行可能です。");
            return true;
        }
        Player player = (Player) sender;

        player.sendTitle("一時停止", "再開するには「/ (スラッシュ・スペース)」と入力してください。", 0, 100, 0);

        return true;
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
