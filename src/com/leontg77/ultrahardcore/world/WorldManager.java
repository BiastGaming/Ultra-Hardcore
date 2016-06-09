package com.leontg77.ultrahardcore.world;

import java.util.Map;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;
import com.leontg77.ultrahardcore.world.orelimiter.OreLimiter;

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
     * @param centerX The world X center.
     * @param centerZ The world Z center.
     */
    public void createWorld(String name, int diameter, long seed, Environment environment, WorldType type, boolean antiStripmine, OreLimiter.Type oreLimiter, boolean newStone, double centerX, double centerZ) {
        settings.getWorlds().set(name + ".name", name);
        settings.getWorlds().set(name + ".radius", diameter);
        settings.getWorlds().set(name + ".seed", seed);

        settings.getWorlds().set(name + ".environment", environment.name());
        settings.getWorlds().set(name + ".worldtype", type.name());
        settings.getWorlds().set(name + ".diameter", diameter);

        settings.getWorlds().set(name + ".antiStripmine", antiStripmine);
        settings.getWorlds().set(name + ".oreLimiter", oreLimiter.name());
        settings.getWorlds().set(name + ".newStone", newStone);
        settings.getWorlds().set(name + ".center.x", centerX);
        settings.getWorlds().set(name + ".center.z", centerZ);

        settings.saveWorlds();

        World world;
        
        try {
            world = loadWorld(name);
        } catch (CommandException e) {
            throw new AssertionError(e);
        }

        world.setGameRuleValue("doDaylightCycle", "false");
        world.setSpawnFlags(false, true);

        int y = LocationUtils.highestTeleportableYAtLocation(new Location(world, centerX, 0, centerX)) + 2;
        world.setSpawnLocation((int) centerX, y, (int) centerZ);

        WorldBorder border = world.getWorldBorder();
        border.setSize(diameter);
        border.setWarningDistance(0);
        border.setWarningTime(60);
        border.setDamageAmount(0.1);
        border.setDamageBuffer(0);
        border.setCenter(centerX, centerZ);
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
     * @throws CommandException If the world name isn't a existing world.
     */
    public World loadWorld(String name) throws CommandException {
        Set<String> worlds = settings.getWorlds().getKeys(false);

        if (!worlds.contains(name)) {
            throw new CommandException("The world '" + name + "' does not exist.");
        }

        Environment environment = Environment.valueOf(settings.getWorlds().getString(name + ".environment", Environment.NORMAL.name()));
        WorldType type = WorldType.valueOf(settings.getWorlds().getString(name + ".worldtype", WorldType.NORMAL.name()));
        OreLimiter.Type oreLimiter = OreLimiter.Type.valueOf(settings.getWorlds().getString(name + ".oreLimiter", OreLimiter.Type.NONE.name()));
        long seed = settings.getWorlds().getLong(name + ".seed", 2347862349786234l);
        boolean newStone = settings.getWorlds().getBoolean(name + ".newStone", true);

        WorldCreator creator = new WorldCreator(name);
        creator.generateStructures(true);
        creator.environment(environment);
        creator.type(type);
        creator.seed(seed);
        creator.generatorSettings(getGeneratorSettings(type, newStone, oreLimiter));

        World world = creator.createWorld();
        world.setDifficulty(Difficulty.HARD);

        world.save();
        return world;
    }

    protected String getGeneratorSettings(WorldType worldtype, boolean newStone, OreLimiter.Type oreLimiter) {
        if (worldtype == WorldType.FLAT) {
            return "3;minecraft:bedrock,2*minecraft:dirt,minecraft:grass;1;village(size=65535 distance=9";
        }

        Map<String, Object> generatorSettings = Maps.newHashMap();
        generatorSettings.put("useMonuments", false);

        if (!newStone) {
            for (String stoneName : ImmutableList.of("granite", "diorite", "andesite")) {
                generatorSettings.put(stoneName + "Size", 1);
                generatorSettings.put(stoneName + "Count", 0);
                generatorSettings.put(stoneName + "MinHeight", 0);
                generatorSettings.put(stoneName + "MaxHeight", 0);
            }
        }

        if (oreLimiter == OreLimiter.Type.SMALLER_VEINS) {
            generatorSettings.put("goldSize", 6);
            generatorSettings.put("redstoneSize", 6);
            generatorSettings.put("diamondSize", 6);
        }

        return new Gson().toJson(generatorSettings);
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