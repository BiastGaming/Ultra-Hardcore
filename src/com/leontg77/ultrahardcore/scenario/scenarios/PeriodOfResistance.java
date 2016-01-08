package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * PeriodOfResistance scenario class
 * 
 * @author LeonTG77
 */
public class PeriodOfResistance extends Scenario implements Listener, CommandExecutor {
	private BukkitRunnable task;
	private DamageType current;
	
	public static final String PREFIX = "§7[§6Resistance§7] §7";
	
	public PeriodOfResistance() {
		super("PeriodOfResistance", "Every 10 minutes the resistance type changes, during the next 10 minutes you cannot take damage from what the type was.");
		Main main = Main.plugin;
		
		main.getCommand("status").setExecutor(this);
	}

	@Override
	public void onDisable() {
		task.cancel();
		task = null;
	}

	@Override
	public void onEnable() {
		if (State.isState(State.INGAME)) {
			on(new GameStartEvent());
		}
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		List<DamageType> types = ImmutableList.copyOf(Arrays.asList(DamageType.values()));
		Random rand = new Random();
		
		current = types.get(rand.nextInt(types.size()));
        PlayerUtils.broadcast(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will no longer hurt you!");
        
		task = new BukkitRunnable() {
			int seconds = 600;
			
			public void run() {
				seconds--;
				
				switch (seconds) {
	            case 300:
	                PlayerUtils.broadcast(PREFIX + "Changing resistant period in 5 minutes!");
	                break;
	            case 60:
	                PlayerUtils.broadcast(PREFIX + "Changing resistant period in 1 minute!");
	                break;
	            case 30:
	            case 10:
	            case 5:
	            case 4:
	            case 3:
	            case 2:
	                PlayerUtils.broadcast(PREFIX + "Changing resistant period in " + seconds + " seconds!");
	                break;
	            case 1:
	                PlayerUtils.broadcast(PREFIX + "Changing resistant period in 1 second!");
	                break;
	            case 0:
	                current = DamageType.values()[new Random().nextInt(DamageType.values().length)];
	                PlayerUtils.broadcast(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will no longer hurt you!");

	                seconds = 600;
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 20, 20);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		DamageCause cause = event.getCause();
		
		if (cause == DamageCause.FALL && current == DamageType.FALLING) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.POISON && current == DamageType.POISON) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.SUFFOCATION && current == DamageType.SUFFOCATION) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.STARVATION && current == DamageType.STARVATION) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.DROWNING && current == DamageType.DROWNING) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.BLOCK_EXPLOSION && current == DamageType.EXPLOSIONS) {
			event.setDamage(10000);
		}
		
		if ((cause == DamageCause.LAVA || cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK) && current == DamageType.LAVA_AND_FIRE) {
			event.setDamage(10000);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Entity damager = event.getDamager();
		
		if (damager instanceof Zombie && current == DamageType.ZOMBIES) {
			event.setCancelled(true);
		}
		
		if (damager instanceof Creeper && current == DamageType.EXPLOSIONS) {
			event.setCancelled(true);
		}
		
		if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Skeleton && current == DamageType.SKELETONS) {
			event.setCancelled(true);
		}
		
		if ((damager instanceof Spider || damager instanceof CaveSpider) && current == DamageType.SPIDERS) {
			event.setCancelled(true);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "\"PeriodOfResistance\" is not enabled.");
			return true;
		}
		
		sender.sendMessage(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will not hurt you!");
		return true;
	}
	
	public enum DamageType {
		FALLING, SUFFOCATION, LAVA_AND_FIRE, DROWNING, EXPLOSIONS, STARVATION, POISON, SPIDERS, SKELETONS, ZOMBIES;
	}
}