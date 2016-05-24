package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Underground Parallel scenario class.
 * 
 * @author LeonTG77
 */
public class UndergroundParallel extends Scenario implements CommandExecutor, Listener {
    public static final String PREFIX = "§9Underground Parallel §8» §7";

    public UndergroundParallel() {
        super("UndergroundParallel", "The surface is replicated underground beneath y42.");

        plugin.getCommand("underpara").setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    private BukkitRunnable task = null;
    private double totalChunks = 0;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling underground parallel generation.");
            task.cancel();
        }

        totalChunks = 0;
        task = null;
    }

    @EventHandler
    public void on(BlockPhysicsEvent event) {
        if (task == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        if (task == null) {
            return;
        }

        event.setCancelled(true);
    }

    private final Random rand = new Random();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "UndergroundParallel is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /underpara <world> <diameter>");
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

        locs.clear();

        for (int x = -1 * diameter; x < diameter; x += 16) {
            for (int z = -1 * diameter; z < diameter; z += 16) {
                locs.add(new Location(world, x, 1, z));
            }
        }

        totalChunks = locs.size();

        PlayerUtils.broadcast(PREFIX + "Starting underground parallel generation in world '§a" + world.getName() + "§7'.");

        task = new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "The underground parallel generation has finished.");

                    cancel();
                    new BukkitRunnable() {
                        public void run() {
                            task = null;
                        }
                    }.runTaskLater(plugin, 40);
                    return;
                }

                Location loc = locs.remove(locs.size() - 1);
                Chunk chunk = world.getChunkAt(loc);

                for (int y = 1; y <= 42; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            Block surface = world.getBlockAt(block.getX(), block.getY() + 59, block.getZ());

                            switch (surface.getType()) {
                            case LEAVES:
                            case LEAVES_2:
                                double randomPerLogs = rand.nextDouble() * 100;

                                if (randomPerLogs < 50.0D) {
                                    block.setType(Material.STONE);
                                    break;
                                }
                                randomPerLogs -= 50.0D;

                                if (randomPerLogs < 24.5D) {
                                    block.setType(Material.COAL_ORE);
                                    break;
                                }
                                randomPerLogs -= 24.5D;

                                if (randomPerLogs < 23.5D) {
                                    block.setType(Material.IRON_ORE);
                                    break;
                                }
                                randomPerLogs -= 23.5D;

                                if (randomPerLogs < 1.0D) {
                                    block.setType(Material.GOLD_ORE);
                                    break;
                                }
                                randomPerLogs -= 1.0D;

                                if (randomPerLogs < 0.5D) {
                                    block.setType(Material.LAPIS_ORE);
                                    break;
                                }

                                block.setType(Material.DIAMOND_ORE);
                                break;
                            case LOG:
                            case LOG_2:
                                double randomPerLeaves = rand.nextDouble() * 100;

                                if (randomPerLeaves < 5.0D) {
                                    block.setType(Material.REDSTONE_ORE);
                                    break;
                                }
                                randomPerLeaves -= 5.0D;

                                if (randomPerLeaves < 5.0D) {
                                    block.setType(Material.GLOWSTONE);
                                    break;
                                }

                                block.setType(Material.GRAVEL);
                                break;
                            default:
                                block.setType(surface.getType());
                                block.setData(surface.getData());

                                if (surface.getState() instanceof InventoryHolder) {
                                    InventoryHolder surfaceInv = (InventoryHolder) surface.getState();
                                    InventoryHolder inv = (InventoryHolder) block.getState();

                                    inv.getInventory().setContents(surfaceInv.getInventory().getContents());
                                }
                                break;
                            }
                        }
                    }
                }

                double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(online, PREFIX + "Generating underground parallel §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}