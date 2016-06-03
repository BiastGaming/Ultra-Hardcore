package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Assassins scenario class.
 * 
 * @author audicymc
 */
public class Assassins extends Scenario implements Listener, CommandExecutor {
    private static final String PREFIX = "§cAssassins §8» §7";

    public Assassins() {
        super("Assassins", "Each player has a target that they must kill. Killing anyone that is not your target or assassin will result in no items dropping. When your target dies, you get their target.");

        plugin.getCommand("target").setExecutor(this);
    }

    private final Map<UUID, UUID> assassins = new HashMap<UUID, UUID>();

    @Override
    public void onDisable() {
        assassins.clear();
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME) || timer.getPvP() > 0) {
            return;
        }

        on(new PvPEnableEvent());
    }

    @EventHandler
    public void on(PvPEnableEvent event) {
        PlayerUtils.broadcast(PREFIX + "Targets are being assigned...");

        List<OfflinePlayer> players = game.getOfflinePlayers();
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            OfflinePlayer assassin = players.get(i);
            OfflinePlayer target = players.get(i < players.size() - 1 ? i + 1 : 0);

            setTarget(assassin, target);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getEntity();

        if (!game.getWorlds().contains(player.getWorld())) {
            return;
        }

        

        if (!assassins.containsKey(player.getName())) {
            return;
        }

        OfflinePlayer assassin = getAssassin(player);
        OfflinePlayer target = getTarget(player);

        
        Player killer = player.getKiller();

        if (killer != null) {
            if (!game.getPlayers().contains(killer)) {
                return;
            }
            
            if (!killer.getUniqueId().equals(assassin.getUniqueId()) && !player.getUniqueId().equals(target.getUniqueId())) {
                event.getDrops().clear();
            }
        }

        setTarget(assassin, target);
        assassins.remove(player.getName());

        PlayerUtils.broadcast(PREFIX + "§a" + player.getName() + " §7was eliminated!");
        event.setDeathMessage(null);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer assassin = getAssassin(player);

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (!assassin.isOnline()) {
            return;
        }

        assassin.getPlayer().setCompassTarget(player.getLocation());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute /target.");
            return true;
        }

        Player player = (Player) sender;

        if (!isEnabled()) {
            player.sendMessage(PREFIX + "Assassins is currently disabled.");
            return true;
        }

        OfflinePlayer target = getTarget(player);

        if (target == null) {
            player.sendMessage(PREFIX + "You do not have a target.");
            return true;
        }

        player.sendMessage(PREFIX + "Your target is: §a" + target.getName());
        return true;
    }

    /**
     * Set the target of the given assassin to the given target.
     * 
     * @param assassin The assassin to set for.
     * @param target The target to give the assassin.
     */
    private void setTarget(OfflinePlayer assassin, OfflinePlayer target) {
        if (assassin == null || target == null) {
            return;
        }
        
        assassins.put(assassin.getUniqueId(), target.getUniqueId());
        
        if (assassin.isOnline()) {
            assassin.getPlayer().sendMessage(PREFIX + "Your new target is: §a" + target.getName());
        }
    }

    /**
     * Get the assassin who has the given target.
     * 
     * @param target The target to use.
     * @return The assassin.
     */
    private OfflinePlayer getAssassin(OfflinePlayer target) {
        if (target == null) {
            return null;
        }
        
        for (Entry<UUID, UUID> entry : assassins.entrySet()) {
            if (entry.getValue().equals(target.getUniqueId())) {
                return Bukkit.getOfflinePlayer(entry.getKey());
            }
        }

        return null;
    }

    /**
     * Get the assassin who has the given target.
     * 
     * @param target The target to use.
     * @return The assassin.
     */
    private OfflinePlayer getTarget(OfflinePlayer assassin) {
        if (assassin == null) {
            return null;
        }
        
        for (Entry<UUID, UUID> entry : assassins.entrySet()) {
            if (entry.getKey().equals(assassin.getUniqueId())) {
                return Bukkit.getOfflinePlayer(entry.getValue());
            }
        }

        return null;
    }

    /**
     * @return The map of the assassins and targets.
     */
    public Map<UUID, UUID> getAssassinsAndTargets() {
        return assassins;
    }
}