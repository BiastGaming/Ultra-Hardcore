package com.leontg77.ultrahardcore.feature.world;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Hearts on tab feature.
 * 
 * @author LeonTG77
 */
public class WorldUpdaterFeature extends Feature {
    private final Timer timer;

    public WorldUpdaterFeature(Timer timer) {
        super("World Updater", "Makes sure all worlds have the correct settings.");
        
        this.timer = timer;
    }
    
    private BukkitRunnable task;

    public void startTask() {
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
                        continue;
                    }

                    if (world.getName().equals("arena")) {
                        if (world.getDifficulty() != Difficulty.HARD) {
                            world.setDifficulty(Difficulty.HARD);
                        }

                        if (world.getTime() != 6000) {
                            world.setTime(6000);
                        }
                        continue;
                    }

                    if (world.getDifficulty() != Difficulty.HARD) {
                        world.setDifficulty(Difficulty.HARD);
                    }

                    if (game.isState(State.INGAME) && timer.getTimeSinceStart() >= 2 && game.getWorlds().contains(world)) {
                        world.setSpawnFlags(true, true);
                    } else {
                        world.setSpawnFlags(false, true);
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 20, 20);
    }
}