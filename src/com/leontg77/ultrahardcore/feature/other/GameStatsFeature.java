package com.leontg77.ultrahardcore.feature.other;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    
    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }
      
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        
        if (ironmans.contains(player.getUniqueId())) {
            ironmans.remove(player.getUniqueId());
            
            if (ironmans.size() == 1 && !isIronManTaken) {
                UUID uuidIronman = ironmans.get(0);
                OfflinePlayer ironMan = Bukkit.getOfflinePlayer(uuidIronman);
                
                PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + ironMan.getName() + " §7is the iron man.");
                
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
                }
                
                isIronManTaken = true;
                
                try {
                    User user = plugin.getUser(ironMan);
                    user.increaseStat(Stat.IRONMAN);
                } catch (Exception ignored) {}
            }
        }
        
        if (!isFirstDamageTaken) {
            PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7was the first to take damage.");
            
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
            
            isFirstDamageTaken = true;
            
            User user = plugin.getUser(player);
            user.increaseStat(Stat.FIRSTDAMAGE);
        }
    }
    
    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        if (!isFirstDeathTaken) {
            PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7was the first to die.");
            
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
            
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
            
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
            
            isFirstBloodTaken = true;
            
            User user = plugin.getUser(killer);
            user.increaseStat(Stat.FIRSTBLOOD);
        }
    }
}