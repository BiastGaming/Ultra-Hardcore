package com.leontg77.ultrahardcore.world.antistripmine.listener;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine;
import com.leontg77.ultrahardcore.world.antistripmine.ChunkOreRemover;
import com.leontg77.ultrahardcore.world.antistripmine.WorldData;

/**
 * Chunk populate listener class.
 * 
 * @author LeonTG77
 */
public class ChunkPopulateListener implements Listener {
    private final AntiStripmine antiSM;
    private final Main plugin;

    /**
     * Chunk populate listener class constructor.
     *
     * @param plugin The main class.
     * @param antiSM The anti stripmine class.
     */
    public ChunkPopulateListener(Main plugin, AntiStripmine antiSM) {
        this.antiSM = antiSM;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ChunkPopulateEvent event) {
        Chunk chunk = event.getChunk();
        World world = event.getWorld();

        WorldData data = antiSM.getWorldData(world);

        if (data == null) {
            return;
        }

        ChunkOreRemover remover = new ChunkOreRemover(antiSM, data, chunk);

        if (antiSM.wasChecked(remover)) {
            plugin.getLogger().warning("Populated " + data.getWorld().getName() + " " + remover.toString() + " again");

            antiSM.queue(remover);
        } else {
            data.logQueued(remover);
            antiSM.queue(remover);
        }
    }
}
