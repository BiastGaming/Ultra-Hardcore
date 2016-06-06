 package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.GeneratorScenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Pyrophobia scenario class.
 * 
 * @author LeonTG77
 */
public class Pyrophobia extends GeneratorScenario {
    private static final PotionEffect FIRE_RES = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, NumberUtils.TICKS_IN_999_DAYS, 2);

    public Pyrophobia() {
        super(
            "Pyrophobia", 
            "All water and ice is replaced with lava, 50% of redstone and lapis is replaced by obsidian, leaves has a 2% chance to drop sugar cane and mobs are fire resistant.", 
            ChatColor.DARK_RED, 
            "genpyro", 
            false, 
            false,
            true,
            true
        );
    }

    @Override
    public void handleBlock(Block block) {
        if (block.getType() == Material.STATIONARY_WATER) {
            block.setType(Material.OBSIDIAN);
        }
        
        if (block.getType() == Material.ICE) {
            block.setType(Material.OBSIDIAN);
        }
        
        if (block.getType() == Material.PACKED_ICE) {
            block.setType(Material.OBSIDIAN);
        }
        
        if (block.getType() == Material.WATER) {
            block.setType(Material.OBSIDIAN);
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
                        }
                    }
                }  
            }
        }.runTaskLater(plugin, ((int) border.getSize()) / 10);
    }
    
    @EventHandler
    public void on(PlayerBucketFillEvent event) {
        ItemStack item = event.getItemStack();
        Player player = event.getPlayer();

        if (item.getType() != Material.WATER_BUCKET) {
            return;
        }
        
        player.sendMessage(PREFIX + "You can't pick up water in Pyrophobia.");
        
        event.setItemStack(new ItemStack(Material.BUCKET));
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
    public void on(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        entity.addPotionEffect(FIRE_RES);
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
}