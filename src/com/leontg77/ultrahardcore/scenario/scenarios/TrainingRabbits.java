package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * TrainingRabbits scenario class
 * 
 * @author LeonTG77
 */
public class TrainingRabbits extends Scenario implements Listener {
    private final Timer timer;

    public TrainingRabbits(Timer timer) {
        super("TrainingRabbits", "Everyone gets jump boost 2 for the entire game, and as you get kills, your level of jump boost will increase. Fall damage is disabled.");

        this.timer = timer;
    }

    public final Map<String, Integer> jump = new HashMap<String, Integer>();

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.removePotionEffect(PotionEffectType.JUMP);
        }

        jump.clear();
    }

    @Override
    public void onEnable() {
        jump.clear();
    }

    @EventHandler
    public void on(GameStartEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, NumberUtils.TICKS_IN_999_DAYS, 1));
            jump.put(online.getName(), 1);
        }
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() != Material.MILK_BUCKET) {
            return;
        }

        player.sendMessage(Main.PREFIX + "You cannot drink milk in TrainingRabbits.");
        event.setItem(new ItemStack (Material.AIR));
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!State.isState(State.INGAME) || timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }

        Player player = event.getPlayer();

        if (!jump.containsKey(player.getName())) {
            jump.put(player.getName(), 1);
        }

        if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
            int level = jump.get(player.getName());

            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, NumberUtils.TICKS_IN_999_DAYS, level));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();

        if (jump.containsKey(player.getName())) {
            jump.put(player.getName(), jump.get(player.getName()) + 1);
        } else {
            jump.put(player.getName(), 2);
        }

        int level = jump.get(player.getName());

        player.removePotionEffect(PotionEffectType.JUMP);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, NumberUtils.TICKS_IN_999_DAYS, level));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() != DamageCause.FALL) {
            return;
        }

        event.setCancelled(true);
    }
}