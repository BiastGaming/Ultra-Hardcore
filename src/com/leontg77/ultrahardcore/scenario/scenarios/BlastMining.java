package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BlastMining scenario class.
 * 
 * @author LeonTG77
 */
public class BlastMining extends Scenario implements Listener {
	private final Main plugin;
	private final Game game;
	
	/**
	 * BlastMining class constructor.
	 * 
	 * @param game The game class.
	 */
	public BlastMining(Main plugin, Game game) {
		super("BlastMining", "When a player mines any ore (including nether quartz), there is a 5% chance that a creeper will spawn (creepers are given slowness 2 for 2 seconds, to give the player some time to react) and there's a 3% chance an ignited TNT will spawn. This will never happen simultaneously, meaning you'll only ever have to deal with one thing at a time.");

		this.plugin = plugin;
		this.game = game;
	}
	
	private final Random rand = new Random();
	
	@EventHandler
	public void on(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (!game.getPlayers().contains(player)) {
			return;
		}
		
		final Location loc = block.getLocation();
		loc.setX(loc.getBlockX() + 0.5);
		loc.setY(loc.getBlockY() + 0.5);
		loc.setZ(loc.getBlockZ() + 0.5);
		
		int randomChance = rand.nextInt(100);
		
		switch (block.getType()) {
		case GOLD_ORE:
		case IRON_ORE:
		case COAL_ORE:
		case LAPIS_ORE:
		case DIAMOND_ORE:
		case REDSTONE_ORE:
		case GLOWING_REDSTONE_ORE:
		case EMERALD_ORE:
		case QUARTZ_ORE:
			if (randomChance < 5) {
				new BukkitRunnable() {
					public void run() {
						Creeper creep = loc.getWorld().spawn(loc, Creeper.class);
						creep.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
					}
				}.runTaskLater(plugin, 5);
				return;
			}
			randomChance -= 5;
			
			if (randomChance < 3) {
				new BukkitRunnable() {
					public void run() {
						TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
						tnt.setFuseTicks(80);
					}
				}.runTaskLater(plugin, 1);
				return;
			}
			break;
		default:
			break;
		}
	}
}