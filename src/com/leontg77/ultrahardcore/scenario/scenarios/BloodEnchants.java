package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BloodEnchants scenario class
 * 
 * @author LeonTG77
 */
public class BloodEnchants extends Scenario implements Listener {

    /**
     * BloodEnchants class constructor.
     */
    public BloodEnchants() {
        super("BloodEnchants", "When you enchant any item for any amount of levels, you take half a heart of damage.");
    }

    @EventHandler
    public void on(EnchantItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEnchanter();
        PlayerUtils.damage(player, 1);
    }
}