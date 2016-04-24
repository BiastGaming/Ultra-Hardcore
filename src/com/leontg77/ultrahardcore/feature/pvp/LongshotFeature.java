package com.leontg77.ultrahardcore.feature.pvp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Anonymous;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshots;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshotsPlus;
import com.leontg77.ultrahardcore.scenario.scenarios.WTFIPTG;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Longshot feature class.
 * 
 * @author LeonTG77
 */
public class LongshotFeature extends Feature implements Listener {
	private final Game game;
	
	private final ScenarioManager scen;
	private final TeamManager teams;

	public LongshotFeature(Game game, ScenarioManager scen, TeamManager teams) {
		super("Longshot", "Displays in the chat if someone gets a long shot of 50+ blocks.");
		
		this.game = game;
		
		this.teams = teams;
		this.scen = scen;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent event) {
		Entity attacked = event.getEntity();
		Entity attacker = event.getDamager();
		
    	if (game.isRecordedRound() || scen.getScenario(RewardingLongshots.class).isEnabled() || scen.getScenario(RewardingLongshotsPlus.class).isEnabled() || scen.getScenario(WTFIPTG.class).isEnabled() || scen.getScenario(Anonymous.class).isEnabled()) {
			return;
		}
    	
    	if (!(attacked instanceof Player) || !(attacker instanceof Arrow)) {
			return;
		}
		
		Player player = (Player) attacked;
		Arrow arrow = (Arrow) attacker;
		
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		
		Player killer = (Player) arrow.getShooter();
		double distance = killer.getLocation().distance(player.getLocation());
		
		if (distance < 50) {
			return;
		}
		
		PlayerUtils.broadcast(Main.PREFIX + name(killer) + " §7got a longshot of §a" + NumberUtils.formatDouble(distance) + " §7blocks on §6" + name(player) + "§7.");
	}
	
	/**
	 * Get the name of the given player with the team color if any.
	 * 
	 * @param player The player to use.
	 * @return The name with the team color.
	 */
	private String name(Player player) {
		Team team = teams.getTeam(player);
		
		if (team == null) {
			return ChatColor.WHITE + player.getName();
		} else {
			return team.getPrefix() + player.getName() + team.getSuffix();
		}
	}
}