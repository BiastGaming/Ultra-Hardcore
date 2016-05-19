package com.leontg77.ultrahardcore.feature.world;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Hearts on tab feature.
 * 
 * @author LeonTG77
 */
public class WorldUpdaterFeature extends Feature {
    private BukkitRunnable task;

    public WorldUpdaterFeature(final Main plugin) {
        super("World Updater", "Makes sure all worlds have the correct settings.");

        task = new BukkitRunnable() {
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getName().equals("lobby")) {
                        if (world.getDifficulty() != Difficulty.PEACEFUL) {
                            world.setDifficulty(Difficulty.PEACEFUL);
                        }

                        if (world.getTime() != 18000) {
                            world.setTime(18000);
                        }

                        if (world.getPVP()) {
                            world.setPVP(false);
                        }
                        continue;
                    }

                    if (world.getName().equals("arena")) {
                        if (world.getDifficulty() != Difficulty.HARD) {
                            world.setDifficulty(Difficulty.HARD);
                        }

                        if (world.getTime() != 6000) {
                            world.setTime(6000);
                        }

                        if (!world.getPVP()) {
                            world.setPVP(true);
                        }
                        continue;
                    }

                    if (world.getDifficulty() != Difficulty.HARD) {
                        world.setDifficulty(Difficulty.HARD);
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 20, 20);
    }

}