package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DeathSentence scenario class
 * 
 * @author LeonTG77
 */
public class DeathSentence extends Scenario implements CommandExecutor, Listener {
	private static final String PREFIX = "§7Death Sentence §8§ §c";
	
	private final Main plugin;
	private final Game game;
	
	private final Map<UUID, Double> time = new HashMap<UUID, Double>();

	public DeathSentence(Main plugin, Game game) {
		super("DeathSentence", "Players are given 10 minutes of their lives. After their 10 Minutes run out, the player dies. However, if a player mines a specific ore or if they kill a player, they will gain a certain amount of time to their lives.");
	
		this.plugin = plugin;
		this.game = game;
		
		plugin.getCommand("dtime").setExecutor(this);
	}
	
	private BukkitRunnable task;

	@Override
	public void onDisable() {
		if (task != null) {
			task.cancel();
		}
		
		task = null;
	}

	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME)) {
			return;
		}

		on(new GameStartEvent());
	}
	
	@EventHandler(ignoreCancelled = true)
    public void on(GameStartEvent event)  {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : game.getPlayers()) {
					if (!time.containsKey(online.getUniqueId())) {
						time.put(online.getUniqueId(), 10.0);
					}
					
					if (time.get(online.getUniqueId()) <= 0.0) {
						PlayerUtils.broadcast(PREFIX + online.getName() + " ran out of lifefile.");
						online.setHealth(0);
					} else {
						online.sendMessage(PREFIX + "You lost 1 minute of your lifetime.");
						time.put(online.getUniqueId(), time.get(online.getUniqueId()) - 1);
					}
				}
			}
		};
		
		task.runTaskTimer(plugin, 1200L, 1200L);
    }

	@EventHandler
	public void on(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!game.getPlayers().contains(player)) {
			return;
		}
		
		Block block = event.getBlock();

		if (!time.containsKey(player.getUniqueId())) {
			time.put(player.getUniqueId(), 10.0);
		}
		
		switch (block.getType()) {
		case IRON_ORE:
			player.sendMessage(PREFIX + "You gained 0.5 more minutes of lifetime.");
			time.put(player.getUniqueId(), time.get(player.getUniqueId()) + 0.5);
			break;
		case GOLD_ORE:
			player.sendMessage(PREFIX + "You gained 2 more minutes of lifetime.");
			time.put(player.getUniqueId(), time.get(player.getUniqueId()) + 2);
			break;
		case DIAMOND_ORE:
			player.sendMessage(PREFIX + "You gained 5 more minutes of lifetime.");
			time.put(player.getUniqueId(), time.get(player.getUniqueId()) + 5);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}

		Player player = event.getPlayer();
		
		if (!game.getPlayers().contains(player)) {
			return;
		}
		
		Block block = event.getBlock();

		if (!time.containsKey(player.getUniqueId())) {
			time.put(player.getUniqueId(), 10.0);
		}
		
		switch (block.getType()) {
		case IRON_ORE:
		case GOLD_ORE:
		case DIAMOND_ORE:
			player.sendMessage(PREFIX + "You cannot place this to gain more time.");
			event.setCancelled(true);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getEntity();
		Player killer = player.getKiller();
		
		if (!game.getWorlds().contains(player.getWorld())) {
			return;
		}
		
		player.sendMessage(PREFIX + "Looks like your Death Sentence came unexpectedly...");
		
		if (killer == null) {
			return;
		}

		if (!game.getWorlds().contains(killer.getWorld())) {
			return;
		}
		
		if (!time.containsKey(killer.getUniqueId())) {
			time.put(killer.getUniqueId(), 10.0);
		}
		
		time.put(killer.getUniqueId(), time.get(killer.getUniqueId()) + 10);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Death Sentence is not enabled.");
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can perform /dtime.");
			return true;
		}
		
		Player player = (Player) sender;

		if (!time.containsKey(player.getUniqueId())) {
			sender.sendMessage(PREFIX + "No Death Sentence time has been set for you yet.");
			return true;
		}
		
		double timeL = time.get(player.getUniqueId());
		ChatColor color;
		
		if (timeL <= 2) {
			color = ChatColor.RED;
		} else if (timeL < 10) {
			color = ChatColor.YELLOW;
		} else {
			color = ChatColor.GREEN;
		}
		
		player.sendMessage(PREFIX + "§fYou have " + color + (timeL == 1 ? "1 minute" : timeL + " minutes") + " minute §7remaining.");
		return true;
	}
}