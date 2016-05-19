package com.leontg77.ultrahardcore.feature.xp;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Nerfed XP feature class.
 * 
 * @author LeonTG77
 */
public class NerfedXPFeature extends ToggleableFeature implements Listener {

    public NerfedXPFeature() {
        super("Nerfed XP", "Make all xp you pick up be 50% less than normal.");

        icon.setType(Material.EXP_BOTTLE);
        slot = 31;
    }

    @EventHandler
    public void on(PlayerExpChangeEvent event) {
        if (!isEnabled()) {
            return;
        }

        event.setAmount(event.getAmount() / 2);
    }
}