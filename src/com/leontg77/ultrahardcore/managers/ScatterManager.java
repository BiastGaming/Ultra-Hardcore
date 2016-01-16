package com.leontg77.ultrahardcore.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.utils.LocationUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Scatter manager class.
 * 
 * @author LeonTG77
 */
public class ScatterManager {
	private static final Set<Material> INVAILD_SPAWN_BLOCKS = ImmutableSet.of(Material.STATIONARY_WATER, Material.WATER, Material.STATIONARY_LAVA, Material.LAVA, Material.CACTUS);
	private static final ScatterManager INSTANCE = new ScatterManager();
	
	private boolean scatterTeams;
	private World world;
	private int radius;
	
	public static ScatterManager getInstance() {
		return INSTANCE;
	}
	
	public void setTeamScatter(boolean enable) {
		this.scatterTeams = enable;
	}
	
	public void setWorld(World world) {
		this.world = world;
		
		this.radius = ((((int) world.getWorldBorder().getSize()) / 2) - 1);
	}
	
	public void scatter(List<Player> toScatter) {
		
	}
	
	/**
	 * Get a list of available scatter locations.
	 * 
	 * @param world the world to scatter in.
	 * @param radius the maximum radius to scatter.
	 * @param count the amount of scatter locations needed.
	 * 
	 * @return A list of vaild scatter locations.
	 */
	public static List<Location> findScatterLocations(World world, int radius, int count) {
		ArrayList<Location> locs = new ArrayList<Location>();
		
		for (int i = 0; i < count; i++) {
			double min = 150;
			
			for (int j = 0; j < 4004; j++) {
				if (j == 4003) {
					PlayerUtils.broadcast(ChatColor.RED + "Could not scatter a player", "uhc.admin");
					break;
				}
				
				Random rand = new Random();
				int x = rand.nextInt(radius * 2) - radius;
				int z = rand.nextInt(radius * 2) - radius;

				Location loc = new Location(world, x + 0.5, 0, z + 0.5);

				boolean close = false;
				for (Location l : locs) {
					if (l.distanceSquared(loc) < min) {
						close = true;
					}
				}
				
				if (!close && isVaild(loc.clone())) {
					double y = LocationUtils.highestTeleportableYAtLocation(loc);
					loc.setY(y + 2);
					locs.add(loc);
					break;
				} else {
					min -= 1;
				}
			}
		}
		
		return locs;
	}

	/**
	 * Check if the given location is a vaild scatter location.
	 * 
	 * @param loc the location.
	 * @return True if its vaild, false otherwise.
	 */
	private static boolean isVaild(Location loc) {
		loc.setY(loc.getWorld().getHighestBlockYAt(loc));
		
		Material type = loc.add(0, -1, 0).getBlock().getType();
		boolean vaild = true;
		
		if (loc.getBlockY() < 60) {
			vaild = false;
		}	
		
		for (Material no : INVAILD_SPAWN_BLOCKS) {
			if (type == no) {
				vaild = false;
			}
		}
		
		return vaild;
	}
}