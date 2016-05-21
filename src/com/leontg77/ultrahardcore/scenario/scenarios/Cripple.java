package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Cripple scenario class.
 *  
 * @author LeonTG77
 */
public class Cripple extends Scenario implements Listener {

    public Cripple() {
        super("Cripple", "If you take fall damage you will get slowness for 30 seconds");
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        if (event.getCause() != DamageCause.FALL) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 0));
    }
}