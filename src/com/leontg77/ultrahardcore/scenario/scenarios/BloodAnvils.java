package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.AnvilRepairEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BloodAnvils scenario class
 * 
 * @author LeonTG77
 */
public class BloodAnvils extends Scenario implements Listener {

    /**
     * BloodAnvils class constructor.
     */
    public BloodAnvils() {
        super("BloodAnvils", "When you repair any item for any amount of levels, you take half a heart of damage.");
    }

    @EventHandler
    public void on(AnvilRepairEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerUtils.damage(player, 1);
    }
}