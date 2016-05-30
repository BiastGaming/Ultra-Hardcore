package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * City world scenario class.
 * 
 * @author D4mnX
 */
public class CityWorld extends Scenario implements CommandExecutor {
    public static final String PREFIX = "§7City World §8» §f";

    public CityWorld() {
        super("CityWorld", "The map is generated using the CityWorld plugin.");
    }

    private BukkitRunnable task = null;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling bugged diamond block fix task.");
            task.cancel();
        }

        task = null;
    }

    @Override
    protected void onSetup(Main plugin, Game game, Timer timer) {
        super.onSetup(plugin, game, timer);
        
        plugin.getCommand("cityworld").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "City World is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /cityworld <world> <diameter>");
            return true;
        }

        final World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "The world '" + args[0] + "' does not exist.");
            return true;
        }

        int diameter;

        try {
            diameter = parseInt(args[1], "diameter") / 2;
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            return true;
        }

        List<Location> locs = Lists.newArrayList();

        for (int x = -1 * diameter; x < diameter; x += 16) {
            for (int z = -1 * diameter; z < diameter; z += 16) {
                locs.add(new Location(world, x, 1, z));
            }
        }

        int totalChunks = locs.size();

        PlayerUtils.broadcast(PREFIX + "Removing bugged diamond blocks in '§a" + world.getName() + "§f'.");

        task = new BukkitRunnable() {
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "All buggy diamond blocks have been removed.");

                    cancel();
                    return;
                }

                Location loc = locs.remove(locs.size() - 1);
                Chunk chunk = world.getChunkAt(loc);

                for (int y = 0; y < 256; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            Block block = chunk.getBlock(x, y, z);

                            if (block.getType() == Material.DIAMOND_BLOCK) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }

                double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(online, PREFIX + "Removing bugged diamond blocks §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}