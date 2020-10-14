package net.teamfruit.sushida.listener;

import net.teamfruit.sushida.logic.GameLogic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class ManageCommandListener implements CommandExecutor, TabCompleter {

    private final GameLogic logic;

    public ManageCommandListener(GameLogic logic) {
        this.logic = logic;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
    
}
