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
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

/**
 * InvertedDimensions scenario class.
 * 
 * @author LeonTG77
 */
public class InvertedDimensions extends Scenario implements CommandExecutor, Listener {
    public static final String PREFIX = "§cInverted Dimensions §8» §7";

    /**
     * InvertedDimentions class constructor.
     *
     * @param plugin The main class.
     */
    public InvertedDimensions() {
        super("InvertedDimensions", "The overworld surface is how nether would look like and vice versa. Also leaves drop sugar canes and water can be placed in the nether but not the overworld, 30% of the lapis you mine will drop obsidian.");

        plugin.getCommand("invert").setExecutor(this);
    }

    private final List<Location> locs = new ArrayList<Location>();

    private BukkitRunnable task = null;
    private double totalChunks = 0;

    @Override
    public void onDisable() {
        if (task != null) {
            PlayerUtils.broadcast(PREFIX + "Cancelling inverted dimensions generation.");
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
    public void onBlockIgnite(BlockIgniteEvent event) {
        IgniteCause cause = event.getCause();

        if (cause == IgniteCause.LAVA) {
            event.setCancelled(true);
            return;
        }

        if (cause == IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    /**
     * @author D4mnX
     */
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

        Location loc = block.getLocation();

        Player player = event.getPlayer();
        World world = loc.getWorld();

        switch (world.getEnvironment()) {
        case NORMAL:
            event.setCancelled(true);
            playEffect(loc);
            world.playSound(loc, Sound.FIZZ, 1, 2);

            if (player.getGameMode() != GameMode.CREATIVE) {
                player.setItemInHand(new ItemStack(Material.BUCKET));
            }
            break;
        case NETHER:
            event.setCancelled(true);
            block.setType(Material.WATER);

            if (player.getGameMode() != GameMode.CREATIVE) {
                player.setItemInHand(new ItemStack(Material.BUCKET));
            }
            break;
        case THE_END:
            break;
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

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();

        if (block.getType() == Material.LAPIS_ORE || block.getType() == Material.REDSTONE_ORE || block.getType() == Material.GLOWING_REDSTONE_ORE) {
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.OBSIDIAN));
        }
    }

    @EventHandler
    public void on(LeavesDecayEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();
        Location loc = block.getLocation().add(0.5, 0.7, 0.5);

        if (rand.nextInt(100) < 2) {
            BlockUtils.dropItem(loc, new ItemStack(Material.SUGAR_CANE, 1 + rand.nextInt(1)));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Inverted Dimensions is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + "Usage: /invert <world> <diameter>");
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

        PlayerUtils.broadcast(PREFIX + "Starting inverted dimensions generation in world '§a" + world.getName() + "§7'.");

        task = new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                if (locs.size() == 0) {
                    PlayerUtils.broadcast(PREFIX + "The inverted dimensions generation has finished.");

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

                for (int y = 128; y >= 0; y--) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 17; z++) {
                            final Block block = chunk.getBlock(x, y, z);

                            switch (block.getWorld().getEnvironment()) {
                            case NETHER:
                                switch (block.getType()) {
                                case FIRE:
                                    block.setType(Material.LONG_GRASS);
                                    block.setData((byte) 1);
                                    break;
                                case NETHERRACK:
                                    if (block.getRelative(BlockFace.UP).isEmpty() || !block.getRelative(BlockFace.UP).getType().isSolid()) {
                                        block.setType(Material.GRASS);
                                    } else {
                                        block.setType(Material.DIRT);
                                    }
                                    break;
                                case SOUL_SAND:
                                    block.setType(Material.SAND);
                                    break;
                                case NETHER_BRICK:
                                    block.setType(Material.WOOD);
                                    break;
                                case NETHER_FENCE:
                                    block.setType(Material.FENCE);
                                    break;
                                case NETHER_BRICK_STAIRS:
                                    byte id = block.getData();
                                    block.setType(Material.WOOD_STAIRS);
                                    block.setData(id);
                                    break;
                                case STATIONARY_LAVA:
                                case LAVA:
                                case BARRIER:
                                    block.setType(Material.BARRIER);

                                    new BukkitRunnable() {
                                        public void run() {
                                            block.setType(Material.WATER);
                                        }
                                    }.runTaskLater(plugin, 300);
                                    break;
                                default:
                                    break;
                                }
                                break;
                            case NORMAL:
                                switch (block.getType()) {
                                case LONG_GRASS:
                                    if (rand.nextInt(8) == 0) {
                                        block.setType(Material.FIRE);
                                    }
                                    break;
                                case GRASS:
                                case DIRT:
                                    block.setType(Material.NETHERRACK);
                                    break;
                                case SAND:
                                case SANDSTONE:
                                    block.setType(Material.SOUL_SAND);
                                    break;
                                case WOOD:
                                    block.setType(Material.NETHER_BRICK);
                                    break;
                                case FENCE:
                                    block.setType(Material.NETHER_FENCE);
                                    break;
                                case WOOD_STAIRS:
                                case SPRUCE_WOOD_STAIRS:
                                case BIRCH_WOOD_STAIRS:
                                case JUNGLE_WOOD_STAIRS:
                                case ACACIA_STAIRS:
                                case DARK_OAK_STAIRS:
                                    byte id = block.getData();
                                    block.setType(Material.NETHER_BRICK_STAIRS);
                                    block.setData(id);
                                    break;
                                case STATIONARY_WATER:
                                case WATER:
                                case ICE:
                                case OBSIDIAN:
                                    block.setType(Material.OBSIDIAN);

                                    new BukkitRunnable() {
                                        public void run() {
                                            block.setType(Material.LAVA);
                                        }
                                    }.runTaskLater(plugin, 300);
                                    break;
                                default:
                                    break;
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
                    PacketUtils.sendAction(online, PREFIX + "Generating inverted dimensions §8(§a" + NumberUtils.formatPercentDouble(completed) + "%§8)");
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
        return true;
    }
}