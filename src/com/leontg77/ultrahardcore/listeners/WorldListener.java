package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.pregenner.events.WorldBorderFillFinishedEvent;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.ChunkModifiableEvent;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * World listener class.
 * <p>
 * Contains all eventhandlers for world releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {
    private final Main plugin;
    private final Arena arena;
    private final Game game;

    /**
     * World listener class constructor.
     *
     * @param plugin The main class.
     * @param game The game class.
     * @param arena The arena class.
     */
    public WorldListener(Main plugin, Game game, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.game = game;
    }

    @EventHandler
    public void on(ChunkUnloadEvent event) {
        if (!game.isState(State.SCATTER)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(ChunkPopulateEvent event) {
        World world = event.getWorld();
        Chunk chunk = event.getChunk();

        String worldName = world.getName();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    return; // World was unloaded
                }

                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                Bukkit.getPluginManager().callEvent(new ChunkModifiableEvent(chunk));
            }
        }.runTaskLater(plugin, 200L);
    }

    @EventHandler
    public void on(WorldBorderFillFinishedEvent event) {
        World world = event.getWorld();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " clear");

        if (arena.isResetting) {
            PlayerUtils.broadcast(Arena.PREFIX + "Arena reset complete.");

            if (arena.wasEnabled) {
                arena.enable();
                PlayerUtils.broadcast(Arena.PREFIX + "The arena has been enabled.");
                PlayerUtils.broadcast(Arena.PREFIX + "You can use §a/a §7to join it.");
            }

            arena.wasEnabled = false;
            arena.isResetting = false;
            return;
        }

        PlayerUtils.broadcast(Main.PREFIX + "Pregen of world '§a" + world.getName() + "§7' finished.");
    }
}