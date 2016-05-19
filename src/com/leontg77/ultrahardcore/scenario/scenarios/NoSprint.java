package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;

/**
 * No sprint scenario class
 * 
 * @author LeonTG77
 */
public class NoSprint extends Scenario implements Listener {

    public NoSprint() {
        super("NoSprint", "Disables sprinting");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(PlayerToggleSprintEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();

        if (!event.isSprinting()) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));

        PacketUtils.sendTitle(player, "§cNo sprinting!", "§7You are not allowed to sprint.", 15, 30, 15);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();

        if (!player.isSprinting()) {
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.BLINDNESS) || !player.hasPotionEffect(PotionEffectType.SLOW)) {
            return;
        }


        event.setCancelled(true);

        PacketUtils.sendTitle(player, "§cNo sprinting!", "§7You are not allowed to sprint.", 15, 30, 15);
    }
}