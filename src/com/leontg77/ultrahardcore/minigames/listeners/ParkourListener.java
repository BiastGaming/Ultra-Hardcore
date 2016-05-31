package com.leontg77.ultrahardcore.minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.minigames.Parkour;
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
    
    private final Settings settings;
    private final Game game;

    private final SpecManager spec;
    private final Parkour parkour;

    private Location skull;
    private Location sign;

    public ParkourListener(Main plugin, Game game, Settings settings, Parkour parkour, SpecManager spec) {
        this.plugin = plugin;
        
        this.settings = settings;
        this.game = game;

        this.parkour = parkour;
        this.spec = spec;

        skull = new Location(Bukkit.getWorld("lobby"), 30, 15, -13);
        sign = new Location(Bukkit.getWorld("lobby"), 30, 14, -12);
    }

    @EventHandler
    public void on(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "Parkour failed! §cYou cannot change gamemode while in the parkour!");

        player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
        parkour.removePlayer(player);
    }

    @EventHandler
    public void on(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_RED + "Parkour failed! §cYou cannot fly while in the parkour!");

        player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
        parkour.removePlayer(player);
    }

    @EventHandler
    public void on(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

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
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!parkour.isParkouring(player)) {
            return;
        }

        player.teleport(plugin.getSpawn(), TeleportCause.UNKNOWN);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (!to.getWorld().getName().equals("lobby")) {
            return;
        }

        Player player = event.getPlayer();

        if (spec.isSpectating(player)) {
            return;
        }

        if (to.getBlockY() < 0 && parkour.isParkouring(player)) {
            player.teleport(parkour.getLocation(parkour.getCheckpoint(player)), TeleportCause.UNKNOWN);
            return;
        }

        if (LocationUtils.areEqual(from, to)) {
            return;
        }

        State state = game.getState();

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

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(Parkour.PREFIX + "§cNo creative mode in the Parkour.");
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (player.isFlying() || player.getAllowFlight()) {
            player.sendMessage(Parkour.PREFIX + "§cNo fly mode in the Parkour.");
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        String date = DateUtils.formatDateDiff(parkour.getStartTime(player).getTime());

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
            
            long best = settings.getConfig().getLong("parkour.best", System.currentTimeMillis());

            if (parkour.getStartTime(player) != null) {
                long newTime = System.currentTimeMillis() - parkour.getStartTime(player).getTime();
                
                if (newTime < best) {
                    if (skull.getBlock().getState() instanceof Skull) {
                        Skull head = (Skull) skull.getBlock().getState();
                        head.setOwner(player.getName());
                    }
                    
                    if (sign.getBlock().getState() instanceof Sign) {
                        Sign text = (Sign) sign.getBlock().getState();
                        text.setLine(2, player.getName());
                        text.setLine(3, DateUtils.secondsToString(newTime / 1000));
                    }
                    
                    settings.getConfig().set("parkour.best", newTime);
                    settings.saveConfig();
                }
            }
        }
    }
}