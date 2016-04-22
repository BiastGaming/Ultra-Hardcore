package com.leontg77.ultrahardcore.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Location utilities class.
 * <p>
 * Contains methods for checking for nearby blocks, getting the highest blocks safe or not and 
 * checking if a player is outside of the border and if so a method to tp them safely inside of it.
 * 
 * @author LeonTG77, with help from ghowden and D4mnX
 */
public class LocationUtils {
	private static BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST};
	
	/**
	 * Check if the given locations have the same x, y and z.
	 * 
	 * @param loc1 The first location.
	 * @param loc2 The second location.
	 * @return True if they are equal, false otherwise.
	 */
	public static boolean areEqual(Location loc1, Location loc2) {
		if (loc1 == null || loc2 == null) {
			return false;
		}
		
		if (!loc1.getWorld().equals(loc2.getWorld())) {
			return false;
		}
		
		return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	/**
	 * Check if the given block is nearby the given location.
	 * 
	 * @param material the type of the block
	 * @param location the location.
	 * @return <code>True</code> if blocks nearby has the checked type, <code>false</code> otherwise.
	 * 
	 * @author D4mnX
	 */
    public static boolean hasBlockNearby(Material material, Location location) {
        Block block = location.getBlock();
        
        for (BlockFace face : faces) {
            if (block.getRelative(face).getType() == material) {
                return true;
            }
        }

        return false;
    }

	/**
	 * Get the highest block at the given location.
	 * 
	 * @param loc the location.
	 * @return The highest block.
	 */
	public static Location getHighestBlock(Location loc) {
		int highest = loc.getWorld().getEnvironment() == Environment.NETHER ? 127 : 255;
		
		for (int i = highest; i >= 0; i--) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).getType() != Material.AIR) {
				return loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).getLocation();
			}
		}
		return loc;
	}
	
	/**
	 * Check if the given location is outside the border.
	 * 
	 * @param loc the location.
	 * @return True if it is, false otherwise.
	 */
	public static boolean isOutsideOfBorder(Location loc) {
        WorldBorder border = loc.getWorld().getWorldBorder();

        double size = border.getSize() / 2;
        double x = (loc.getX() - border.getCenter().getX());
        double z = (loc.getZ() - border.getCenter().getZ());
        
        return Math.abs(x) > size || Math.abs(z) > size;
    }
	
	/**
     * Finds a safe location inside the border. If the X or Z value is outside of the border they are set to be within
     * the border with the specified buffer. If no safe teleportable Y coordinate was found for the new location then
     * the Y location will be set to -1 and IS NOT SAFE FOR TELEPORTING. If the location is already inside the border
     * then it will be returned the without modification
     *
     * @param loc the location to check
     * @param buffer The amount of blocks inside the border to return.
     * @param travel If finding a location for a portal the travel agent is given, for a normal tp this is null.
     * @return a location within the border
     * 
	 * @author ghowden
     */
    public static Location findSafeLocationInsideBorder(Location loc, int buffer, TravelAgent travel) {
        WorldBorder border = loc.getWorld().getWorldBorder();
        Location centre = border.getCenter();

        Location pos = loc.subtract(centre);

        double size = border.getSize() / 2;
        double bufferSize = size - buffer;

        double x = pos.getX();
        double z = pos.getZ();
        boolean changed = false;

        if (Math.abs(x) > size) {
            pos.setX(x > 0 ? bufferSize : -bufferSize);
            changed = true;
        }

        if (Math.abs(z) > size) {
            pos.setZ(z > 0 ? bufferSize : -bufferSize);
            changed = true;
        }

        if (!changed) {
        	return loc;
        }

        pos.setY(highestTeleportableYAtLocation(pos.add(centre)));
        
        if (travel != null) {
            Location to = travel.findOrCreate(pos);
            
            if (!isOutsideOfBorder(to)) {
            	pos = to;
            }
        }
        
        return pos;
    }
	
	/**
     * Checks for the highest non-air block with 2 air blocks above it.
     *
     * @param location the location whose X,Z coordinates are used
     * @return -1 if no valid location found, otherwise coordinate with non-air Y coord with 2 air blocks above it
	 * 
	 * @author ghowden
     */
    public static int highestTeleportableYAtLocation(Location location) {
        Location startingLocation = location.clone();
        startingLocation.setY(location.getWorld().getMaxHeight());

        boolean above2WasAir = false;
        boolean aboveWasAir = false;
        Block currentBlock = startingLocation.getBlock();

        while (currentBlock.getY() >= 0) {
            if (currentBlock.getType() != Material.AIR) {
                if (above2WasAir && aboveWasAir) {
                    return currentBlock.getY();
                }

                above2WasAir = aboveWasAir;
                aboveWasAir = false;
            } else {
                above2WasAir = aboveWasAir;
                aboveWasAir = true;
            }

            currentBlock = currentBlock.getRelative(BlockFace.DOWN);
        }

        return -1;
    }
}