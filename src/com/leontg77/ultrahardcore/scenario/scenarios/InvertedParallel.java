package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

/**
 * InvertedParallel scenario class.
 *
 * @author D4mnX/LeonTG77
 */
public class InvertedParallel extends Scenario implements CommandExecutor, Listener {
    public static final String PREFIX = "§cInverted Parallel §8» §7";

    private final Main plugin;

    /**
     * InvertedParallel class constructor.
     *
     * @param plugin The main class.
     */
    public InvertedParallel(Main plugin) {
        super("InvertedParallel", "The surface is replicated underground beneath y42, but looks like the nether similiar to Inverted Dimensions. You cannot place water inn the 'fake nether'.");

        this.plugin = plugin;

        plugin.getCommand("invertpara").setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    private BukkitRunnable task = null;
    private double totalChunks = 0;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling inverted parallel generation.");
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

    @EventHandler
    public void on(BlockIgniteEvent event) {
        IgniteCause cause = event.getCause();

        if (cause == IgniteCause.LAVA) {
            event.setCancelled(true);
            return;
        }

        if (cause == IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerBucketEmptyEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        if (event.getBucket() == Material.LAVA_BUCKET) {
            return;
        }

        Block blockClicked = event.getBlockClicked();
        Block block = blockClicked.getRelative(event.getBlockFace());

        if (block.getY() > 40) {
            return;
        }

        event.setCancelled(true);
        Location loc = block.getLocation();
        Player player = event.getPlayer();
        World world = loc.getWorld();

        playEffect(loc);
        world.playSound(loc, Sound.FIZZ, 1, 2);

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setItemInHand(new ItemStack(Material.BUCKET));
        }
    }

    /**
     * Player the fire effect at the given location.
     *
     * @param loc The location.
     */
    private void playEffect(Location loc) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SMOKE_LARGE, true, loc.getBlockX() + 0.5f, loc.getBlockY() + 0.5f, loc.getBlockZ() + 0.5f, 0.0000000003f, 0.0000000003f, 0.0000000003f, 0.015f, 25, null);

        for (Player player : loc.getWorld().getPlayers()) {
            CraftPlayer craft = (CraftPlayer) player; // safe cast.

            craft.getHandle().playerConnection.sendPacket(packet);
        }
    }

    private final Random rand = new Random();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Command structure taken from other scenarios with world modification commands

        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Inverted parallel is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /invertedparallel <world> <diameter>");
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

        PlayerUtils.broadcast(PREFIX + "Starting inverted parallel generation in world '§a" + world.getName() + "§7'.");

        task = new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "The inverted parallel generation has finished.");

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
                            final Block blockInParallel = chunk.getBlock(x, y, z);
                            final Block blockOnSurface = chunk.getBlock(x, y + 59, z);

                            switch (blockOnSurface.getType()) {
                                case LEAVES:
                                case LEAVES_2:
                                    double randomPerLogs = rand.nextDouble() * 100;

                                    if (randomPerLogs < 50.0D) {
                                        blockInParallel.setType(Material.STONE);
                                        break;
                                    }
                                    randomPerLogs -= 50.0D;

                                    if (randomPerLogs < 24.5D) {
                                        blockInParallel.setType(Material.COAL_ORE);
                                        break;
                                    }
                                    randomPerLogs -= 24.5D;

                                    if (randomPerLogs < 23.5D) {
                                        blockInParallel.setType(Material.IRON_ORE);
                                        break;
                                    }
                                    randomPerLogs -= 23.5D;

                                    if (randomPerLogs < 1.0D) {
                                        blockInParallel.setType(Material.GOLD_ORE);
                                        break;
                                    }
                                    randomPerLogs -= 1.0D;

                                    if (randomPerLogs < 0.5D) {
                                        blockInParallel.setType(Material.LAPIS_ORE);
                                        break;
                                    }

                                    blockInParallel.setType(Material.DIAMOND_ORE);
                                    break;
                                case LOG:
                                case LOG_2:
                                    double randomPerLeaves = rand.nextDouble() * 100;

                                    if (randomPerLeaves < 5.0D) {
                                        blockInParallel.setType(Material.REDSTONE_ORE);
                                        break;
                                    }
                                    randomPerLeaves -= 5.0D;

                                    if (randomPerLeaves < 5.0D) {
                                        blockInParallel.setType(Material.GLOWSTONE);
                                        break;
                                    }

                                    blockInParallel.setType(Material.GRAVEL);
                                    break;
                                case DOUBLE_PLANT:
                                case YELLOW_FLOWER:
                                case RED_ROSE:
                                case LONG_GRASS:
                                    if (rand.nextInt(8) == 0) {
                                        blockInParallel.setType(Material.FIRE);
                                    } else {
                                        blockInParallel.setType(Material.AIR);
                                    }
                                    break;
                                case SNOW:
                                case SNOW_BLOCK:
                                case WATER_LILY:
                                    blockInParallel.setType(Material.AIR);
                                    break;
                                case GRASS:
                                case DIRT:
                                    blockInParallel.setType(Material.NETHERRACK);
                                    break;
                                case SAND:
                                case SANDSTONE:
                                    blockInParallel.setType(Material.SOUL_SAND);
                                    break;
                                case WOOD:
                                    blockInParallel.setType(Material.NETHER_BRICK);
                                    break;
                                case FENCE:
                                    blockInParallel.setType(Material.NETHER_FENCE);
                                    break;
                                case WOOD_STAIRS:
                                case SPRUCE_WOOD_STAIRS:
                                case BIRCH_WOOD_STAIRS:
                                case JUNGLE_WOOD_STAIRS:
                                case ACACIA_STAIRS:
                                case DARK_OAK_STAIRS:
                                    byte id = blockInParallel.getData();
                                    blockInParallel.setType(Material.NETHER_BRICK_STAIRS);
                                    blockInParallel.setData(id);
                                    break;
                                case STATIONARY_WATER:
                                case WATER:
                                case ICE:
                                case OBSIDIAN:
                                    blockInParallel.setType(Material.LAVA);
                                    
                                    new BukkitRunnable() {
                                        public void run() {
                                            blockInParallel.setType(Material.LAVA);
                                        }
                                    }.runTaskLater(plugin, 600);
                                    break;
                                default:
                                    blockInParallel.setType(blockOnSurface.getType());
                                    blockInParallel.setData(blockOnSurface.getData());

                                    if (blockOnSurface.getState() instanceof InventoryHolder) {
                                        InventoryHolder surfaceInv = (InventoryHolder) blockOnSurface.getState();
                                        InventoryHolder inv = (InventoryHolder) blockInParallel.getState();

                                        inv.getInventory().setContents(surfaceInv.getInventory().getContents());
                                    }
                                    break;
                            }
                        }
                    }
                }

                double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(online, PREFIX + "Generating inverted parallel §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}