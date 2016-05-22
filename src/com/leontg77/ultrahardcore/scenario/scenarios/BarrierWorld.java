package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BarrierWorld scenario class.
 *
 * @author D4mnX
 */
public class BarrierWorld extends Scenario implements CommandExecutor, Listener {
    public static final String PREFIX = "§fBarrierWorld §8» §7";

    protected static final List<Material> PLANT_MATERIALS = ImmutableList.of(
            Material.DOUBLE_PLANT,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.LONG_GRASS
    );

    private final Main plugin;

    /**
     * BarrierWorld scenario class constructor.
     *
     * @param plugin The main class.
     */
    public BarrierWorld(Main plugin) {
        super("BarrierWorld", "Stone, dirt and grass is replaced with barriers. Stone with 2 non-slid blocks above is unaffected, allowing you to see caves.");

        this.plugin = plugin;

        plugin.getCommand("barrierworld").setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    private BukkitRunnable task = null;
    private double totalChunks = 0;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling BarrierWorld generation.");
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "BarrierWorld is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /barrierworld <world> <diameter>");
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
        PlayerUtils.broadcast(PREFIX + "Starting BarrierWorld generation in world '§a" + world.getName() + "§7'.");

        task = new BukkitRunnable() {
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "The BarrierWorld generation has finished.");

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

                for (int y = 0; y < 128; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            Block oneAbove = block.getRelative(BlockFace.UP);
                            Block twoAbove = oneAbove.getRelative(BlockFace.UP);

                            if (block.getType() == Material.DIRT || block.getType() == Material.GRASS) {
                                block.setType(Material.BARRIER);

                                if (PLANT_MATERIALS.contains(oneAbove.getType())) {
                                    oneAbove.setType(Material.AIR);
                                }

                                if (PLANT_MATERIALS.contains(twoAbove.getType())) {
                                    twoAbove.setType(Material.AIR);
                                }

                                continue;
                            }

                            if (block.getType() != Material.STONE) {
                                continue;
                            }

                            if (!oneAbove.getType().isSolid() && !twoAbove.getType().isSolid()) {
                                continue;
                            }

                            block.setType(Material.BARRIER);
                        }
                    }
                }

                double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(online, PREFIX + "Generating BarrierWorld §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}