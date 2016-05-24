package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Maps;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * LWCM scenario class.
 * 
 * @author LeonTG77 & D4mnX
 */
public class LWCM extends Scenario implements Listener, CommandExecutor {
    public static final String PREFIX = "§dLWCM §8» §7";

    private final TeamManager teams;

    public LWCM(TeamManager teams) {
        super("LWCM", "This is a mix of \"Soul Brothers\" and \"LAFS\". Half of the players would be scattered in one world, and the other half would be scattered in the other world just like soul brothers. But instead of having teams, people would come as solos. The twist from LAFS is that when say for example: Player 1 is at X: 245 Y: 41 Z: -245 in World 1. When Player 2 randomly walks within 20 blocks radius of those coords in the other world, Player 1 and Player 2 would be put on a team. And just like in soul brothers, after a certain time the teams would be scattered together in World 3.");

        this.teams = teams;

        plugin.getCommand("lwcm").setExecutor(this);
    }

    private final Map<UUID, Location> lateTeleports = Maps.newHashMap();
    private final Random random = new Random();

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        Player near = null;

        for (Player online : game.getPlayers()) {
            if (online.getWorld().getName().equals(player.getWorld().getName())) {
                continue;
            }

            Location pLoc = player.getLocation().clone();
            pLoc.setWorld(online.getWorld());

            if (online.getLocation().distance(pLoc) <= 20) {
                near = online;
                break;
            }
        }

        if (near == null) {
            return;
        }

        if (teams.getTeam(player) != null) {
            return;
        }

        if (teams.getTeam(near) != null) {
            return;
        }

        Team team = teams.findAvailableTeam();

        if (team == null) {
            return;
        }

        teams.joinTeam(team, near);
        teams.joinTeam(team, player);

        PlayerUtils.broadcast(PREFIX + "§a" + player.getName() + " §7and§a " + near.getName() + " §7has found each other.");
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        lateTeleports.computeIfPresent(player.getUniqueId(), (uuid, location) -> {
            player.teleport(location);
            return null;
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Love When Coords Match is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length == 0) {
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(PREFIX + "/lwcm <world>");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a existing world.");
            return true;
        }

        List<Player> players = game.getPlayers();
        int size = players.size();

        if ((size % 2) != 0) {
            sender.sendMessage(PREFIX + "The current amount of game players (" + size + ") is not divisible by 2.");
            return true;
        }

        int splitSize = size / 2;

        for (int i = 0; i < splitSize; i++) {
            Player randomPlayer = players.get(random.nextInt(players.size()));
            players.remove(randomPlayer);

            Location oldLocation = randomPlayer.getLocation();
            Location newLocation = oldLocation.clone();
            newLocation.setWorld(world);

            UUID uuid = randomPlayer.getUniqueId();

            final int finalI = i;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Player player = Bukkit.getPlayer(uuid);

                if (player == null) {
                    throw new AssertionError("Player was offline?");
                }

                player.teleport(newLocation);

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(onlinePlayer, PREFIX + "Moved " + finalI + "/" + splitSize + " players into " + world.getName());
                }
            }, i * 20L);
        }
        return true;
    }
}