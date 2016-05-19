package com.leontg77.ultrahardcore.commands.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * PvP command class.
 * 
 * @author LeonTG77
 */
public class PvPCommand extends UHCCommand {

    public PvPCommand() {
        super("pvp", "<world|global> <on|off>");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            return false;
        }

        boolean enable = parseBoolean(args[1]);

        if (args[0].equalsIgnoreCase("global")) {
            for (World world : Bukkit.getWorlds()) {
                world.setPVP(enable);
            }

            sender.sendMessage(Main.PREFIX + "Global pvp has been " + booleanToString(enable) + ".");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            throw new CommandException("The world '" + args[0] + "' does not exist.");
        }

        sender.sendMessage(Main.PREFIX + "PvP in '§a" + world.getName() + "§7' has been " + booleanToString(enable) + ".");
        world.setPVP(enable);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("global");

            for (World world : Bukkit.getWorlds()) {
                toReturn.add(world.getName());
            }
        }

        if (args.length == 2) {
            toReturn.add("on");
            toReturn.add("off");
        }

        return toReturn;
    }
}