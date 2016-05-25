package com.leontg77.ultrahardcore.commands.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.WorldCreatorGUI;
import com.leontg77.ultrahardcore.world.WorldManager;
import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine;
import com.leontg77.ultrahardcore.world.antistripmine.WorldData;

/**
 * World command class.
 * 
 * @author LeonTG77
 */
public class WorldCommand extends UHCCommand {
    private final AntiStripmine antiSM;

    private final Settings settings;
    private final Game game;

    private final WorldManager manager;
    private final GUIManager gui;

    public WorldCommand(Game game, Settings settings, AntiStripmine antiSM, GUIManager gui, WorldManager manager) {
        super("world", "");

        this.antiSM = antiSM;

        this.settings = settings;
        this.game = game;

        this.manager = manager;
        this.gui = gui;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player)) {
                    throw new CommandException("Only players can create worlds");
                }

                Player player = (Player) sender;

                if (args.length < 3) {
                    player.sendMessage(Main.PREFIX + "Usage: /world create <name> <mapsize> [seed]");
                    return true;
                }

                WorldCreatorGUI creator = gui.getGUI(WorldCreatorGUI.class);

                if (!creator.get().getViewers().isEmpty()) {
                    throw new CommandException("Someone else is currently making a world.");
                }

                String worldname = args[1].toLowerCase();
                int diameter = parseInt(args[2], "diameter");

                long seed = new Random().nextLong();

                if (args.length > 3) {
                    try {
                        seed = parseLong(args[3]);
                    } catch (Exception e) {
                        seed = (long) args[3].hashCode();
                    }
                }

                player.openInventory(creator.get(worldname, diameter, seed));
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 1) {
                    sender.sendMessage(Main.PREFIX + "Usage: /world delete <world>");
                    return true;
                }

                World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    throw new CommandException("The world '" + args[1] + "' does not exist.");
                }

                if (world.getName().equals("lobby")) {
                    throw new CommandException("You cannot delete the lobby world.");
                }

                if (!manager.deleteWorld(world)) {
                    throw new CommandException("Could not delete world '" + world.getName() + "'.");
                }

                sender.sendMessage(Main.PREFIX + "World '§a" + world.getName() + "§7' has been deleted.");
                return true;
            }

            if (args[0].equalsIgnoreCase("tp")) {
                if (!(sender instanceof Player)) {
                    throw new CommandException("Only players can teleport to worlds.");
                }

                Player player = (Player) sender;

                if (args.length == 1) {
                    player.sendMessage(Main.PREFIX + "Usage: /world tp <world>");
                    return true;
                }

                World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    throw new CommandException("The world '" + args[1] + "' does not exist.");
                }

                player.sendMessage(Main.PREFIX + "Teleported to world '§a" + world.getName() + "§7'.");
                player.teleport(world.getSpawnLocation());
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(Main.PREFIX + "Default worlds: §8(§62§8)");
                sender.sendMessage(Main.ARROW + "lobby §8- §aNORMAL §8(§7Spawn World§8)");
                sender.sendMessage(Main.ARROW + "arena §8- §aNORMAL §8(§7Arena World§8)");
                sender.sendMessage(Main.PREFIX + "Game worlds: §8(§6" + (Bukkit.getWorlds().size() - 2) + "§8)");

                if ((Bukkit.getWorlds().size() - 2) == 0) {
                    sender.sendMessage(Main.ARROW + "There are no game worlds.");
                    return true;
                }

                for (World world : Bukkit.getWorlds()) {
                    switch (world.getName().toLowerCase()) {
                    case "lobby":
                    case "arena":
                        continue;
                    }

                    ChatColor color;

                    switch (world.getEnvironment()) {
                    case NETHER:
                        color = ChatColor.RED;
                        break;
                    case NORMAL:
                        color = ChatColor.GREEN;
                        break;
                    case THE_END:
                        color = ChatColor.AQUA;
                        break;
                    default:
                        return true;
                    }

                    sender.sendMessage(Main.ARROW + world.getName() + " §8- " + color + world.getEnvironment().name() + " §8(§7" + (game.getWorlds().contains(world) ? "§6In use" : "§7Not used")+ "§8)");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("load")) {
                if (args.length == 1) {
                    sender.sendMessage(Main.PREFIX + "Usage: /world unload <world>");
                    return true;
                }

                World world = Bukkit.getWorld(args[1]);

                if (world != null) {
                    throw new CommandException("The world '" + args[1] + "' is already loaded.");
                }

                manager.loadWorld(args[1]);
                sender.sendMessage(Main.PREFIX + "World '§a" + args[1] + "§7' has been loaded.");
                return true;
            }

            if (args[0].equalsIgnoreCase("unload")) {
                if (args.length == 1) {
                    sender.sendMessage(Main.PREFIX + "Usage: /world unload <world>");
                    return true;
                }

                World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    throw new CommandException("The world '" + args[1] + "' does not exist.");
                }

                if (world == Bukkit.getWorlds().get(0)) {
                    throw new CommandException("You cannot unload the lobby world.");
                }

                sender.sendMessage(Main.PREFIX + "World '§a" + world.getName() + "§7' has been unloaded.");
                manager.unloadWorld(world);
                return true;
            }

            if (args[0].equalsIgnoreCase("data")) {
                if (args.length > 1) {
                    World world = Bukkit.getWorld(args[1]);

                    if (world == null) {
                        throw new CommandException("The world '" + args[1] + "' does not exist.");
                    }

                    WorldData data = antiSM.getWorldData(world);
                    
                    if (data == null) {
                        throw new CommandException("No data found for world '" + world.getName() + "'.");
                    }
                    
                    data.displayStats(sender);
                    return true;
                }

                antiSM.displayStats(sender);
                return true;
            }
        }

        sender.sendMessage(Main.PREFIX + "World management help:");
        sender.sendMessage("§8» §a/world create §8- §7§oCreate a world.");
        sender.sendMessage("§8» §a/world delete §8- §7§oDelete a world.");
        sender.sendMessage("§8» §a/world load §8- §7§oLoad a world.");
        sender.sendMessage("§8» §a/world unload §8- §7§oUnload a world.");
        sender.sendMessage("§8» §a/world list §8- §7§oList all worlds.");
        sender.sendMessage("§8» §a/world tp §8- §7§oTeleport to a world.");
        sender.sendMessage("§8» §a/world data [world] §8- §7§oDisplay AntiStripmine info.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("create");
            toReturn.add("delete");
            toReturn.add("list");
            toReturn.add("tp");
            toReturn.add("load");
            toReturn.add("unload");
            toReturn.add("data");
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
            case "create":
                for (String path : settings.getHOF().getKeys(false)) {
                    toReturn.add(path.toLowerCase());
                }
                break;
            case "load":
            case "unload":
            case "tp":
            case "delete":
            case "data":
                for (World world : Bukkit.getWorlds()) {
                    toReturn.add(world.getName());
                }
                break;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create")) {
                toReturn.add("2000");
                toReturn.add("3000");
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("create")) {
                toReturn.add("" + new Random().nextLong());
            }
        }

        return toReturn;
    }
}