package com.leontg77.ultrahardcore.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.TeamJoinEvent;
import com.leontg77.ultrahardcore.events.TeamLeaveEvent;

/**
 * Team management class.
 * <p>
 * Contains methods for adding players to a team, removing players from a team, getting the team for a player and setting up teams.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class TeamManager {
	private final Scoreboard board;
	private final Main plugin;
	
	/**
	 * Team manager class constructor.
	 * 
	 * @param plugin The main class.
	 * @param board The board manager class.
	 */
	public TeamManager(Main plugin, BoardManager board) {
		this.plugin = plugin;
		
		this.board = board.getBoard();
	}

	private final Map<String, Set<String>> savedTeams = new HashMap<String, Set<String>>();
	private final List<Team> teams = new ArrayList<Team>();
	
	/**
	 * Find the first team with no players in it.
	 * 
	 * @return The available team, null if not found.
	 */
	public Team findAvailableTeam() {
		for (Team team : getTeams()) {
			if (team.getSize() == 0) {
				return team;
			}
		}
		
		return null;
	}

	/**
	 * Joins the specified team for the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param team the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(Team team, OfflinePlayer player) {	
		if (team == null) {
			return;
		}
		
		team.addPlayer(player);
		
		Bukkit.getPluginManager().callEvent(new TeamJoinEvent(team, player));
		
		if (!savedTeams.containsKey(team.getName())) {
			Set<String> players = new HashSet<String>(team.getEntries());
			
			savedTeams.put(team.getName(), players);
			return;
		}
		
		savedTeams.get(team.getName()).add(player.getName());
	}

	/**
	 * Joins the specified team for the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param name the name of the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(String name, OfflinePlayer player) {	
		Team team = getTeam(name);
		
		joinTeam(team, player);
	}
	
	/**
	 * Leaves the current team of the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param player the player thats leaving the team.
	 * @param unsave Wether to unsave the player from the team.
	 */
	public void leaveTeam(final OfflinePlayer player, final boolean unsave) {
		new BukkitRunnable() { // wait 5 ticks, incase they got removed after death but other events with higher priority wants to use the team.
			public void run() {
				Team team = getTeam(player);
				
				if (team == null) {
					return;
				}
				
				team.removePlayer(player);
				
				Bukkit.getPluginManager().callEvent(new TeamLeaveEvent(team, player));
				
				if (!unsave) {
					return;
				}
				
				if (!savedTeams.containsKey(team.getName())) {
					Set<String> players = new HashSet<String>(team.getEntries());
					
					savedTeams.put(team.getName(), players);
					return;
				}
				
				savedTeams.get(team.getName()).remove(player.getName());
				
				if (savedTeams.get(team.getName()).isEmpty()) {
					savedTeams.remove(team.getName());
				}
			}
		}.runTaskLater(plugin, 5);
	}
	
	/**
	 * Get the players on the given team.
	 * 
	 * @param team The given team.
	 * @return A list of OfflinePlayer's on the team.
	 */
	public Set<OfflinePlayer> getPlayers(Team team) {
		return team.getPlayers();
	}
	
	/**
	 * Sends a message to everyone on the given team.
	 * 
	 * @param team the team.
	 * @param message the message to send.
	 */
	public void sendMessage(Team team, String message) {
		for (OfflinePlayer teammate : getPlayers(team)) {
			Player online = teammate.getPlayer();
			
			if (online == null) {
				continue;
			}
			
			online.sendMessage(message);
		}
	}

	/**
	 * Gets the team of the given player player.
	 * 
	 * @param player the player in the team.
	 * @return The team, null if the player isn't on a team.
	 */
	public Team getTeam(OfflinePlayer player) {
		return board.getPlayerTeam(player);
	}

	/**
	 * Gets the team by a name.
	 * 
	 * @param name the name.
	 * @return The team.
	 */
	public Team getTeam(String name) {
		for (Team team : board.getTeams()) {
			if (team.getName().equalsIgnoreCase(name)) {
				return team;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a map of all saved teams.
	 * <p>
	 * The maps key string is the team name and the value Set is the players in it.
	 * 
	 * @return A map of all saved teams.
	 */
	public Map<String, Set<String>> getSavedTeams() {
		return savedTeams;
	}
	
	/**
	 * Get a set of all teams.
	 * 
	 * @return A set of all teams.
	 */
	public List<Team> getTeams() {
		return ImmutableList.copyOf(teams);
	}
	
	/**
	 * Get a list of all teams that has players on it.
	 * 
	 * @return A list of teams with players.
	 */
	public List<Team> getTeamsWithPlayers() {
		List<Team> teamsWithPlayers = new ArrayList<Team>();
		
		for (Team team : getTeams()) {
			if (team.getSize() > 0) {
				teamsWithPlayers.add(team);
			}
		}

		return ImmutableList.copyOf(teamsWithPlayers);
	}
	
	/**
	 * Set up all the teams.
	 */
	public void setup() {
		List<String> color = new ArrayList<String>();
		teams.clear();

		color.add("§1");
		color.add("§2");
		color.add("§3");
		color.add("§4");
		color.add("§5");
		color.add("§6");
		color.add("§7");
		color.add("§8");
		color.add("§9");
		color.add("§a");
		color.add("§b");
		color.add("§c");
		color.add("§d");
		color.add("§e");
		color.add("§f");

		Collections.shuffle(color);
		
		List<String> tempColorList = new ArrayList<String>();
		
		for (String li : color) {
			tempColorList.add(li + "§o");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§n");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§m");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§l");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§m§n");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§o§n");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§o§m");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§l§n");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§l§m");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§o§n§m");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§n§m");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§l§o§n");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§l§o§m");
		}
		
		for (String li : color) {
			tempColorList.add(li + "§l§o§n§m");
		}

		color.addAll(tempColorList);
		
		color.remove("§7§o");
		color.remove("§f");
		
		Team spec = (getTeam("spec") == null ? board.registerNewTeam("spec") : board.getTeam("spec"));
		spec.setDisplayName("spec");
		spec.setPrefix("§7§o");
		spec.setSuffix("§f");
		
		spec.setAllowFriendlyFire(false);
		spec.setCanSeeFriendlyInvisibles(true);	
		spec.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		
		for (int i = 0; i < color.size(); i++) {
			String teamName = "UHC" + (i + 1);
			
			Team team = (board.getTeam(teamName) == null ? board.registerNewTeam(teamName) : board.getTeam(teamName));
			team.setDisplayName(teamName);
			team.setPrefix(color.get(i));
			team.setSuffix("§f");
			
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(true);
			team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			
			teams.add(team);
		}

		plugin.getLogger().info("Setup " + (teams.size() + 1) + " teams.");
	}
}