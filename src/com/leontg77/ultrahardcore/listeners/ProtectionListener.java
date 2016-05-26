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
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

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
    public void on(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        World world = block.getWorld();
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }

        if (player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        World world = block.getWorld();

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
    public void on(VehicleDamageEvent event) {
        Entity damager = event.getAttacker();

        if (!(damager instanceof Player)) {
            return;
        }
        
        Player player = (Player) damager;
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
    public void on(HangingBreakEvent event) {
        if (event instanceof HangingBreakByEntityEvent) {
            on((HangingBreakByEntityEvent) event);
            return;
        }
        
        Entity entity = event.getEntity();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        World world = entity.getWorld();
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(HangingBreakByEntityEvent event) {
        Entity damager = event.getRemover();

        if (!(damager instanceof Player)) {
            return;
        }
        
        Player player = (Player) damager;
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
        if (event instanceof EntityDamageByEntityEvent) {
            on((EntityDamageByEntityEvent) event);
            return;
        }
        
        Entity entity = event.getEntity();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        World world = entity.getWorld();
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }

        event.setCancelled(true);
    }
    
    private void on(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        
        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }

        if (!(damager instanceof Player)) {
            return;
        }
        
        Player player = (Player) damager;
        World world = player.getWorld();
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }
        
        if (!(entity instanceof Player) && player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        Player player = (Player) event.getPlayer();
        World world = player.getWorld();

        if (game.isState(State.SCATTER)) {
            event.setCancelled(true);
            return;
        }
        
        if (game.getWorlds().contains(world) || world.getName().equals("arena")) {
            return;
        }
        
        if (player.hasPermission("uhc.build")) {
            return;
        }

        event.setCancelled(true);
    }
}