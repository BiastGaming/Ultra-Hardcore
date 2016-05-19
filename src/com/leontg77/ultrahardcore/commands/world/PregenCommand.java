package com.leontg77.ultrahardcore.commands.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Pregen command class.
 * 
 * @author LeonTG77
 */
public class PregenCommand extends UHCCommand {

    public PregenCommand() {
        super("pregen", "<world|cancel|pause> <diameter> [force]");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("cancel")) {
                PlayerUtils.broadcast(Main.PREFIX + "Cancelling pregen.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill cancel");
                return true;
            }

            if (args[0].equalsIgnoreCase("pause")) {
                PlayerUtils.broadcast(Main.PREFIX + "Pausing pregen.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill pause");
                return true;
            }
        }

        if (args.length < 2) {
            return false;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            throw new CommandException("The world '" + args[0] + "' does not exist.");
        }

        Location center = world.getWorldBorder().getCenter();

        int radius = parseInt(args[1], "diameter") / 2;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " set " + radius + " " + center.getX() + " " + center.getZ());

        if (args.length > 2) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " fill 420 208 " + parseBoolean(args[2]));
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " fill 420");
        }

        radius = radius * 2;

        PlayerUtils.broadcast(Main.PREFIX + "Starting pregen of world '§a" + world.getName() + "§7' with size of §6" + radius + "x" + radius + "§7.");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill confirm");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (World world : Bukkit.getWorlds()) {
                toReturn.add(world.getName());
            }

            toReturn.add("pause");
            toReturn.add("cancel");
        }

        if (args.length == 2) {
            World world = Bukkit.getWorld(args[0]);

            if (world != null) {
                toReturn.add("" + ((int) world.getWorldBorder().getSize()));
            }
        }

        if (args.length == 3) {
            toReturn.add("true");
            toReturn.add("false");
        }

        return toReturn;
    }
}