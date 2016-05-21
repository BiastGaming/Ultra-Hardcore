package com.leontg77.ultrahardcore.feature.death;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.minigames.Arena;

/**
 * Death Lightninig feature class.
 * 
 * @author LeonTG77
 */
public class DeathLightningFeature extends ToggleableFeature implements Listener {
    private final Main plugin;

    private final Arena arena;
    private final Game game;

    /**
     * Death Lightninig feature class contructor.
     *
     * @param arena The arena class.
     * @param game The game class.
     */
    public DeathLightningFeature(Main plugin, Arena arena, Game game) {
        super("Death Lightning", "A fake lightning that will strike when a player dies.");

        icon.setType(Material.BLAZE_ROD);
        slot = 49;

        this.plugin = plugin;

        this.arena = arena;
        this.game = game;
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) {
            return;
        }

        final Player player = event.getEntity();
        final User user = plugin.getUser(player);

        final List<World> worlds = game.getWorlds();

        // the arena has it's own way of doing deaths.
        if (arena.isEnabled() && arena.hasPlayer(player)) {
            return;
        }

        // I know this isnt a death lightning thing, but they're dead, no more wl for them.
        player.setWhitelisted(false);
        user.setDeathLoc(player.getLocation());

        if (!game.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
            return;
        }

        player.getWorld().strikeLightningEffect(player.getLocation());

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(online.getWorld())) {
                continue;
            }

            online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 1000000000, 1);
        }
    }
}