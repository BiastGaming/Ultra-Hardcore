package com.leontg77.ultrahardcore.world;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * World management class.
 * 
 * @author LeonTG77
 */
public class WorldManager {
	private Settings settings = Settings.getInstance();
	private static WorldManager instance = new WorldManager();
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static WorldManager getInstance() {
		return instance;
	}
	
	/**
	 * Load all the saved worlds.
	 */
	public void loadWorlds() {
		try {
			Set<String> worlds = settings.getWorlds().getConfigurationSection("worlds").getKeys(false);
			
			for (String world : worlds) {
				String name = world;
				
				try {
					loadWorld(name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			settings.getWorlds().set("worlds.arena.name", "arena");
			settings.getWorlds().set("worlds.arena.radius", 200);
			settings.getWorlds().set("worlds.arena.seed", 123l);
			settings.getWorlds().set("worlds.arena.environment", "NORMAL");
			settings.getWorlds().set("worlds.arena.worldtype", "NORMAL");
			settings.saveWorlds();
		}
	}
	
	/**
	 * Create a world with the given settings.
	 * 
	 * @param name The name of the world.
	 * @param radius The border size.
	 * @param seed The seed of the world.
	 * @param environment The world's environment.
	 * @param type The world type.
	 */
	public void createWorld(String name, int radius, long seed, Environment environment, WorldType type) {
		WorldCreator creator = new WorldCreator(name);
		creator.generateStructures(true);
		creator.environment(environment);
		creator.type(type);
		creator.seed(seed);
		
		if (Game.getInstance().newStone()) {
			creator.generatorSettings("{\"useMonuments\":false}");
		} else {
			creator.generatorSettings("{\"useMonuments\":false,\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0}");
		}
		
		World world = creator.createWorld();
		world.setDifficulty(Difficulty.HARD);
		world.setSpawnLocation(0, 0, 0);
		int y = LocationUtils.highestTeleportableYAtLocation(world.getSpawnLocation()) + 2;
		world.setSpawnLocation(0, y, 0);

		WorldBorder border = world.getWorldBorder();
		
		border.setSize(radius * 2);
		border.setCenter(0.0, 0.0);
		border.setWarningDistance(0);
		border.setWarningTime(60);
		border.setDamageAmount(0.1);
		border.setDamageBuffer(0);
		
		world.save();

		settings.getWorlds().set("worlds." + world.getName() + ".name", name);
		settings.getWorlds().set("worlds." + world.getName() + ".radius", radius);
		settings.getWorlds().set("worlds." + world.getName() + ".seed", seed);
		settings.getWorlds().set("worlds." + world.getName() + ".environment", environment.name());
		settings.getWorlds().set("worlds." + world.getName() + ".worldtype", type.name());
		settings.saveWorlds();
	}
	
	/**
	 * Delete the given world.
	 * 
	 * @param world The world deleting.
	 * @return True if it was deleted, false otherwise.
	 */
	public boolean deleteWorld(World world) {
		for (Player player : world.getPlayers()) {
			player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		
		Bukkit.unloadWorld(world, true);
		
		settings.getWorlds().set("worlds." + world.getName(), null);
		settings.saveWorlds();
		
		return FileUtils.deleteFile(world.getWorldFolder());
	}
	
	/**
	 * Loads a world with the given settings.
	 * 
	 * @param name The name of the world.
	 * @param radius The border size.
	 * @param seed The seed of the world.
	 * @param environment The world's environment.
	 * @param type The world type.
	 * @throws CommandException if world doesn't exist.
	 */
	public void loadWorld(String name) throws CommandException {
		Set<String> worlds = settings.getWorlds().getConfigurationSection("worlds").getKeys(false);
		
		if (!worlds.contains(name)) {
			throw new CommandException("The world '" + name + "' does not exist.");
		}
		
		WorldCreator creator = new WorldCreator(name);
		creator.generateStructures(true);
		
		if (!Game.getInstance().newStone()) {
			creator.generatorSettings("{\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0}");
		}
		
		long seed = settings.getWorlds().getLong("worlds." + name + ".seed", 2347862349786234l);
		Environment environment = Environment.valueOf(settings.getWorlds().getString("worlds." + name + ".environment", Environment.NORMAL.name()));
		WorldType worldtype = WorldType.valueOf(settings.getWorlds().getString("worlds." + name + ".worldtype", WorldType.NORMAL.name()));
		
		creator.environment(environment);
		creator.type(worldtype);
		creator.seed(seed);
		
		World world = creator.createWorld();
		world.setDifficulty(Difficulty.HARD);
		world.save();
	}
	
	/**
	 * Unloads the given world.
	 * 
	 * @param world The world.
	 * @return True if successful, false otherwise.
	 */
	public boolean unloadWorld(World world) {
		return Bukkit.unloadWorld(world, true);
	}
}
