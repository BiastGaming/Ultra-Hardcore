package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Permakill scenario class.
 * 
 * @author LeonTG77
 */
public class Permakill extends Scenario implements Listener {

    public Permakill() {
        super("Permakill", "Everytime a player dies it toggles between perma day and perma night");
    }

    @Override
    public void onDisable() {
        game.getWorld().setGameRuleValue("doDaylightCycle", "true");
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        game.getWorld().setGameRuleValue("doDaylightCycle", "false");
        game.getWorld().setTime(6000);
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!game.getWorlds().contains(player.getWorld())) {
            return;
        }

        if (game.getWorld().getTime() == 6000) {
            game.getWorld().setTime(18000);
        } else {
            game.getWorld().setTime(6000);
        }
    }
}