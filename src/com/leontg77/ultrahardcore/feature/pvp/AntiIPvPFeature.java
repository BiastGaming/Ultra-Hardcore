package com.leontg77.ultrahardcore.feature.pvp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Bow;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Anti iPvP feature class.
 * 
 * @author LeonTG77
 */
public class AntiIPvPFeature extends Feature implements Listener {
	private static final String PREFIX = "§8[§4§liPvP§8] §7";

	private final ScenarioManager scen;
    private final Game game;

	private final TeamManager manager;
    private final SpecManager spec;

	public AntiIPvPFeature(Game game, TeamManager manager, SpecManager spec, ScenarioManager scen) {
		super("Anti-iPvP", "Disable all players to indirect damage other players before pvp.");

		this.game = game;
		this.scen = scen;
		
		this.manager = manager;
		this.spec = spec;
	}
    
    @EventHandler
    public void on(final PlayerBucketEmptyEvent event) {
    	final Block clicked = event.getBlockClicked();
    	final BlockFace face = event.getBlockFace();
    	
    	final Block block = clicked.getRelative(face);
    	final Player player = event.getPlayer();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        if (!game.getWorlds().contains(player.getWorld())) {
        	return;
        }
        
        // if pvp is enabled we want them to be able to iPvP
        if (player.getWorld().getPVP() || scen.getScenario(Bow.class).isEnabled()) {
        	return;
        }
    
		if (event.getBucket() != Material.LAVA_BUCKET) {
			return;
		}
		
		if (!isAnyoneNearby(player, block.getLocation())) {
			return;
    	}
		
		player.sendMessage(PREFIX + "iPvP is currently disallowed.");
		player.sendMessage(PREFIX + "Stop iPvPing now or staff will take action.");
		
    	event.setCancelled(true);
    }
	
	@EventHandler
	public void on(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        if (!game.getWorlds().contains(player.getWorld())) {
        	return;
        }
        
        // if pvp is enabled we want them to be able to iPvP
        if (player.getWorld().getPVP() || scen.getScenario(Bow.class).isEnabled()) {
        	return;
        }
        
    	if (block == null) {
        	return;
    	}
    	
    	if (isSuffocation(player, event) || isDamageBlock(player, event)) {
			player.sendMessage(PREFIX + "iPvP is currently disallowed.");
			player.sendMessage(PREFIX + "Stop iPvPing now or staff will take action.");
			
        	event.setCancelled(true);
    	}
	}
	
	@EventHandler
	public void on(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        if (!game.getWorlds().contains(player.getWorld())) {
        	return;
        }
        
        // if pvp is enabled we want them to be able to iPvP
        if (player.getWorld().getPVP() || scen.getScenario(Bow.class).isEnabled()) {
        	return;
        }
        
    	if (block == null) {
        	return;
    	}
        
    	if (isSpleef(player, event) || isBreakSuffocation(player, event)) {
			player.sendMessage(PREFIX + "iPvP is currently disallowed.");
			player.sendMessage(PREFIX + "Stop iPvPing now or staff will take action.");
			
        	event.setCancelled(true);
    	}
	}

	/**
	 * Check if the given iPvPer is using fire, lava or cactus to damage someone.
	 * 
	 * @param iPvPer The iPvPer.
	 * @param event The block place event that gets the block they are placing.
	 * @return True if he is and alerts the staff, false otherwise.
	 */
	private boolean isDamageBlock(Player iPvPer, BlockPlaceEvent event) {
		final Block block = event.getBlock();
		boolean isiPvPBlock = false;
		
		switch (block.getType()) {
		case CACTUS:
		case FIRE:
		case TNT:
			isiPvPBlock = true;
			break;
		default:
			isiPvPBlock = false;
			break;
		}
		
		return isiPvPBlock && isAnyoneNearby(iPvPer, block.getLocation());
	}

	/**
	 * Check if the given iPvPer is breaking a block that will make someone below that block get suffocated.
	 * 
	 * @param iPvPer The iPvPer.
	 * @param event The block break event that gets the block they are breaking.
	 * @return True if he is and alerts the staff, false otherwise.
	 */
	private boolean isBreakSuffocation(Player iPvPer, BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Location loc = block.getLocation();
		
		boolean isPlayerBelow = false;
		boolean isFallingBlock;

		final Team playerTeam = manager.getTeam(iPvPer);
		
		final Block above = block.getRelative(BlockFace.UP, 1);
		
		switch (above.getType()) {
		case SAND:
		case GRAVEL:
		case ANVIL:
			isFallingBlock = true;
			break;
		default:
			isFallingBlock = false;
			return false;
		}
		
		for (Player inWorld : iPvPer.getWorld().getPlayers()) {
			if (inWorld == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(inWorld)) {
				continue;
			}
			
			final Team nearTeam = manager.getTeam(inWorld);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}
			
			final Location pLoc = inWorld.getLocation();
			
			if (pLoc.getBlockX() == loc.getBlockX() && pLoc.getBlockY() < loc.getBlockY() && loc.getBlockZ() == pLoc.getBlockZ()) {
				PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-§ §a" + inWorld.getName() + " §8[§7Suffocation§8]", "uhc.staff");
				isPlayerBelow = true;
				break;
			}
		}
		
		return isFallingBlock && isPlayerBelow;
	}

	/**
	 * Check if the given iPvPer is placing falling blocks above someone to suffocate them.
	 * 
	 * @param iPvPer The iPvPer.
	 * @param event The block place event that gets the block they are placing.
	 * @return True if he is and alerts the staff, false otherwise.
	 */
	private boolean isSuffocation(Player iPvPer, BlockPlaceEvent event) {
		final Block block = event.getBlock();
		final Location loc = block.getLocation();
		
		boolean isPlayerBelow = false;
		boolean isFallingBlock;

		final Team playerTeam = manager.getTeam(iPvPer);
		
		switch (block.getType()) {
		case SAND:
		case GRAVEL:
		case ANVIL:
			isFallingBlock = true;
			break;
		default:
			isFallingBlock = false;
			return false;
		}
		
		final Block below = block.getRelative(BlockFace.DOWN, 1);
		
		for (Player inWorld : iPvPer.getWorld().getPlayers()) {
			if (inWorld == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(inWorld)) {
				continue;
			}
			
			final Team nearTeam = manager.getTeam(inWorld);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}
			
			final Location pLoc = inWorld.getLocation();
			
			if (below.getType() == Material.AIR && pLoc.getBlockX() == loc.getBlockX() && pLoc.getBlockY() < loc.getBlockY() && loc.getBlockZ() == pLoc.getBlockZ()) {
				PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-§ §a" + inWorld.getName() + " §8[§7" + NameUtils.capitalizeString(block.getType().name(), true) + "§8]", "uhc.staff");
				isPlayerBelow = true;
				break;
			}
		}
		
		return below.getType() == Material.AIR && isFallingBlock && isPlayerBelow;
	}

	/**
	 * Check if the given iPvPer is spleefing someone.
	 * 
	 * @param iPvPer The iPvPer.
	 * @param event The block break event that gets the block they are breaking.
	 * @return True if he is and alerts the staff, false otherwise.
	 */
	private boolean isSpleef(Player iPvPer, BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Location loc = block.getLocation();

		final Team playerTeam = manager.getTeam(iPvPer);
		boolean isPlayerAbove = false;
		
		for (Player inWorld : iPvPer.getWorld().getPlayers()) {
			if (inWorld == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(inWorld)) {
				continue;
			}
			
			Team nearTeam = manager.getTeam(inWorld);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}
			
			Location pLoc = inWorld.getLocation();
			
			if (pLoc.getBlockX() == loc.getBlockX() && pLoc.getBlockY() == loc.clone().add(0, 1, 0).getBlockY() && loc.getBlockZ() == pLoc.getBlockZ()) {
				PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-§ §a" + inWorld.getName() + " §8[§7Spleef§8]", "uhc.staff");
				isPlayerAbove = true;
				break;
			}
		}
		
		return isPlayerAbove;
	}

	/**
	 * Check if any players are near the given ipvper.
	 * 
	 * @param iPvPer The iPvPer used for the name.
	 * @param loc The location to start the nearby check from.
	 * @return True if theres any and alerts the staff, false otherwise.
	 */
	private boolean isAnyoneNearby(Player iPvPer, Location loc) {
		final Team playerTeam = manager.getTeam(iPvPer);
    	
    	for (Entity near : PlayerUtils.getNearby(loc, 10)) {
			if (!(near instanceof Player)) {
				continue;
			}
			
			final Player nearby = (Player) near;
			
			if (nearby == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(nearby)) {
				continue;
			}
			
			final Team nearTeam = manager.getTeam(nearby);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}

			PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-§ §a" + nearby.getName() + " §8[§7" + NameUtils.capitalizeString(iPvPer.getItemInHand().getType().name(), true) + "§8]", "uhc.staff");
			return true;
		}
    	
    	return false;
	}
}