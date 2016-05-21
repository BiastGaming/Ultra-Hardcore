package com.leontg77.ultrahardcore.feature.death;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.minigames.Arena;

/**
 * Rspawn feature class.
 * 
 * @author LeonTG77
 */
public class RespawnFeature extends Feature implements Listener {
    private final SpecManager spec;
    private final Main plugin;

    private final Arena arena;
    private final Game game;

    public RespawnFeature(Main plugin, SpecManager spec, Arena arena, Game game) {
        super("Respawn", "Messages and auto spec mode when someone respawn!");

        this.plugin = plugin;
        this.spec = spec;

        this.arena = arena;
        this.game = game;
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        event.setRespawnLocation(plugin.getSpawn());
        player.setMaxHealth(20);

        if (arena.isEnabled() || !game.isState(State.INGAME) || game.isRecordedRound()) {
            return;
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.hidePlayer(player);
        }

        player.sendMessage(Main.PREFIX + "Thanks for playing a game provided by Arctic UHC!");
        player.sendMessage(Main.PREFIX + "Follow us on twtter to know when our next games are: §a§o@ArcticUHC");
        player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");

        if (!game.isPrivateGame() && !player.hasPermission("uhc.prelist")) {
            player.sendMessage(Main.PREFIX + "You may stay as long as you want (You are vanished).");
            return;
        }

        player.sendMessage(Main.PREFIX + "Found death spectator permission...");
        player.sendMessage(Main.PREFIX + "Enabling your spectator mode in 10 seconds.");

        new BukkitRunnable() {
            public void run() {
                if (!game.isState(State.INGAME) && !game.isState(State.ENDING)) {
                    return;
                }

                if (!player.isOnline() || spec.isSpectating(player)) {
                    return;
                }

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.showPlayer(player);
                }

                spec.enableSpecmode(player);
            }
        }.runTaskLater(plugin, 200);
    }
}