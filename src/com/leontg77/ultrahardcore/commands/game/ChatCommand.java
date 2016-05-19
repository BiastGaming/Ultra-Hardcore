package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Chat command class.
 * 
 * @author LeonTG77
 */
public class ChatCommand extends UHCCommand {
    private final Game game;

    public ChatCommand(Game game) {
        super("chat", "<clear|mute>");

        this.game = game;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args[0].equalsIgnoreCase("mute")) {
            if (game.isMuted()) {
                PlayerUtils.broadcast(Main.PREFIX + "The chat has been enabled.");
                game.setMuted(false);
                return true;
            }

            PlayerUtils.broadcast(Main.PREFIX + "The chat has been disabled.");
            game.setMuted(true);
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < 150; i++) {
                    online.sendMessage("§0");
                }
            }

            PlayerUtils.broadcast(Main.PREFIX + "The chat has been cleared.");
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("clear");
            toReturn.add("mute");
        }

        return toReturn;
    }
}