package com.leontg77.ultrahardcore.commands.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Fireworks;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * End command class.
 * 
 * @author LeonTG77
 */
public class EndCommand extends UHCCommand {

	public EndCommand() {
		super("end", "<kills> <winners>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		int kills = parseInt(args[0], "kill amount");

		Spectator spec = Spectator.getInstance();
		Game game = Game.getInstance();
		
		List<String> winners = new ArrayList<String>();
		Settings settings = Settings.getInstance();
		
		PlayerUtils.broadcast(Main.PREFIX + "The game has ended and the winners are:");
		
		for (int i = 1; i < args.length; i++) {
			OfflinePlayer winner = PlayerUtils.getOfflinePlayer(args[i]);
			
			User user = User.get(winner);

			if (!game.isRecordedRound() && !game.isPrivateGame()) {
				user.getFile().set("stats.wins", user.getFile().getInt("stats.wins") + 1);
				user.saveFile();
			}
			
			PlayerUtils.broadcast(Main.PREFIX + args[i]);
			winners.add(args[i]);
		}
		
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "With �a" + kills + "�7 kills.");
		PlayerUtils.broadcast(Main.PREFIX + "View the hall of fame with �a/hof");
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String host = GameUtils.getHostName(game.getHost());
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();

		Fireworks firework = Fireworks.getInstance();
		FileUtils.updateUserFiles();
		
		InvGUI.getInstance().setup();
		
		int matchcount = 1;
		
		if (settings.getHOF().contains(host) && settings.getHOF().contains(host + ".games")) {
			matchcount = settings.getHOF().getConfigurationSection(host + ".games").getKeys(false).size() + 1;
		}

		if (!game.isRecordedRound()) {
			settings.getHOF().set(host + ".games." + matchcount + ".date", dateFormat.format(date));
			settings.getHOF().set(host + ".games." + matchcount + ".winners", winners);
			settings.getHOF().set(host + ".games." + matchcount + ".kills", kills);
			settings.getHOF().set(host + ".games." + matchcount + ".teamsize", GameUtils.getTeamSize(true, false).trim());
			settings.getHOF().set(host + ".games." + matchcount + ".scenarios", game.getScenarios());
			settings.saveHOF();
		}
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scen.setEnabled(false);
		}

		for (World world : Bukkit.getWorlds()) {
			world.save();
		}

		for (Player online : PlayerUtils.getPlayers()) {
			if (spec.isSpectating(online)) {
				spec.disableSpecmode(online);
			}
			
			for (Player onlineTwo : PlayerUtils.getPlayers()) {
				online.showPlayer(onlineTwo);
				onlineTwo.showPlayer(online);
			}

			online.setGameMode(GameMode.SURVIVAL);
			online.teleport(Main.getSpawn());
			online.setMaxHealth(20.0);
			online.setFireTicks(0);
			
			User user = User.get(online);
			user.reset();
			
			online.saveData();
		}
		
		State.setState(State.NOT_RUNNING);
		firework.startFireworkShow();
		
		Main.teamKills.clear();
		Main.kills.clear();

		Bukkit.getServer().setIdleTimeout(60);
		Main.plugin.saveData();

		game.setScenarios("games running");
		game.setAntiStripmine(true);
		game.setMatchPost("none");
		game.setMaxPlayers(150);
		game.setTeamSize("No");
		
		TeamManager teams = TeamManager.getInstance();
		teams.getSavedTeams().clear();
		Team team = teams.getTeam("spec");
		
		for (String member : team.getEntries()) {
			team.removeEntry(member);
		}

		for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
			whitelisted.setWhitelisted(false);
		}

		new BukkitRunnable() {
			public void run() {
				BoardManager board = BoardManager.getInstance();
				
				for (String entry : board.board.getEntries()) {
					board.resetScore(entry);
				}
				
				TeamManager teams = TeamManager.getInstance();
				
				for (Team team : teams.getTeams()) {
					for (String member : team.getEntries()) {
						team.removeEntry(member);
					}
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Reset scoreboards and teams.");

				WorldManager manager = WorldManager.getInstance();
				
				for (World world : GameUtils.getGameWorlds()) {
					manager.deleteWorld(world);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Deleted used worlds.");
			}
		}.runTaskLater(Main.plugin, 300);

		new BukkitRunnable() {
			public void run() {
				String kickMessage = 
				"�8� �cThanks for playing! �8�" +
			    "\n" + 
			    "\n�7If you'd like to know about updates and upcoming games," +
			    "\n�7you can follow us on twitter �a@ArcticUHC�7!";
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.kickPlayer(kickMessage);
				}
				
				Bukkit.shutdown();
			}
		}.runTaskLater(Main.plugin, 1200);
		
		if (Bukkit.getScheduler().isQueued(Timers.taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(Timers.taskMinutes)) {
			Bukkit.getScheduler().cancelTask(Timers.taskMinutes);
		}
		
		if (Bukkit.getScheduler().isQueued(Timers.taskSeconds) || Bukkit.getScheduler().isCurrentlyRunning(Timers.taskSeconds)) {
			Bukkit.getScheduler().cancelTask(Timers.taskSeconds);
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}