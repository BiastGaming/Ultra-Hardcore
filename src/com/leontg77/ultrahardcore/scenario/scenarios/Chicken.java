package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Chicken scenario class
 * 
 * @author LeonTG77
 */
public class Chicken extends Scenario implements Listener {

    public Chicken() {
        super("Chicken", "Everyone starts on 1/2 heart with a notch apple.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(final GameStartEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerUtils.giveItem(online, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
            online.setHealth(1);
        }
    }
}