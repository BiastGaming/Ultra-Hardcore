package com.leontg77.ultrahardcore.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Generator super scenario class.
 * 
 * @author LeonTG77
 */
public abstract class GeneratorScenario extends Scenario implements CommandExecutor, TabCompleter, Listener {
    private static final Set<Material> GENERATION_DROPS = ImmutableSet.of(Material.SEEDS, Material.DOUBLE_PLANT, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.SUGAR_CANE, Material.LONG_GRASS, Material.WATER_LILY);

    protected final String PREFIX;
    private final String commandName;

    private final boolean disableFlowing;
    private final boolean disablePhysics;

    private final boolean disableFire;
    private final boolean killDrops;

    public GeneratorScenario(String name, String description, ChatColor prefixColor, String commandName, boolean disablePhysics, boolean disableFlowing, boolean disableFire, boolean killDrops) {
        super(name, description);
        
        this.commandName = commandName;
        
        this.disableFlowing = disableFlowing;
        this.disablePhysics = disablePhysics;
        
        this.disableFire = disableFire;
        this.killDrops = killDrops;
        
        if (prefixColor == ChatColor.GRAY) {
            this.PREFIX = prefixColor + name + " §8» §f";
        } else {
            this.PREFIX = prefixColor + name + " §8» §7";
        }
        
        plugin.getCommand(commandName).setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    protected BukkitRunnable task = null;
    private double totalChunks = 0;
    
    protected final Random rand = new Random();

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling " + getName().toLowerCase() + " generation.");
            task.cancel();
        }

        totalChunks = 0;
        task = null;
    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        if (!disableFlowing) {
            return;
        }
        
        if (task == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockPhysicsEvent event) {
        if (!disablePhysics) {
            return;
        }
        
        if (task == null) {
            return;
        }

        event.setCancelled(true);
    }
    
    @EventHandler
    public void on(ItemSpawnEvent event) {
        if (!killDrops) {
            return;
        }
        
        if (task == null) {
            return;
        }
        
        Item item = event.getEntity();
        ItemStack stack = item.getItemStack();
        
        if (GENERATION_DROPS.contains(stack.getType())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * Handle the given block that just got looped to with the genetator.
     * 
     * @param block The block to handle.
     */
    public abstract void handleBlock(Block block);
    
    /**
     * Handle the given chunk that just got looped to with the genetator.
     * 
     * @param chunk The chunk to handle.
     */
    public void handleChunk(Chunk chunk) {}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + getName() + " is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (task != null) {
            sender.sendMessage(ChatColor.RED + "There is already a " + getName().toLowerCase() + " generation going.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /" + commandName + " <world> <diameter>");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "The world '" + args[0] + "' does not exist.");
            return true;
        }

        if (world.getName().equals("lobby")) {
            sender.sendMessage(ChatColor.RED + "You can't generate " + getName().toLowerCase() + " in the spawn world.");
            return true;
        }

        int radius;

        try {
            radius = parseInt(args[1], "diameter") / 2;
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            return true;
        }

        if (disableFire) {
            world.setGameRuleValue("doFireTick", "false");
        }
        
        Location center = world.getWorldBorder().getCenter();
        locs.clear();

        for (int x = -radius; x < radius; x += 16) {
            for (int z = -radius; z < radius; z += 16) {
                Location at = new Location(world, x, 1, z);
                at.add(center.getBlockX(), 0, center.getBlockZ());
                
                locs.add(at);
            }
        }

        totalChunks = locs.size();

        PlayerUtils.broadcast(PREFIX + "Starting " + getName().toLowerCase() + " generation in world '§a" + world.getName() + "§7'.");
        PlayerUtils.broadcast(PREFIX + "Task will take §a" + DateUtils.secondsToString((long) (totalChunks / plugin.getTps())) + "§7, may be longer with lower tps.");

        task = new BukkitRunnable() {
            public void run() {
                if (locs.isEmpty()) {
                    PlayerUtils.broadcast(PREFIX + "The " + getName().toLowerCase() + " generation has finished.");

                    cancel();
                    
                    new BukkitRunnable() {
                        public void run() {
                            task = null;
                        }
                    }.runTaskLater(plugin, 100);
                    
                    if (killDrops) {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof Item) {
                                Item item = (Item) entity;
                                
                                if (GENERATION_DROPS.contains(item.getItemStack().getType())) {
                                    item.remove();
                                }
                            }
                        }
                    }
                    return;
                }

                Location loc = locs.remove(locs.size() - 1);
                
                Chunk chunk = world.getChunkAt(loc);
                handleChunk(chunk);
                
                for (int y = 256; y >= 0; y--) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            handleBlock(block);
                        }
                    }
                }

                double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);
                String message = PREFIX + "Generating " + getName().toLowerCase() + " §8(§a" + NumberUtils.formatPercentDouble(completed) + "% §8/ §6" + DateUtils.secondsToString((long) ((long) (locs.size() / plugin.getTps()))) + " §7left§8)";
                    
                Bukkit.getOnlinePlayers().forEach(online -> PacketUtils.sendAction(online, message));
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> toReturn = new ArrayList<String>();
        
        if (!isEnabled()) {
            return toReturn;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            return toReturn;
        }
        
        List<String> list = new ArrayList<String>();
        
        if (args.length == 1) {
            Bukkit.getWorlds().forEach(world -> list.add(world.getName()));
        }
        
        if (args.length == 2) {
            World world = Bukkit.getWorld(args[0]);
            
            if (world != null) {
                list.add((int) (world.getWorldBorder().getSize() + 10) + "");
            }
        }

        // make sure to only tab complete what starts with what they 
        // typed or everything if they didn't type anything
        for (String str : list) {
            if (args[args.length - 1].isEmpty() || str.startsWith(args[args.length - 1].toLowerCase())) {
                toReturn.add(str);
            }
        }
        
        return toReturn;
    }
}