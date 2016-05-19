package com.leontg77.ultrahardcore.feature.horses;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Horses feature class.
 * 
 * @author LeonTG77
 */
public class HorseFeature extends ToggleableFeature implements Listener {

    public HorseFeature() {
        super("Horses", "A mob that lets you ride around.");

        icon.setType(Material.SADDLE);
        slot = 24;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            kickOffHorse(player);
        }
    }

    // also called when a player joins the game and is re-mounted
    @EventHandler
    public void on(EntityMountEvent event) {
        if (isEnabled()) {
            return;
        }
        
        final Entity entity = event.getEntity();
        final Entity mount = event.getMount();
        
        if (!(entity instanceof Player)) {
            return;
        }

        if (mount.getType() != EntityType.HORSE) {
            return;
        }

        entity.sendMessage(Main.PREFIX + "Horses are disabled.");
        event.setCancelled(true);
    }

    /**
     * Kick the given player out of his horse.
     *
     * @param player The player to kick off.
     */
    private void kickOffHorse(Player player) {
        final Entity vehicle = player.getVehicle();
        
        if (vehicle == null) {
            return;
        }
        
        if (vehicle.getType() != EntityType.HORSE) {
            return;
        }

        player.sendMessage(Main.PREFIX + "Horses are disabled.");
        vehicle.eject();
    }
}