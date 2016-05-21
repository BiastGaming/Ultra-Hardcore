package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.AnvilRepairEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BloodEnchants+ scenario class.
 * 
 * @author LeonTG77
 */
public class BloodEnchantsPlus extends Scenario implements Listener {

    /**
     * BloodEnchants+ class constructor.
     */
    public BloodEnchantsPlus() {
        super("BloodEnchants+", "Every level of enchanting you use you lose 1/2 a heart for. e.g. if you spend 5 levels of EXP on enchantments you lose 2.5 hearts, This includes Anvils as well.");
    }

    @EventHandler
    public void on(EnchantItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEnchanter();
        PlayerUtils.damage(player, event.getExpLevelCost());
    }

    @EventHandler
    public void on(AnvilRepairEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerUtils.damage(player, event.getRepairCost());
    }
}