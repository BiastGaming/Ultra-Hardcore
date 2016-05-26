package com.leontg77.ultrahardcore.minigames.listeners;

import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;

/**
 * King of the ladder listener.
 * 
 * @author D4mnX
 */
public class KingOfTheLadderListener implements Listener {
    public static final String PREFIX = "§6KOTL §8» §7";

    private final Main plugin;
    
    public KingOfTheLadderListener(Main plugin) {
        this.plugin = plugin;
    }
    
    protected static final double KOTL_CENTER_X = 11.5;
    protected static final double KOTL_CENTER_Z = -14.5;
    
    protected static final Predicate<Entity> IS_IN_KOTL = player -> {
        if (!player.getWorld().getName().equals("lobby")) {
            return false;
        }
        
        Location loc = player.getLocation().clone();
        loc.setY(0);
        
        double distance = loc.distance(new Location(loc.getWorld(), KOTL_CENTER_X, 0, KOTL_CENTER_Z));
        return distance < 3.5d;
    };

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    protected void on(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        
        if (!(damaged instanceof Player)) {
            return;
        }

        if (!IS_IN_KOTL.test(damaged)) {
            return;
        }

        Entity damager = event.getDamager();
        
        if (!(damager instanceof Player)) {
            return;
        }

        if (!IS_IN_KOTL.test(damager)) {
            return;
        }

        event.setCancelled(false);
        event.setDamage(0);
    }

    private String currentKing = "";
    
    @EventHandler
    protected void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Player player = event.getPlayer();
        
        if (!IS_IN_KOTL.test(player)) {
            return;
        }
        
        if (currentKing.equals(player.getName())) {
            return;
        }
        
        currentKing = player.getName();
        
        User user = plugin.getUser(player);
        user.increaseStat(Stat.TIMES_KOTL);

        String message = PREFIX + "§a" + player.getName() + " §7is now the King of the ladder.";
        Bukkit.getOnlinePlayers().stream()
                .filter(IS_IN_KOTL)
                .forEach(kotlPlayer -> kotlPlayer.sendMessage(message));
    }
}