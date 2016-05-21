package com.leontg77.ultrahardcore.world;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * World management class.
 * 
 * @author LeonTG77
 */
public class WorldManager {
    private final Settings settings;

    /**
     * WorldManager class constructor.
     *
     * @param settings The settings class.
     */
    public WorldManager(Settings settings) {
        this.settings = settings;
    }

    /**
     * Load all the saved worlds.
     */
    public void loadWorlds() {
        final Set<String> worlds = settings.getWorlds().getKeys(false);

        if (worlds == null) {
            return;
        }

        for (String world : worlds) {
            try {
                loadWorld(world);
            } catch (Exception ex) {
                Bukkit.getLogger().severe(ex.getMessage());
            }
        }
    }

    /**
     * Create a world with the given settings.
     *
     * @param name The world name.
     * @param diameter The world diameter size.
     * @param seed The world seed.
     * @param environment The world environment.
     * @param type The world type.
     * @param antiStripmine Wether antistripmine should be enabled.
     * @param oreLimiter Wether ore limiter should be enabled.
     * @param newStone Wether the world should have the new 1.8 stone.
     * @param x The world X center.
     * @param z The world Z center.
     */
    public void createWorld(String name, int diameter, long seed, Environment environment, WorldType type, boolean antiStripmine, boolean oreLimiter, boolean newStone, double x, double z) {
        settings.getWorlds().set(name + ".name", name);
        settings.getWorlds().set(name + ".radius", diameter);
        settings.getWorlds().set(name + ".seed", seed);

        settings.getWorlds().set(name + ".environment", environment.name());
        settings.getWorlds().set(name + ".worldtype", type.name());
        settings.getWorlds().set(name + ".diameter", diameter);

        settings.getWorlds().set(name + ".antiStripmine", antiStripmine);
        settings.getWorlds().set(name + ".oreLimiter", oreLimiter);
        settings.getWorlds().set(name + ".newStone", newStone);
        settings.getWorlds().set(name + ".center.x", x);
        settings.getWorlds().set(name + ".center.z", z);

        settings.saveWorlds();

        WorldCreator creator = new WorldCreator(name);
        creator.generateStructures(true);
        creator.environment(environment);
        creator.type(type);
        creator.seed(seed);

        if (type == WorldType.FLAT) {
            creator.generatorSettings("3;minecraft:bedrock,2*minecraft:dirt,minecraft:grass;1;village(size=65535 distance=9)");
        } else {
            if (newStone) {
                creator.generatorSettings("{\"useMonuments\":false}");
            } else {
                creator.generatorSettings("{\"useMonuments\":false,\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0}");
            }
        }

        World world = creator.createWorld();
        world.setDifficulty(Difficulty.HARD);

        int y = LocationUtils.highestTeleportableYAtLocation(new Location(world, x, 0, z)) + 2;
        world.setSpawnLocation((int) x, y, (int) z);

        world.setGameRuleValue("doDaylightCycle", "false");

        WorldBorder border = world.getWorldBorder();

        border.setSize(diameter);
        border.setWarningDistance(0);
        border.setWarningTime(60);
        border.setDamageAmount(0.1);
        border.setDamageBuffer(0);
        border.setCenter(x, z);

        world.setSpawnFlags(false, true);

        world.save();
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

        settings.getWorlds().set(world.getName(), null);
        settings.saveWorlds();

        return FileUtils.deleteFile(world.getWorldFolder());
    }

    /**
     * Loads the world with the given world name.
     * 
     * @param name The name of the world.
     * @param game The game class.
     * @throws CommandException If the world name isn't a existing world.
     */
    public void loadWorld(String name) throws CommandException {
        Set<String> worlds = settings.getWorlds().getKeys(false);

        if (!worlds.contains(name)) {
            throw new CommandException("The world '" + name + "' does not exist.");
        }

        WorldCreator creator = new WorldCreator(name);
        creator.generateStructures(true);

        long seed = settings.getWorlds().getLong(name + ".seed", 2347862349786234l);
        Environment environment = Environment.valueOf(settings.getWorlds().getString(name + ".environment", Environment.NORMAL.name()));
        WorldType worldtype = WorldType.valueOf(settings.getWorlds().getString(name + ".worldtype", WorldType.NORMAL.name()));

        creator.environment(environment);
        creator.type(worldtype);
        creator.seed(seed);

        if (worldtype == WorldType.FLAT) {
            creator.generatorSettings("3;minecraft:bedrock,2*minecraft:dirt,minecraft:grass;1;village(size=65535 distance=9)");
        } else {
            if (settings.getWorlds().getBoolean(name + ".newStone", true)) {
                creator.generatorSettings("{\"useMonuments\":false}");
            } else {
                creator.generatorSettings("{\"useMonuments\":false,\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0}");
            }
        }

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
        for (Player player : world.getPlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        return Bukkit.unloadWorld(world, true);
    }
}