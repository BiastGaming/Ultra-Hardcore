package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Achievements scenario class.
 * 
 * @author LeonTG77
 */
public class Achievements extends Scenario implements Listener {

    public Achievements() {
        super("Achievements", "Everytime you get an achievement you get 1 extra half heart.");
    }

    @EventHandler
    public void on(PlayerAchievementAwardedEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        player.setMaxHealth(player.getMaxHealth() + 1);
        player.setHealth(player.getHealth() + 1);
    }
}