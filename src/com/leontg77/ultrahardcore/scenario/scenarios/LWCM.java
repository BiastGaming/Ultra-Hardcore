package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * LWCM scenario class.
 * 
 * @author LeonTG77
 */
public class LWCM extends Scenario implements Listener {
	public static final String PREFIX = "§dLWCM §8» §7";
	
	private final TeamManager teams;
	private final Game game;
	
	public LWCM(Game game, TeamManager teams) {
		super("LWCM", "This is a mix of \"Soul Brothers\" and \"LAFS\". Half of the players would be scattered in one world, and the other half would be scattered in the other world just like soul brothers. But instead of having teams, people would come as solos. The twist from LAFS is that when say for example: Player 1 is at X: 245 Y: 41 Z: -245 in World 1. When Player 2 randomly walks within 20 blocks radius of those coords in the other world, Player 1 and Player 2 would be put on a team. And just like in soul brothers, after a certain time the teams would be scattered together in World 3.");
		
		this.teams = teams;
		this.game = game;
	}
	
	@EventHandler
	public void on(PlayerMoveEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!game.getPlayers().contains(player)) {
			return;
		}
		
		Player near = null;
		
		for (Player online : game.getPlayers()) {
			if (online.getWorld().getName().equals(player.getWorld().getName())) {
				continue;
			}
			
			Location pLoc = player.getLocation().clone();
			pLoc.setWorld(online.getWorld());
			
			if (online.getLocation().distance(pLoc) <= 20) {
				near = online;
				break;
			}
		}
		
		if (near == null) {
			return;
		}
		
		if (teams.getTeam(player) != null) {
			return;
		}
		
		if (teams.getTeam(near) != null) {
			return;
		}
		
		Team team = teams.findAvailableTeam();
		
		if (team == null) {
			return;
		}

		teams.joinTeam(team, near);
		teams.joinTeam(team, player);
		
		PlayerUtils.broadcast(PREFIX + "§a" + player.getName() + " §7and§a " + near.getName() + " §7has found each other.");
	}
}