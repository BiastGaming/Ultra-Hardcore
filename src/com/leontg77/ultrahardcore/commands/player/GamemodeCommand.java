package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Gamemode command class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GamemodeCommand extends UHCCommand {
    private final SpecManager spec;

    public GamemodeCommand(SpecManager spec) {
        super("gamemode", "<mode> [player]");

        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        GameMode mode = null;

        try {
            mode = GameMode.getByValue(Integer.parseInt(args[0]));
        } catch (Exception e) {
            for (GameMode modes : GameMode.values()) {
                if (modes.name().startsWith(args[0].toUpperCase())) {
                    mode = modes;
                    break;
                }
            }
        }

        if (mode == null) {
            throw new CommandException("'" + args[0] + "' is not a valid gamemode.");
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can change their own gamemode.");
            }

            Player player = (Player) sender;

            if (spec.isSpectating(sender.getName())) {
                throw new CommandException("You cannot change your gamemode while spectating.");
            }

            player.sendMessage(Main.PREFIX + "You are now in §6" + mode.name().toLowerCase() + " §7mode.");
            player.setGameMode(mode);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            throw new CommandException("'" + args[1] + "' is not online.");
        }

        sender.sendMessage(Main.PREFIX + "You have changed §a" + target.getName() + "'s §7gamemode to §6" + mode.name().toLowerCase() + " §7mode.");
        target.sendMessage(Main.PREFIX + "You are now in §6" + mode.name().toLowerCase() + " §7mode.");

        target.setGameMode(mode);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (GameMode mode : GameMode.values()) {
                toReturn.add(mode.name());
            }
        }

        if (args.length == 2) {
            return allPlayers();
        }

        return toReturn;
    }
}