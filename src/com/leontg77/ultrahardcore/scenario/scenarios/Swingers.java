package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Swingers scenario class.
 * 
 * @author LeonTG77
 */
public class Swingers extends Scenario implements Listener {
	public static final String PREFIX = "§dSwingers §8» §7";
	
	private final TeamManager teams;
	private final Game game;

	/**
	 * Swingers class constructor.
	 * 
	 * @param game The game class.
	 * @param teams The team manager class.
	 */
	public Swingers(Game game, TeamManager teams) {
		super("Swingers", "You team with the first team you see that has the same teamsize as you, in order to get on a team with them right click the player.");
		
		this.teams = teams;
		this.game = game;
	}
	
	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Entity interact = event.getRightClicked();
		
		if (!(interact instanceof Player)) {
			return;
		}
		
		Player clicked = (Player) interact;
		Player player = event.getPlayer();
		
		if (!game.getPlayers().contains(player) || !game.getPlayers().contains(clicked)) {
			return;
		}

		if (teams.getTeam(player) == null) {
			player.sendMessage(ChatColor.RED + "You are not on a team");
			return;
		}
		
		if (teams.getTeam(clicked) == null) {
			player.sendMessage(ChatColor.RED + "That player is not on a team.");
			return;
		}

		Team team1 = teams.getTeam(clicked);
		Team team2 = teams.getTeam(player);
		
		if (team2.getSize() != game.getTeamManagementTeamsize()) {
			player.sendMessage(ChatColor.RED + "Your teamsize does not match the game teamsize.");
			return;
		}
		
		if (team1.equals(team2)) {
			player.sendMessage(ChatColor.RED + "That player is already on your team.");
			return;
		}
		
		if (team1.getSize() != team2.getSize()) {
			player.sendMessage(ChatColor.RED + "That team is not the same teamsize as yours.");
			return;
		}

		PlayerUtils.broadcast(PREFIX + "Team §a" + team1.getName() + listPlayers(teams.getPlayers(team1)) + " §7and§a " + team2.getName() + listPlayers(teams.getPlayers(team2)) + " §7has found each other.");
		
		for (OfflinePlayer players : teams.getPlayers(team1)) {
			teams.joinTeam(team2, players);
		}
	}
	
	/**
	 * Create a string list of the offline player list.
	 * 
	 * @param list The offline player list.
	 * @return The created string list.
	 */
	private String listPlayers(Set<OfflinePlayer> list) {
		StringBuilder builder = new StringBuilder();
		
		for (OfflinePlayer player : list) {
			if (builder.length() > 0) {
				builder.append("§8, §7");
			}
			
			builder.append("§7" + player.getName());
		}
		
		return "§8(" + builder.toString() + "§8)";
	}
}