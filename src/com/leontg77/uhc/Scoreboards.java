package com.leontg77.uhc;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * The class for managing scoreboards.
 * <p>
 * This class contains methods for setting scores, getting scores, setting up boards and resettings scores.
 * 
 * @author LeonTG77
 */
public class Scoreboards {
	private static Scoreboards manager = new Scoreboards();

	public Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	public Objective nameHealth = board.getObjective("nameHealth");
	public Objective tabHealth = board.getObjective("tabHealth");
	public Objective kills = board.getObjective("kills");
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return the instance.
	 */
	public static Scoreboards getManager() {
		return manager;
	}
	
	/**
	 * Setup the scoreboard objectives.
	 */
	public void setup() {
		if (board.getObjective("kills") == null) {
			kills = board.registerNewObjective("kills", "dummy");
		}
		
		if (board.getObjective("tabHealth") == null) {
			tabHealth = board.registerNewObjective("tabHealth", "dummy");
		}
		
		if (board.getObjective("nameHealth") == null) {
			nameHealth = board.registerNewObjective("nameHealth", "dummy");
		}
		
		kills.setDisplayName("§4UHC §8- §7" + Settings.getInstance().getConfig().getString("game.host"));
		kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		nameHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
		nameHealth.setDisplayName("§4♥");
		
		tabHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		Main.plugin.getLogger().info("Scoreboards has been setup.");
	}
	
	/**
	 * Sets the score of the given player.
	 * 
	 * @param player the player setting it for.
	 * @param newScore the new score.
	 */
	public void setScore(String player, int newScore) {
		Score score = kills.getScore(player);
		
		score.setScore(newScore);
	}

	/**
	 * Gets a score for the given string.
	 * 
	 * @param string the wanted string.
	 * @return The score of the string.
	 */
	public int getScore(String string) {
		return kills.getScore(string).getScore();
	}

	/**
	 * Reset the score of the given string.
	 * 
	 * @param string the string resetting.
	 */
	public void resetScore(String string) {
		board.resetScores(string);
	}
}