package com.leontg77.ultrahardcore;

import java.util.List;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Announcer class.
 * <p>
 * Used for sending useful information every so often.
 * 
 * @author LeonTG77
 */
public class Announcer {
	private final Main plugin;
	private final Game game;
	
	/**
	 * Announcer class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 */
	protected Announcer(Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	/**
	 * List of announcements.
	 */
	private static final List<String> ANNOUNCEMENTS = ImmutableList.of(
			"Remember to use §e/uhc §7for all game information.",
			"You can look at the hall of fame with §e/hof§7.",
			"If you have any questions, ask in §e/helpop§7.",
			"You can find the match post by doing §e/post§7.",
			"The server runs off a custom UHC plugin by §eLeonTG77§7.",
			"Wonder if you are lagging? Use §e/ms §7or §e/tps§7.",
			"Follow our twitter for games and updates, §6twitter.com/ArcticUHC§7!",
			"You can apply for a rank at §6redd.it/45gxj0",
			"You can report staff at §6redd.it/45gxj0",
			"You can give us suggestions at §6redd.it/45gxj0",
			"You can view your stats by using §e/stats§7.",
			"You can view the top 10 stats with §e/top§7.",
			"View the border size with §e/border§7.",
			"Wondering how long it is until PvP/Meetup? Use §e/timeleft§7.",
			"View the enabled scenario(s) by doing §e/scen§7.",
			"Check if you are combat tagged with §a/ct§7."
	);
	
	private final Random rand = new Random();
	
	/**
	 * Start the announcer timer.
	 */
	public void startAnnouncer() {
		new BukkitRunnable() {
			public void run() {
				if (game.isRecordedRound() || game.isPrivateGame() || !State.isState(State.INGAME)) {
					return;
				}
				
				PlayerUtils.broadcast(Main.INFO_PREFIX + ANNOUNCEMENTS.get(rand.nextInt(ANNOUNCEMENTS.size())));
			}
		}.runTaskTimer(plugin, 10000, 10000);
		
		plugin.getLogger().info("Started the announcer timer.");
	}
}