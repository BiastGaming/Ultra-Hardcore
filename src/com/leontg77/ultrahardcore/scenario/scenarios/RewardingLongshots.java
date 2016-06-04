package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * RewardingLongshots scenario class.
 * 
 * @author LeonTG77
 */
public class RewardingLongshots extends Scenario implements Listener {
    public static final String PREFIX = "§7[§9Rewarding Longshots§7] §f";

    public RewardingLongshots() {
        super("RewardingLongshots", "When shooting and hitting people with a bow from a variable distance, you will be rewarded with various different items.");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        
        if (!(damager instanceof Arrow) || !(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        Arrow arrow = (Arrow) damager;

        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player killer = (Player) arrow.getShooter();
        
        if (!game.getPlayers().contains(killer)) {
            return;
        }
        
        double distance = killer.getLocation().distance(player.getLocation());

        if (distance < 30) {
            return;
        }

        PlayerUtils.broadcast(PREFIX + killer.getName() + " just got a longshot of " + NumberUtils.formatDouble(distance) + " blocks!");

        if (distance <= 49) {
            PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
            return;
        }

        if (distance <= 99) {
            PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
            PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
            return;
        }

        if (distance <= 199) {
            PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
            PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
            PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND));
            return;
        }

        PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT, 3));
        PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT, 2));
        PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND, 5));
    }
}