package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * PotentialHearts scenario class
 * 
 * @author LeonTG77
 */
public class PotentialHearts extends Scenario implements Listener {

    public PotentialHearts() {
        super("PotentialHearts", "Everyone starts off with 10 hearts and 10 unhealed potential hearts you need to heal by yourself.");
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setMaxHealth(20);
        }
    }

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(FinalHealEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setMaxHealth(40);
        }
    }
}