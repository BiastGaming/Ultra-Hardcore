package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Snowday scenario class.
 * 
 * @author LeonTG77
 */
public class Snowday extends Scenario implements Listener, CommandExecutor {
    public static final String PREFIX = "§fSnowday §8» §7";

    public Snowday() {
        super("Snowday", "Grass and dirt is replaced by snow blocks, The entire world is a cold taiga biome which snows in all the time.");
        
        plugin.getCommand("snowday").setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    private BukkitRunnable task = null;
    private double totalChunks = 0;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling snowday generation.");
            task.cancel();
        }

        totalChunks = 0;
        task = null;
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        for (World world : game.getWorlds()) {
            if (world.getEnvironment() != Environment.NORMAL) {
                continue;
            }

            world.setThundering(false);
            world.setStorm(true);
        }
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
            sender.sendMessage(PREFIX + "Snowday is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /snowday <world> <diameter>");
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

        PlayerUtils.broadcast(PREFIX + "Starting snowday generation in world '§a" + world.getName() + "§7'.");

        task = new BukkitRunnable() {
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "The snowday generation has finished.");

                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }

                    cancel();
                    task = null;
                    return;
                }

                Location loc = locs.remove(locs.size() - 1);
                Chunk chunk = world.getChunkAt(loc);

                for (int y = 128; y >= 55 ; y--) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            Block block = chunk.getBlock(x, y, z);

                            block.setBiome(Biome.COLD_TAIGA);

                            switch (block.getType()) {
                            case LONG_GRASS:
                            case DOUBLE_PLANT:
                            case YELLOW_FLOWER:
                            case RED_ROSE:
                                block.setType(Material.AIR);
                                break;
                            case GRASS:
                            case DIRT:
                                block.setType(Material.SNOW_BLOCK);
                                break;
                            case WATER:
                            case STATIONARY_WATER:
                                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                                    block.setType(Material.ICE);
                                }
                                break;
                            case LEAVES:
                            case LEAVES_2:
                                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                                    block.getRelative(BlockFace.UP).setType(Material.SNOW);
                                }
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }

                double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(online, PREFIX + "Generating snowday §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}