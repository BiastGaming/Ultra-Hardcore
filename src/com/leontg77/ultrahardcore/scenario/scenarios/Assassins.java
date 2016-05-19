package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Assassins scenario class.
 * 
 * @author audicymc
 */
public class Assassins extends Scenario implements Listener, CommandExecutor {
    private final Timer timer;
    private final Game game;

    public Assassins(Timer timer, Game game) {
        super("Assassins", "Each player has a target that they must kill. Killing anyone that is not your target or assassin will result in no items dropping. When your target dies, you get their target.");

        this.timer = timer;
        this.game = game;

        Bukkit.getPluginCommand("target").setExecutor(this);
    }

    private static final String PREFIX = "§cAssassins §8» §7";

    private final Map<String, String> assassins = new HashMap<String, String>();

    @Override
    public void onDisable() {
        assassins.clear();
    }

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME) && timer.getPvP() > 0) {
            return;
        }

        on(new PvPEnableEvent());
    }

    @EventHandler
    public void on(final PvPEnableEvent event) {
        PlayerUtils.broadcast(PREFIX + "Assigning targets...");

        final List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            final Player assassin = players.get(i);
            final Player target = players.get(i < players.size() - 1 ? i + 1 : 0);

            setTarget(assassin.getName(), target.getName());
        }
    }

    @EventHandler
    public void on(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (!game.getPlayers().contains(player) || !game.getPlayers().contains(killer)) {
            return;
        }

        if (!assassins.containsKey(player.getName())) {
            return;
        }

        final String assassin = getAssassin(player.getName());
        final String target = getTarget(player.getName());

        if (killer != null && !killer.getName().equals(assassin) && !player.getName().equals(target)) {
            event.getDrops().clear();
        }

        setTarget(assassin, target);
        assassins.remove(player.getName());

        PlayerUtils.broadcast(PREFIX + "§a" + player.getName() + " §7was eliminated!");
        event.setDeathMessage(null);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final String target = getAssassin(player.getName());

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (target == null) {
            return;
        }

        final Player assassin = Bukkit.getPlayer(target);

        if (assassin == null) {
            return;
        }

        assassin.setCompassTarget(player.getLocation());
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can have assassins targets.");
            return true;
        }

        final Player player = (Player) sender;

        if (!isEnabled()) {
            player.sendMessage(PREFIX + "Assassins is not enabled.");
            return true;
        }

        final String target = getTarget(player.getName());

        if (target == null) {
            player.sendMessage(PREFIX + "Could not find your target.");
            return true;
        }

        player.sendMessage(PREFIX + "Your target: §a" + getTarget(player.getName()));
        return true;
    }

    /**
     * Set a new target for the assassin.
     *
     * @param assassin the assassin.
     * @param target the new target.
     */
    private void setTarget(final String assassin, final String target) {
        assassins.put(assassin, target);

        final Player player = Bukkit.getPlayer(assassin);

        if (player == null) {
            return;
        }

        player.sendMessage(PREFIX + "Your new target: §a" + target);
    }

    /**
     * Get the assassin that has the target.
     *
     * @param target the target.
     * @return the assassin.
     */
    private String getAssassin(String target) {
        for (Entry<String, String> entry : assassins.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(target)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Get the target for an assassin
     *
     * @param assassin the assassin.
     * @return the target of the assassin
     */
    private String getTarget(String assassin) {
        for (Entry<String, String> entry : assassins.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(assassin)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * Get the assassins map
     *
     * @return the Assassins map
     */
    public Map<String, String> getAssassinsMap() {
        return assassins;
    }
}