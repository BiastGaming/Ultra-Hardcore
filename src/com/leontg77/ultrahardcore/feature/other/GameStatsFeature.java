package com.leontg77.ultrahardcore.feature.other;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Game stats feature class.
 * 
 * @author LeonTG77
 */
public class GameStatsFeature extends Feature implements Listener {
    private final Timer timer;

    public GameStatsFeature(Timer timer) {
        super("Game Stats", "Adds first damage, first death, first blood and iron man stats.");
        
        this.timer = timer;
    }

    private final List<UUID> ironmans = Lists.newArrayList();
    
    public boolean isFirstDamageTaken = false;
    public boolean isFirstBloodTaken = false;
    public boolean isFirstDeathTaken = false;
    public boolean isIronManTaken = false;
    
    @EventHandler
    public void on(GameStartEvent event) {
        isFirstDamageTaken = false;
        isFirstBloodTaken = false;
        isFirstDeathTaken = false;
        isIronManTaken = false;
        
        for (OfflinePlayer offline : game.getOfflinePlayers()) {
            ironmans.add(offline.getUniqueId());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }
      
        if (event.getFinalDamage() == 0) {
            return;
        }
      
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        handleIronman(player);
        
        if (!isFirstDamageTaken) {
            PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7was the first to take damage.");
            Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1));
            
            isFirstDamageTaken = true;
            
            User user = plugin.getUser(player);
            user.increaseStat(Stat.FIRSTDAMAGE);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        handleIronman(player);
        
        if (!isFirstDeathTaken) {
            PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7was the first to die.");
            Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1));
            
            isFirstDeathTaken = true;
            
            User user = plugin.getUser(player);
            user.increaseStat(Stat.FIRSTDEATH);
        }
        
        Player killer = player.getKiller();
        
        if (killer == null) {
            return;
        }
        
        if (!isFirstBloodTaken) {
            PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + killer.getName() + " §7got the first kill.");
            Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1));
            
            isFirstBloodTaken = true;
            
            User user = plugin.getUser(killer);
            user.increaseStat(Stat.FIRSTBLOOD);
        }
    }
    
    private void handleIronman(Player player) {
        if (isIronManTaken || !ironmans.contains(player.getUniqueId())) return;

        // Remove from the iron man list
        ironmans.remove(player.getUniqueId());

        // Grab a list of online iron men
        List<Player> onlineIronMen = game
                .getPlayers()
                .stream()
                .filter(it -> ironmans.contains(it.getUniqueId()))
                .collect(Collectors.toList());

        // If it's not the final player don't do anything else
        if (onlineIronMen.size() != 1) return;

        Player ironMan = onlineIronMen.get(0);

        if (ironMan == null) return;

        PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + ironMan.getName() + " §7is the iron man.");
        Bukkit.getOnlinePlayers().forEach(online -> online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1));

        isIronManTaken = true;

        try {
            User user = plugin.getUser(ironMan);
            user.increaseStat(Stat.IRONMAN);
        } catch (Exception ignored) {}
    }
}