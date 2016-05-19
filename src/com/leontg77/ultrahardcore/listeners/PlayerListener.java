package com.leontg77.ultrahardcore.listeners;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class PlayerListener implements Listener {
    private final SpecManager spec;
    private final Main plugin;

    /**
     * Player listener class constructor.
     *
     * @param spec The spectator manager class.
     */
    public PlayerListener(Main plugin, SpecManager spec) {
        this.plugin = plugin;
        this.spec = spec;
    }

    @EventHandler
    public void on(PlayerAchievementAwardedEvent event) {
        Player player = event.getPlayer();

        if (!spec.isSpectating(player) && State.isState(State.INGAME)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        World world = player.getWorld();

        if (world.getName().equals("lobby")) {
            event.setCancelled(true);
            event.setFoodLevel(20);
            return;
        }
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (player.getAllowFlight() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        switch (event.getCause()) {
        case ENDER_PEARL:
        case END_PORTAL:
        case NETHER_PORTAL:
        case UNKNOWN:
            return;
        default:
            break;
        }

        user.setLastLoc(player.getLocation());
    }
}