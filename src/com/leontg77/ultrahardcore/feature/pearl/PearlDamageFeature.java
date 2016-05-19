package com.leontg77.ultrahardcore.feature.pearl;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Pearl Damage feature class.
 * 
 * @author LeonTG77
 */
public class PearlDamageFeature extends Feature implements Listener {
    private static final String PATH = "feature.pearldamage.enabled";

    private final Settings settings;

    private double pearlDamage;

    public PearlDamageFeature(Settings settings) {
        super("Pearl Damage", "Modify how much damage enderpearls do.");

        pearlDamage = settings.getConfig().getDouble(PATH, 0);

        this.settings = settings;
    }

    /**
     * Set the amount of damage pearls should do.
     *
     * @param damage The damage to deal, 0 to cancel damage.
     */
    public void setPearlDamage(double damage) {
        this.pearlDamage = damage;

        settings.getConfig().set(PATH, damage);
        settings.saveConfig();
    }

    /**
     * Get the amount of damage pearls do.
     *
     * @return The damage amount.
     */
    public double getPearlDamage() {
        return pearlDamage;
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();

        if (!(attacker instanceof EnderPearl)) {
            return;
        }

        if (pearlDamage > 0.0) {
            event.setDamage(pearlDamage * 2);
            return;
        }

        event.setCancelled(true);
    }
}