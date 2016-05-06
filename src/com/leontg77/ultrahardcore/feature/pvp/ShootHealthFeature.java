package com.leontg77.ultrahardcore.feature.pvp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Anonymous;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.SelfDiagnosis;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.scenario.scenarios.WTFIPTG;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Shoot Health feature class.
 * 
 * @author LeonTG77
 */
public class ShootHealthFeature extends Feature implements Listener {
	private final Main plugin;
	private final Game game;
	
	private final ScenarioManager scen;
	private final TeamManager teams;

	public ShootHealthFeature(Main plugin, Game game, ScenarioManager scen, TeamManager teams) {
		super("Shoot Health", "Displays the shot persons health to the shooter.");

		this.plugin = plugin;
		this.game = game;

		this.teams = teams;
		this.scen = scen;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent event) {
		Entity attacked = event.getEntity();
		Entity attacker = event.getDamager();
		
    	if (game.isRecordedRound() || scen.getScenario(TeamHealth.class).isEnabled() || scen.getScenario(SelfDiagnosis.class).isEnabled() || scen.getScenario(Paranoia.class).isEnabled() || scen.getScenario(WTFIPTG.class).isEnabled() || scen.getScenario(Anonymous.class).isEnabled()) {
			return;
		}
    	
    	if (!(attacked instanceof Player) || !(attacker instanceof Arrow)) {
			return;
		}
    	
    	final Player player = (Player) attacked;
		final Arrow arrow = (Arrow) attacker;
		
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		
		new BukkitRunnable() {
			public void run() {
				Player killer = (Player) arrow.getShooter();
				
				double health = player.getHealth();
				String percent = NumberUtils.makePercent(health);
				
				if (health > 0.0000) {
					killer.sendMessage(Main.PREFIX + name(player)+ " §7is now at §a" + percent + "%");
				}
			}
		}.runTaskLater(plugin, 1);
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