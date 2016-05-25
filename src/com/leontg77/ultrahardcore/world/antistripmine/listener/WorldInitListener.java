package com.leontg77.ultrahardcore.world.antistripmine.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine;

/**
 * World init listener class.
 * 
 * @author LeonTG77
 */
public class WorldInitListener implements Listener {
    private final AntiStripmine antiSM;
    private final Settings settings;

    /**
     * World init listener class constructor.
     *
     * @param plugin The main class.
     * @param antiSM The anti stripmine class.
     */
    public WorldInitListener(Settings settings, AntiStripmine antiSM) {
        this.settings = settings;
        this.antiSM = antiSM;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(WorldInitEvent event) {
        World world = event.getWorld();
        
        if (!settings.getWorlds().getBoolean(world.getName() + ".antiStripmine", true)) {
            return;
        }

        antiSM.registerWorld(world);
    }
}
