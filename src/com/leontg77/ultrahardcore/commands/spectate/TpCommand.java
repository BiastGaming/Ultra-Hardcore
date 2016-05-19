package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Tp command class
 * 
 * @author LeonTG77
 */
public class TpCommand extends UHCCommand {
    private final SpecManager spec;

    public TpCommand(SpecManager spec) {
        super("tp", "<player> [player]");

        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!sender.hasPermission("uhc.tp.bypass") && !spec.isSpectating(sender.getName())) {
            throw new CommandException("You can only do this while spectating.");
        }

        if (args.length == 0) {
            return false;
        }

        Player targetOne = Bukkit.getPlayer(args[0]);

        if (targetOne == null) {
            throw new CommandException("'" + args[0] + "' is not online.");
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can teleport to players.");
            }

            Player player = (Player) sender;

            player.sendMessage(Main.PREFIX + "You teleported to §a" + targetOne.getName() + "§7.");
            player.teleport(targetOne);
            return true;
        }

        if (!sender.hasPermission("uhc.tp.other")) {
            throw new CommandException("You cannot teleport players to another player.");
        }

        Player targetTwo = Bukkit.getPlayer(args[1]);

        if (targetTwo == null) {
            throw new CommandException("'" + args[1] + "' is not online.");
        }

        sender.sendMessage(Main.PREFIX + "You teleported §a" + targetOne.getName() + "§7 to §a" + targetTwo.getName() + "§7.");
        targetOne.teleport(targetTwo);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return allPlayers();
        }

        return new ArrayList<String>();
    }
}