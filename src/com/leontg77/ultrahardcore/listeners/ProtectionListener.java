package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;

/**
 * Protection listener class.
 * <p> 
 * All events in this class is for protecting certain worlds from being destroyed
 * 
 * @author LeonTG77
 */
public class ProtectionListener implements Listener {
    private final Game game;

    /**
     * Player listener class constructor.
     *
     * @param game The game class.
     */
    public ProtectionListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void on(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        final World world = block.getWorld();
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }

        if (player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        final Player player = event.getPlayer();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        final World world = block.getWorld();

        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }

        if (player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        Block block = event.getClickedBlock();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }

        // hopping on farms
        if (action == Action.PHYSICAL) {
            if (block == null) {
                return;
            }

            if (block.getType() != Material.SOIL) {
                return;
            }

            event.setCancelled(true);
            return;
        }
        
        if (player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerArmorStandManipulateEvent event) {
        ArmorStand stand = event.getRightClicked();
        Player player = event.getPlayer();
        
        if (!stand.isVisible()) {
            event.setCancelled(true);
            return;
        }

        World world = player.getWorld();
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }
        
        if (player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        if (!entity.getWorld().getName().equals("lobby")) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent && shouldCancelDamageByOther((EntityDamageByEntityEvent) event)) {
            return;
        }

        switch (entity.getType()) {
        case ARMOR_STAND:
        case BOAT:
        case ENDER_CRYSTAL:
        case ITEM_FRAME:
        case LEASH_HITCH:
        case MINECART:
        case MINECART_CHEST:
        case MINECART_COMMAND:
        case MINECART_FURNACE:
        case MINECART_HOPPER:
        case MINECART_MOB_SPAWNER:
        case MINECART_TNT:
        case PAINTING:
        case PLAYER:
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }
 
    /**
     * Check if the entity damage by entity event should return the entity damge event int he method above.
     *
     * @param event The entity damage by entity event.
     * @return True if it should, false otherwise.
     */
    private boolean shouldCancelDamageByOther(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();

        if (damager instanceof Player && damager.hasPermission("uhc.build")) {
            return true;
        }

        return false;
    }
}