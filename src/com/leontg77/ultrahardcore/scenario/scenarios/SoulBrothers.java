package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.EntityUtils;

/**
 * Soul Brothers scenario class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("unused")
public class SoulBrothers extends Scenario implements CommandExecutor {
	public static final String PREFIX = "§2Soul Brothers §8§ §7";

	private final Main plugin;
	private final Game game;

	private final ScatterManager scatter;
	private final TeamManager teams;
	
	/**
	 * Soul Brothers scenario class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 */
	public SoulBrothers(Main plugin, Game game, ScatterManager scatter, TeamManager teams) {
		super("SoulBrothers", "All teammates are seperated into their own worlds, at some point they will be teleported to a final world together.");

		this.plugin = plugin;
		this.game = game;

		this.scatter = scatter;
		this.teams = teams;
		
		plugin.getCommand("soul").setExecutor(this);
	}

	public List<World> getWorlds() {
		List<World> worlds = new ArrayList<World>();
		
		for (World world : game.getWorlds()) {
			if (world.getName().toLowerCase().contains("_sb_") && !world.getName().toLowerCase().endsWith("_sb_final")) {
				worlds.add(world);
			}
		}
		
		return worlds;
	}
	
	public World getFinalWorld() {
		for (World world : game.getWorlds()) {
			if (world.getName().toLowerCase().endsWith("_sb_final")) {
				return world;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	protected List<OfflinePlayer> getTeamPlayers(Team team) {
        return team.getEntries().stream()
                .map(Bukkit::getOfflinePlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Soul Brothers is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length == 0) {
			return true;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(PREFIX + "Help with hosting Soul Brothers:");
			sender.sendMessage(Main.ARROW + "§fFirst you decide how many worlds you need, then you create a world with a normal");
			sender.sendMessage(Main.ARROW + "§fworld name for a normal game, just that you end the world name with _sb_[worldnumber].");
			sender.sendMessage(Main.ARROW + "§fEx: 'leon_sb_1' and 'leon_sb_2' if you had 2 worlds (replace leon with your name).");
			sender.sendMessage(Main.ARROW + "§fNormally the worlds have the same seed, but for the final world aka the one all gets");
			sender.sendMessage(Main.ARROW + "§fscattered in at last needs to end with _sb_final! Ex: 'leon_sb_final'");
			sender.sendMessage(Main.ARROW + "§fWhen you start the first scatter you do /soul scatter");
			sender.sendMessage(Main.ARROW + "§fAnd for the final scatter you do /soul finalscatter, then /soul start to clear effects");
			sender.sendMessage(Main.ARROW + "§fYou use normal /start after the first scatter but /soul start is there for no countdown");
			sender.sendMessage(Main.ARROW + "§fand so it doesn't clear inventories. Have fun hosting, ask Leon if you got any questions!");
		}
		
		if (args[0].equalsIgnoreCase("scatter")) {
			State.setState(State.SCATTER);
			
			int worlds = getWorlds().size();
			
			List<String> toScatter = new ArrayList<String>();
			
			scatter.setTeamScatter(false);
			scatter.setWorld(null);
			
			try {
				scatter.scatter(ImmutableList.copyOf(toScatter));
			} catch (CommandException e) {
				e.printStackTrace();
			}
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
			}
			
			for (World world : game.getWorlds()) {
				world.setDifficulty(Difficulty.HARD);
				world.setPVP(false);
				world.setTime(0);
				
				world.setGameRuleValue("doDaylightCycle", "false");
				world.setThundering(false);
				world.setStorm(false);
				
				world.setSpawnFlags(false, true);
				
				for (Entity mob : world.getEntities()) {
					if (EntityUtils.isButcherable(mob.getType())) {
						mob.remove();
					}
				}
			}
		}
		
		if (args[0].equalsIgnoreCase("finalscatter")) {
			
		}
		
		if (args[0].equalsIgnoreCase("start")) {
			
		}
		
		return true;
	}
	
}