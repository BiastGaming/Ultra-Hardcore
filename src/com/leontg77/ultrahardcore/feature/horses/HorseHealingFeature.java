package com.leontg77.ultrahardcore.feature.horses;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Horse healing feature class.
 * 
 * @author LeonTG77
 */
public class HorseHealingFeature extends ToggleableFeature implements Listener {
    private static final Set<Material> DISABLED = ImmutableSet.of(Material.SUGAR, Material.WHEAT, Material.APPLE, Material.GOLDEN_CARROT, Material.GOLDEN_APPLE, Material.HAY_BLOCK);

    public HorseHealingFeature() {
        super("Horse Healing", "Some food items can be used to heal horses.");

        icon.setType(Material.BREAD);
        slot = 26;
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        if (isEnabled()) {
            return;
        }
        
        final Entity clicked = event.getRightClicked();
        final Player player = event.getPlayer();

        if (!(clicked instanceof Horse)) {
            return;
        }

        final ItemStack hand = player.getItemInHand();

        if (hand == null) {
            return;
        }

        if (!DISABLED.contains(hand.getType())) {
            return;
        }

        player.sendMessage(Main.PREFIX + "Horse healing is disabled.");
        event.setCancelled(true);

        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityRegainHealthEvent event) {
        if (isEnabled()) {
            return;
        }

        if (event.getEntityType() != EntityType.HORSE) {
            return;
        }

        event.setCancelled(true);
    }
}