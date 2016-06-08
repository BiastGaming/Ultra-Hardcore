package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.GeneratorScenario;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

/**
 * InvertedParallel scenario class.
 *
 * @author D4mnX/LeonTG77
 */
@SuppressWarnings("deprecation")
public class InvertedParallel extends GeneratorScenario {
    
    public InvertedParallel() {
        super(
            "InvertedParallel", 
            "The surface is replicated underground beneath y42, but looks like the nether similiar to Inverted Dimensions. You cannot place water inn the 'fake nether'.",
            ChatColor.RED,
            "invertpara",
            true,
            true,
            false,
            false
        );
    }
    
    @Override
    public void handleBlock(Block block) {
        Location loc = block.getLocation();
        
        if (loc.getBlockY() == 0 || loc.getBlockY() > 42) {
            return;
        }
            
        World world = block.getWorld();
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
        case DOUBLE_PLANT:
        case YELLOW_FLOWER:
        case RED_ROSE:
        case LONG_GRASS:
            if (rand.nextInt(8) == 0) {
                block.setType(Material.FIRE);
            } else {
                block.setType(Material.AIR);
            }
            break;
        case SNOW:
        case SNOW_BLOCK:
        case WATER_LILY:
            block.setType(Material.AIR);
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
        case SPRUCE_FENCE:
        case BIRCH_FENCE:
        case JUNGLE_FENCE:
        case ACACIA_FENCE:
        case DARK_OAK_FENCE:
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
        case ICE:
        case OBSIDIAN:
            block.setType(Material.LAVA);
            
            new BukkitRunnable() {
                public void run() {
                    block.setType(Material.LAVA);
                }
            }.runTaskLater(plugin, 600);
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
}   