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
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DetailedWorld scenario class.
 * 
 * @author LeonTG77
 */
public class DetailedWorld extends Scenario implements CommandExecutor, Listener {
    public static final String PREFIX = "§aDetailed World §8» §7";

    public DetailedWorld() {
        super("DetailedWorld", "The world has a lot of terrain changes from grass being mixed in with green clay to leaves having more details to them.");

        plugin.getCommand("detail").setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    private BukkitRunnable task = null;
    private double totalChunks = 0;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling detailed world generation.");
            task.cancel();
        }

        totalChunks = 0;
        task = null;
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
            sender.sendMessage(PREFIX + "Detailed World is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /detail <world> <diameter>");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

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

        PlayerUtils.broadcast(PREFIX + "Starting detailed world generation in world '§a" + world.getName() + "§7'.");

        task = new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "The detailed world generation has finished.");

                    cancel();
                    
                    new BukkitRunnable() {
                        public void run() {
                            task = null;
                        }
                    }.runTaskLater(plugin, 100);
                    return;
                }

                Location loc = locs.remove(locs.size() - 1);
                Chunk chunk = world.getChunkAt(loc);

                for (int y = 0; y < 128; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            Block block = chunk.getBlock(x, y, z);

                            if (block.getType() == Material.DIRT && BlockUtils.getDurability(block) == 0 && rand.nextInt(8) == 0) {
                                block.setData((byte) 1);
                            }

                            if (block.getType() == Material.LEAVES && rand.nextInt(8) == 0) {
                                block.setData((byte) 1);
                            }

                            if (block.getType() == Material.LEAVES_2 && rand.nextInt(8) == 0) {
                                block.setType(Material.LEAVES);
                                block.setData((byte) 1);
                            }
                            
                            if (block.getType() == Material.SAND && BlockUtils.getDurability(block) == 0 && rand.nextInt(8) == 0) {
                                block.setType(rand.nextInt(8) == 0 ? Material.SANDSTONE_STAIRS : Material.SANDSTONE);
                            }
                            
                            if (block.getType() == Material.ICE && rand.nextInt(8) == 0) {
                                block.setType(Material.PACKED_ICE);
                            }
                            
                            if (block.getType() == Material.STONE && rand.nextInt(8) == 0) {
                                block.setType(Material.COBBLESTONE);
                            }

                            if (block.getType() == Material.STONE && rand.nextInt(8) == 0) {
                                block.setType(Material.STAINED_CLAY);
                                block.setData((byte) 9);
                            }
                            
                            if (block.getType() == Material.ICE && rand.nextInt(8) == 0) {
                                block.setType(Material.PACKED_ICE);
                            }

                            if (block.getType() == Material.SNOW) {
                                block.setData((byte) rand.nextInt(4));
                            }

                            if (block.getType() == Material.GRASS && rand.nextInt(50) < 14) {
                                block.setType(Material.STAINED_CLAY);
                                block.setData((byte) (rand.nextBoolean() ? 5 : 13));
                            }

                            if (block.getType() == Material.STONE && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                                block.setType(Material.STAINED_GLASS);
                                block.setData((byte) 8);
                                
                                block.getRelative(BlockFace.DOWN).setType(Material.GLOWSTONE);
                            }

                            if (block.getType() == Material.DIRT && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                                block.setType(Material.STAINED_GLASS);
                                block.setData((byte) 12);
                                
                                block.getRelative(BlockFace.DOWN).setType(Material.GLOWSTONE);
                            }

                            if (block.getType() == Material.GRASS && rand.nextInt(100) == 0 && block.getRelative(BlockFace.UP).isEmpty()) {
                                block.setType(Material.STAINED_GLASS);
                                block.setData((byte) 13);
                                
                                block.getRelative(BlockFace.DOWN).setType(Material.GLOWSTONE);
                            }

                            if (LocationUtils.hasBlockNearby(Material.LAVA, block.getLocation()) && rand.nextInt(4) == 0 && !block.isLiquid()) {
                                block.setType(Material.SOUL_SAND);
                            }

                            if (LocationUtils.hasBlockNearby(Material.LOG, block.getLocation()) && rand.nextInt(8) == 0 && block.isEmpty()) {
                                block.setType(Material.SPRUCE_FENCE);
                            }

                            if (LocationUtils.hasBlockNearby(Material.LOG_2, block.getLocation()) && rand.nextInt(8) == 0 && block.isEmpty()) {
                                block.setType(Material.SPRUCE_FENCE);
                            }
                            
                            switch (block.getBiome()) {
                            case BIRCH_FOREST:
                            case BIRCH_FOREST_HILLS:
                            case BIRCH_FOREST_HILLS_MOUNTAINS:
                            case BIRCH_FOREST_MOUNTAINS:
                                if (block.getType() == Material.SPRUCE_FENCE) {
                                    block.setType(Material.BIRCH_FENCE);
                                }
                                break;
                            case EXTREME_HILLS:
                            case EXTREME_HILLS_MOUNTAINS:
                            case EXTREME_HILLS_PLUS:
                            case EXTREME_HILLS_PLUS_MOUNTAINS:
                            case MUSHROOM_ISLAND:
                            case MUSHROOM_SHORE:
                                if (block.getType() == Material.GRASS && rand.nextInt(8) == 0) {
                                    block.setType(Material.DIRT);
                                    block.setData((byte) 1);
                                }
                                break;
                            case JUNGLE:
                            case JUNGLE_EDGE:
                            case JUNGLE_EDGE_MOUNTAINS:
                            case JUNGLE_HILLS:
                            case JUNGLE_MOUNTAINS:
                            case SWAMPLAND:
                            case SWAMPLAND_MOUNTAINS:
                                if (block.getType() == Material.GRASS && rand.nextInt(8) == 0) {
                                    block.setType(Material.DIRT);
                                    block.setData((byte) 2);
                                }
                                
                                if (block.getType() == Material.STAINED_CLAY) {
                                    block.setType(Material.GRASS);
                                }
                                break;
                            case SAVANNA:
                            case SAVANNA_MOUNTAINS:
                            case SAVANNA_PLATEAU:
                            case SAVANNA_PLATEAU_MOUNTAINS:
                                if (block.getType() == Material.SPRUCE_FENCE) {
                                    block.setType(Material.ACACIA_FENCE);
                                }
                                
                                if (!block.isEmpty() && !block.isLiquid() && rand.nextInt(1000) == 0) {
                                    Block up = block.getRelative(BlockFace.UP);
                                    
                                    if (up.isEmpty()) {
                                        up.setType(Material.FIRE);
                                        
                                        block.setType(Material.NETHERRACK);

                                        up.getRelative(BlockFace.NORTH).setType(Material.STEP);
                                        up.getRelative(BlockFace.NORTH).setData((byte) 3);

                                        up.getRelative(BlockFace.EAST).setType(Material.STEP);
                                        up.getRelative(BlockFace.EAST).setData((byte) 3);

                                        up.getRelative(BlockFace.WEST).setType(Material.STEP);
                                        up.getRelative(BlockFace.WEST).setData((byte) 3);

                                        up.getRelative(BlockFace.SOUTH).setType(Material.STEP);
                                        up.getRelative(BlockFace.SOUTH).setData((byte) 3);
                                    }
                                }
                                break;
                            case SUNFLOWER_PLAINS:
                            case PLAINS:
                                if (!block.isEmpty() && !block.isLiquid() && rand.nextInt(100) == 0) {
                                    block.getRelative(BlockFace.UP).setType(Material.FENCE);
                                    
                                    if (rand.nextBoolean()) {
                                        block.getRelative(BlockFace.UP, 2).setType(Material.WOOD_STEP);
                                    }
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
                    PacketUtils.sendAction(online, PREFIX + "Generating detailed world §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}