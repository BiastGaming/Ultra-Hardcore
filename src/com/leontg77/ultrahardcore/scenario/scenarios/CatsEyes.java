package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * CatsEyes scenario class
 * 
 * @author LeonTG77
 */
public class CatsEyes extends Scenario implements Listener {
    private static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.NIGHT_VISION, NumberUtils.TICKS_IN_999_DAYS, 1);

    public CatsEyes() {
        super("CatsEyes", "All players are given Night Vision at the start of the game.");
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.removePotionEffect(EFFECT.getType());
        }
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final GameStartEvent event)  {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.addPotionEffect(EFFECT);
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();

        player.addPotionEffect(EFFECT);
    }
}