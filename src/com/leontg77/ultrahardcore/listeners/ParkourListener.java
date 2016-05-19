package com.leontg77.ultrahardcore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Parkour listener class.
 * <p> 
 * Contains events to manage the parkour.
 * 
 * @author LeonTG77
 */
public class ParkourListener implements Listener {
    private final Main plugin;

    private final SpecManager spec;
    private final Parkour parkour;

    public ParkourListener(Main plugin, Parkour parkour, SpecManager spec) {
        this.plugin = plugin;

        this.parkour = parkour;
        this.spec = spec;
    }

    @EventHandler
    public void on(final PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "Parkour failed! §cYou cannot change gamemode while in the parkour!");

        player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
        parkour.removePlayer(player);
    }

    @EventHandler
    public void on(final PlayerToggleFlightEvent event) {
        final Player player = event.getPlayer();

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "Parkour failed! §cYou cannot fly while in the parkour!");

        player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
        parkour.removePlayer(player);
    }

    @EventHandler
    public void on(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (event.getCause() == TeleportCause.UNKNOWN) {
            return;
        }

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "Parkour failed! §cYou cannot teleport while in the parkour!");
        parkour.removePlayer(player);
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (!to.getWorld().getName().equals("lobby")) {
            return;
        }

        final Player player = event.getPlayer();

        if (spec.isSpectating(player)) {
            return;
        }

        if (to.getBlockY() < 20 && parkour.isParkouring(player)) {
            player.teleport(parkour.getLocation(parkour.getCheckpoint(player)), TeleportCause.UNKNOWN);
            return;
        }

        if (LocationUtils.areEqual(from, to)) {
            return;
        }

        final State state = State.getState();

        // parkour should never be used incase of this.
        if (state == State.SCATTER || state == State.INGAME || state == State.ENDING) {
            return;
        }

        // start point
        if (LocationUtils.areEqual(to, parkour.getLocation(0))) {
            if (parkour.isParkouring(player)) {
                player.sendMessage(Parkour.PREFIX + "The timer has been reset.");
                player.playSound(player.getLocation(), "random.pop", 1, 1);

                parkour.resetTime(player);
                return;
            }

            player.sendMessage(Parkour.PREFIX + "You started the parkour!");
            player.playSound(player.getLocation(), "random.pop", 1, 1);

            parkour.addPlayer(player);
        }

        if (!parkour.isParkouring(player)) {
            return;
        }

        final String date = DateUtils.formatDateDiff(parkour.getStartTime(player).getTime());

        // checkpoint 1
        if (LocationUtils.areEqual(to, parkour.getLocation(1))) {
            if (parkour.getCheckpoint(player) == 1) {
                return;
            }

            player.sendMessage(Parkour.PREFIX + "You reached checkpoint §61§7.");
            player.sendMessage(Parkour.PREFIX + "You have used: §a" + date);

            player.playSound(player.getLocation(), "random.pop", 1, 1);
            parkour.setCheckpoint(player, 1);
        }

        // checkpoint 2
        if (LocationUtils.areEqual(to, parkour.getLocation(2))) {
            if (parkour.getCheckpoint(player) == 2) {
                return;
            }

            player.sendMessage(Parkour.PREFIX + "You reached checkpoint §62§7.");
            player.sendMessage(Parkour.PREFIX + "You have used: §a" + date);

            player.playSound(player.getLocation(), "random.pop", 1, 1);
            parkour.setCheckpoint(player, 2);
        }

        // checkpoint 3
        if (LocationUtils.areEqual(to, parkour.getLocation(3))) {
            if (parkour.getCheckpoint(player) == 3) {
                return;
            }

            player.sendMessage(Parkour.PREFIX + "You reached checkpoint §63§7.");
            player.sendMessage(Parkour.PREFIX + "You have used: §a" + date);

            player.playSound(player.getLocation(), "random.pop", 1, 1);
            parkour.setCheckpoint(player, 3);
        }

        // end point
        if (LocationUtils.areEqual(to, parkour.getLocation(4))) {
            PlayerUtils.broadcast(Parkour.PREFIX + "§a" + player.getName() + " §7completed the parkour in " + date + "!");

            player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
            parkour.removePlayer(player);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        }
    }
}