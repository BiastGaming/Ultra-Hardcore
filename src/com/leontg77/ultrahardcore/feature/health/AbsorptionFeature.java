package com.leontg77.ultrahardcore.feature.health;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Absorption feature class.
 * 
 * @author LeonTG77
 */
public class AbsorptionFeature extends ToggleableFeature implements Listener {
    private final Main plugin;

    public AbsorptionFeature(Main plugin) {
        super("Absorption", "Golden apples gives absorption when consumed.");

        icon.setType(Material.GOLDEN_APPLE);
        slot = 0;

        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (isEnabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();

        if (item.getType() != Material.GOLDEN_APPLE) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                player.removePotionEffect(PotionEffectType.ABSORPTION);
            }
        }.runTask(plugin);
    }
}