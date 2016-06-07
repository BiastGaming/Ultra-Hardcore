package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.GeneratorScenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

/**
 * InvertedDimensions scenario class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class InvertedDimensions extends GeneratorScenario {
    
    private static final List<Material> ORES = ImmutableList.of(
            Material.COAL_ORE, Material.IRON_ORE, Material.REDSTONE_ORE, Material.EMERALD_ORE,
            Material.DIAMOND_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.QUARTZ_ORE
    );
    
    public InvertedDimensions() {
        super(
            "InvertedDimensions", 
            "The overworld surface is how nether would look like and vice versa. Also leaves drop sugar canes and water can be placed in the nether but not the overworld, lapis and redstone drops obsidian as well as their normal drops.",
            ChatColor.RED,
            "invert",
            true,
            true,
            true,
            true
        );
    }

    @Override
    public void handleBlock(Block block) {
        switch (block.getWorld().getEnvironment()) {
        case NETHER:
            switch (block.getType()) {
            case FIRE:
                block.setType(Material.LONG_GRASS);
                block.setData((byte) 1);
                break;
            case NETHERRACK:
                Block up = block.getRelative(BlockFace.UP);
                
                if (up.isEmpty() || !up.getType().isSolid()) {
                    block.setType(Material.GRASS);
                } 
                else if (up.getType() == Material.DIRT || up.getType() == Material.STONE || up.getType() == Material.QUARTZ_ORE) {
                    block.setType(Material.STONE);
                }
                else {
                    block.setType(Material.DIRT);
                }
                break;
            case SOUL_SAND:
                block.setType(Material.SAND);
                break;
            case NETHER_BRICK:
                block.setType(Material.WOOD);
                block.setData((byte) 1);
                break;
            case NETHER_FENCE:
                block.setType(Material.SPRUCE_FENCE);
                break;
            case NETHER_BRICK_STAIRS:
                byte id = block.getData();
                block.setType(Material.SPRUCE_WOOD_STAIRS);
                block.setData(id);
                break;
            case STATIONARY_LAVA:
            case LAVA:
                block.setType(Material.BARRIER);
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
                block.setType(Material.OBSIDIAN);
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void handleChunk(Chunk chunk) {
        WorldBorder border = chunk.getWorld().getWorldBorder();

        new BukkitRunnable() {
            public void run() {
                for (int y = 256; y >= 0; y--) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            chunk.load();
                            
                            if (block.getType() == Material.OBSIDIAN) {
                                block.setType(Material.STATIONARY_LAVA);
                            }
                            
                            if (block.getType() == Material.LAPIS_ORE || block.getType() == Material.REDSTONE_ORE) {
                                if (rand.nextBoolean()) {
                                    block.setType(Material.OBSIDIAN);
                                }
                            }
                            
                            if (block.getType() == Material.BARRIER) {
                                block.setType(Material.STATIONARY_WATER);
                            }
                            
                            if (block.getType() == Material.QUARTZ_ORE) {
                                Material type = ORES.get(rand.nextInt(ORES.size()));
                                BlockUtils.getVein(block).forEach(loopBlock -> loopBlock.setType(type));
                            }
                        }
                    }
                }  
            }
        }.runTaskLater(plugin, ((int) border.getSize()) / 10);
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
}