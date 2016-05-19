package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Parkour command class.
 * 
 * @author LeonTG77
 */
public class ParkourCommand extends UHCCommand {
    private final Main plugin;

    private final Settings settings;
    private final Parkour parkour;

    public ParkourCommand(Main plugin, Settings settings, Parkour parkour) {
        super("parkour", "<timer|checkpoint|restart|leave>");

        this.plugin = plugin;

        this.settings = settings;
        this.parkour = parkour;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can use the parkour.");
        }

        Player player = (Player) sender;
        State state = State.getState();

        if (state == State.SCATTER || state == State.INGAME || state == State.ENDING) {
            throw new CommandException("You can't do parkour commands while a game is running.");
        }

        if (args.length == 0) {
            return false;
        }

        switch (args[0].toLowerCase()) {
        case "timer":
            if (!parkour.isParkouring(player)) {
                throw new CommandException("You are currently not parkouring.");
            }

            player.sendMessage(Parkour.PREFIX + "You have used: §a" + DateUtils.formatDateDiff(parkour.getStartTime(player).getTime()));
            return true;
        case "checkpoint":
            if (!parkour.isParkouring(player)) {
                throw new CommandException("You are currently not parkouring.");
            }

            player.sendMessage(Parkour.PREFIX + "You teleported to your last checkpoint.");
            player.teleport(parkour.getLocation(parkour.getCheckpoint(player)), TeleportCause.UNKNOWN);
            return true;
        case "restart":
            if (!parkour.isParkouring(player)) {
                throw new CommandException("You are currently not parkouring.");
            }

            player.sendMessage(Parkour.PREFIX + "You restarted the parkour.");

            player.teleport(parkour.getLocation(0), TeleportCause.UNKNOWN);
            parkour.addPlayer(player);
            return true;
        case "leave":
            if (!parkour.isParkouring(player)) {
                throw new CommandException("You are currently not parkouring.");
            }

            player.sendMessage(Parkour.PREFIX + "You have left the parkour.");

            player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
            parkour.removePlayer(player);
            return true;
        case "setstart":
            if (!player.hasPermission(getPermission() + ".admin")) {
                return false;
            }

            saveLocation("parkour.spawnpoint", player.getLocation());
            player.sendMessage(Parkour.PREFIX + "You saved the startpoint location.");

            player.chat("/text &6Start Parkour");
            return true;
        case "setpoint1":
            if (!player.hasPermission(getPermission() + ".admin")) {
                return false;
            }

            saveLocation("parkour.checkpoint.1", player.getLocation());
            player.sendMessage(Parkour.PREFIX + "You saved the checkpoint 1 location.");

            player.chat("/text &fCheckpoint &a#1");
            return true;
        case "setpoint2":
            if (!player.hasPermission(getPermission() + ".admin")) {
                return false;
            }

            saveLocation("parkour.checkpoint.2", player.getLocation());
            player.sendMessage(Parkour.PREFIX + "You saved the checkpoint 2 location.");

            player.chat("/text &fCheckpoint &a#2");
            return true;
        case "setpoint3":
            if (!player.hasPermission(getPermission() + ".admin")) {
                return false;
            }

            saveLocation("parkour.checkpoint.3", player.getLocation());
            player.sendMessage(Parkour.PREFIX + "You saved the checkpoint 3 location.");

            player.chat("/text &fCheckpoint &a#3");
            return true;
        case "setend":
            if (!player.hasPermission(getPermission() + ".admin")) {
                return false;
            }

            saveLocation("parkour.endpoint", player.getLocation());
            player.sendMessage(Parkour.PREFIX + "You saved the endpoint location.");

            player.chat("/text &6Finish Parkour");
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("timer");
            toReturn.add("checkpoint");
            toReturn.add("restart");
            toReturn.add("leave");

            if (sender.hasPermission(getPermission() + ".admin")) {
                toReturn.add("setstart");
                toReturn.add("setend");
                toReturn.add("setpoint1");
                toReturn.add("setpoint2");
                toReturn.add("setpoint3");
            }
        }

        return toReturn;
    }

    /**
     * Save the given location at the given config path.
     *
     * @param path The config path to save at.
     * @param loc The location to save.
     */
    private void saveLocation(final String path, final Location loc) {
        FileConfiguration config = settings.getConfig();

        config.set(path + ".world", loc.getWorld().getName());

        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());

        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());

        settings.saveConfig();
        parkour.loadLocations();
    }
}