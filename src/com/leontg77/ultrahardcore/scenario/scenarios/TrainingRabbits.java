package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * TrainingRabbits scenario class.
 * 
 * @author LeonTG77
 */
public class TrainingRabbits extends Scenario implements Listener {
    private static final PotionEffectType EFFECT_TYPE = PotionEffectType.JUMP;

    public TrainingRabbits() {
        super("TrainingRabbits", "Everyone gets jump boost 2 for the entire game, and as you get kills, your level of jump boost will increase. Fall damage is disabled.");
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.removePotionEffect(PotionEffectType.JUMP);
        }
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
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.addPotionEffect(new PotionEffect(EFFECT_TYPE, NumberUtils.TICKS_IN_999_DAYS, 1));
        }
    }
    
    /**
     * Get the current jump boost level of the given player.
     * 
     * @param player The player to get for.
     * @return The jump level.
     */
    private int getJumpLevel(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (!effect.getType().equals(EFFECT_TYPE)) {
                continue;
            }
            
            return effect.getAmplifier();
        }
        
        return 1;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }

        int level = getJumpLevel(player);
        
        player.removePotionEffect(EFFECT_TYPE);
        player.addPotionEffect(new PotionEffect(EFFECT_TYPE, NumberUtils.TICKS_IN_999_DAYS, level));
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        
        if (!player.hasPotionEffect(EFFECT_TYPE)) {
            return;
        }
        
        ItemStack item = event.getItem();

        if (item.getType() != Material.MILK_BUCKET) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't drink milk in " + getName() + ".");
        
        event.setItem(new ItemStack(Material.BUCKET));
        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getEntity();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        Player killer = player.getKiller();

        if (killer == null) {
            return;
        }

        int level = getJumpLevel(killer) + 1;
        
        player.removePotionEffect(EFFECT_TYPE);
        player.addPotionEffect(new PotionEffect(EFFECT_TYPE, NumberUtils.TICKS_IN_999_DAYS, level));
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        if (event.getCause() == DamageCause.FALL) {
            event.setCancelled(true);
        }
    }
}