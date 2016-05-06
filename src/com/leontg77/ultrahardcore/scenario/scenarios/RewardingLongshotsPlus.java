package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * RewardingLongshots+ scenario class
 * 
 * @author LeonTG77
 */
public class RewardingLongshotsPlus extends Scenario implements Listener {
	
	public RewardingLongshotsPlus() {
		super("RewardingLongshots+", "When shooting and hitting people with a bow from a variable distance, you will be rewarded with various different items.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
			return;
		}
	
		Player player = (Player) event.getEntity();
		Arrow damager = (Arrow) event.getDamager();
		
		if (!(damager.getShooter() instanceof Player)) {
			return;
		}
		
		Player killer = (Player) damager.getShooter();
		double distance = killer.getLocation().distance(player.getLocation());
		
		if (distance < 30) {
			return;
		}

		PlayerUtils.broadcast("§7[§9Rewarding Longshots+§7] §f" + killer.getName() + " just got a longshot of " + NumberUtils.formatDouble(distance) + " meters!");
		
		if (distance <= 49) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 8));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			return;
		}

		if (distance <= 99) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 16));
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			return;
		} 

		if (distance <= 199) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.BOW));
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 32));
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND));
			return;
		}
		
		PlayerUtils.giveItem(killer, new ItemStack(Material.BOW, 2));
		PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 64));
		PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT, 3));
		PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT, 2));
		PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND, 5));
	}
}