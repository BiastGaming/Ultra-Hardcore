package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Birds scenario class.
 * 
 * @author LeonTG77
 */
public class Birds extends Scenario implements Listener {
	private final Timer timer;
	
	/**
	 * Birds class constructor.
	 * 
	 * @param timer The timer class.
	 */
	public Birds(Timer timer) {
		super("Birds", "All players can fly!");
		
		this.timer = timer;
	}
	
	public static final String PREFIX = "§aBirds §8§ §7";
	
	@Override
	public void onDisable() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setAllowFlight(false);
			online.setFlying(false);
		}
	}

	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME) || timer.getTimeSinceStartInSeconds() < 20) {
			return;
		}
		
		on(new FinalHealEvent());
	}
	
	@EventHandler
	public void on(FinalHealEvent event) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setAllowFlight(true);
		}
	}
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		if (!State.isState(State.INGAME) || timer.getTimeSinceStartInSeconds() < 20) {
			return;
		}
		
		Player player = event.getPlayer();
		
		player.setAllowFlight(true);
		player.setFlying(true);
	}
	
	@EventHandler
	public void on(PlayerMoveEvent event) {
		if (!State.isState(State.INGAME) || timer.getTimeSinceStartInSeconds() < 20) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (player.isFlying() && event.getTo().getY() > 255) {
			player.sendMessage(PREFIX + "You cannot fly above the height limit.");
			event.setCancelled(true);
		}
	}
}