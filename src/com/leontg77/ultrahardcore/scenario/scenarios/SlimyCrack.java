package com.leontg77.ultrahardcore.scenario.scenarios;

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
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * SlimyCrack scenario class
 * 
 * @author dans1988
 */
public class SlimyCrack extends Scenario implements Listener, CommandExecutor {
    public static final String PREFIX = "§aSlimyCrack §8» §f";
    
    private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;
    private static final int STAIRCASE_START = 16;

    public SlimyCrack() {
        super("SlimyCrack", "There is a giant fissure generated through natural terrain which exposes ores, caves mineshafts and the like but at the bottom there are slime blocks except at the sides where there are gaps that players are still able to fall down. The crack goes through 0,0 and is parallel to the x axis.");

        plugin.getCommand("slimecrack").setExecutor(this);
    }

    private boolean generation = false;

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (!generation) {
            return;
        }
        
        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can generate cracks.");
            return true;
        }

        Player player = (Player) sender;

        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "SlimyCrack is not enabled.");
            return true;
        }

        if (!player.hasPermission("uhc.slimycrack")) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /slimecrack <width> <length> <speed> <z>");
            return true;
        }

        int width;
        
        try {
            width = Integer.parseInt(args[0]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an valid width.");
            return true;
        }

        int length;
        
        try {
            length = Integer.parseInt(args[1]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + args[1] + " is not an valid length.");
            return true;
        }

        int speed;
        
        try {
            speed = Integer.parseInt(args[2]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + args[2] + " is not an valid speed.");
            return true;
        }

        boolean z;
        
        if (args[3].equalsIgnoreCase("true")) {
            z = true;
        }
        else if (args[3].equalsIgnoreCase("false")) {
            z = false;
        }
        else {
            player.sendMessage(ChatColor.RED + "Z can only be true or false, not " + args[3] + ".");
            return true;
        }
        
        generate(player.getWorld(), length, width, speed, z);
        return true;
    }

    /**
     * Generate the bigcrack.
     *
     * @param world The world to use.
     * @param length The length to use.
     * @param width The width to use.
     * @param speed The speed to use.
     * @param useZ Wether to use the Z or X.
     */
    public void generate(final World world, final int length, final int width, int speed, final boolean useZ) {
        generation = true;
        
        int xChunk;
        if (useZ) {
            if (length % BLOCKS_PER_CHUNK == 0) {
                xChunk = length / BLOCKS_PER_CHUNK;
            } else {
                xChunk = (length / BLOCKS_PER_CHUNK) + 1;
            }
        } else {
            if (width % BLOCKS_PER_CHUNK == 0) {
                xChunk = (width + STAIRCASE_START) / BLOCKS_PER_CHUNK;
            } else {
                xChunk = ((width + STAIRCASE_START) / BLOCKS_PER_CHUNK) + 1;
            }
        }

        int xMaxChunk = xChunk;
        xChunk = xChunk * -1;
        
        int zChunk;
        if (useZ) {
            if (width % BLOCKS_PER_CHUNK == 0) {
                zChunk = (width + STAIRCASE_START) / BLOCKS_PER_CHUNK;
            } else {
                zChunk = ((width + STAIRCASE_START) / BLOCKS_PER_CHUNK) + 1;
            }
        } else {
            if (length % BLOCKS_PER_CHUNK == 0) {
                zChunk = length / BLOCKS_PER_CHUNK;
            } else {
                zChunk = (length / BLOCKS_PER_CHUNK) + 1;
            }
        }
        
        int zMaxChunk = zChunk;
        zChunk = zChunk * -1;
        
        int delayMultiplier = 0;
        
        for (int x = xChunk; x <= xMaxChunk; x++) {
            for (int z = zChunk; z <= zMaxChunk; z++) {
                final Chunk chunk = world.getChunkAt(x, z);
                
                new BukkitRunnable() {
                    public void run() {
                        populate(world, chunk, width, length, useZ);

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            PacketUtils.sendAction(online, PREFIX + "Populated chunk at x = §a" + chunk.getX() + "§7, z = §a" + chunk.getZ() + "§7.");
                        }
                    }
                }.runTaskLater(plugin, delayMultiplier * speed);
                
                delayMultiplier++;
            }
        }
        
        new BukkitRunnable() {
            public void run() {
                generation = false;
                PlayerUtils.broadcast(PREFIX + "SlimyCrack generation finished!");
            }
        }.runTaskLater(plugin, delayMultiplier * speed);
    }

    /**
     * Populate a chunk.
     *
     * @param world The world of the crack.
     * @param chunk The chunk to populate.
     * @param length The length of the crack.
     * @param width The width of the crack.
     * @param useZ Wether to use the Z or X.
     */
    public void populate(World world, Chunk chunk, int width, int length, boolean useZ) {
        chunk.load();
        
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y >= 0; y--) {
                    Block block = chunk.getBlock(x, y, z);
                    Location location = block.getLocation();
                    
                    int xLocation = location.getBlockX();
                    int yLocation = location.getBlockY();
                    int zLocation = location.getBlockZ();
                    
                    int stairWidth = width + STAIRCASE_START - y;
                    
                    if (useZ) {
                        if (zLocation >= (width * -1) && zLocation <= width && xLocation <= length && xLocation >= (length * -1) && yLocation > STAIRCASE_START) {
                            block.setType(Material.AIR);
                        } else if (y <= STAIRCASE_START && y != 1) {
                            if (zLocation >= (stairWidth * -1) && zLocation <= stairWidth && xLocation <= length && xLocation >= (length * -1)) {
                                block.setType(Material.AIR);
                            }
                        } else if (y == 1) {
                            if (zLocation >= (width * -1) && zLocation <= width && xLocation <= length && xLocation >= (length * -1)) {
                                block.setType(Material.SLIME_BLOCK);
                            } else if (zLocation >= (stairWidth * -1) && zLocation <= stairWidth && xLocation <= length && xLocation >= (length * -1)) {
                                block.setType(Material.AIR);
                            }
                        }
                    } else {
                        if (xLocation >= (width * -1) && xLocation <= width && zLocation <= length && zLocation >= (length * -1) && yLocation > STAIRCASE_START) {
                            block.setType(Material.AIR);
                        } else if (y <= STAIRCASE_START && y != 1) {
                            if (xLocation >= (stairWidth * -1) && xLocation <= stairWidth && zLocation <= length && zLocation >= (length * -1)) {
                                block.setType(Material.AIR);
                            }
                        } else if (y == 1) {
                            if (xLocation >= (width * -1) && xLocation <= width && zLocation <= length && zLocation >= (length * -1)) {
                                block.setType(Material.SLIME_BLOCK);
                            } else if (xLocation >= (stairWidth * -1) && xLocation <= stairWidth && zLocation <= length && zLocation >= (length * -1)) {
                                if (block.getType() != Material.SLIME_BLOCK) {
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}