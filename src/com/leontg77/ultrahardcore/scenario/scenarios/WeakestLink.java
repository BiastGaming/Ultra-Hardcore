package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Ordering;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * WeakestLink scenario class.
 * 
 * @author LeonTG77
 */
public class WeakestLink extends Scenario implements Listener, CommandExecutor, TabCompleter {
    public static final String PREFIX = "§aWeakest Link §8» §f";

    protected final Paranoia para;
    
    public WeakestLink(Paranoia para) {
        super("WeakestLink", "Every 10 minutes the person with the least health will perish. If everyone is at the same health no one will die. Will be disabled at meetup");
        
        this.para = para;
    }
   
    private BukkitRunnable task;
    
    @Override
    public void onDisable() {
       if (task != null) {
           task.cancel();
       }
       
       task = null;
    }
    
    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        on(new GameStartEvent());
    }
    
    @EventHandler
    public void on(GameStartEvent event) {
        task = new KillingTask();
        task.runTaskTimer(plugin, 12000, 12000);
    }
    
    @EventHandler
    public void on(MeetupEvent event) {
        onDisable();
    }
    
    @Override
    protected void onSetup(Main plugin, Game game, Timer timer) {
        super.onSetup(plugin, game, timer);
        
        plugin.getCommand("lowest").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Weakest Link is currenly disabled.");
            return true;
        }
        
        if (para.isEnabled()) {
            sender.sendMessage(ChatColor.RED + "You can't use this in paranoia.");
            return true;
        }
        
        Player lowest = getLowestPlayer();
        
        if (lowest == null) {
            sender.sendMessage(PREFIX + "Everyone is at the same health.");
            return true;
        }
        
        sender.sendMessage(PREFIX + "The lowest player is: §a" + lowest.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
    
    /**
     * Ordering of everyones health.
     */
    private static final Ordering<Player> BY_HEALTH = new Ordering<Player>() {
        @Override
        public int compare(Player p1, Player p2) {
            return Double.compare(p1.getHealth(), p2.getHealth());
        }
    };
        
    /**
     * Get the lowest online player by their health.
     * 
     * @return The lowest player.
     */
    public Player getLowestPlayer() {
        Player lowest = BY_HEALTH.min(game.getPlayers());

        if (isAllAtSameHealth() || lowest == null) {
            return null;
        }
        
        return lowest;
    }
    
    /**
     * Check if all online players are at the same health.
     * <p>
     * Doesn't count players in gamemode 3 (or gm 1 in 1.7).
     * 
     * @return True if they are, false otherwise.
     */
    private boolean isAllAtSameHealth() {
        boolean isSameHealth = true;
        Player last = null;
        
        for (Player online : game.getPlayers()) {
            if (last != null && last.getHealth() != online.getHealth()) {
                isSameHealth = false;
                break;
            }
            
            last = online;
        }
        
        return isSameHealth;
    }
    
    /**
     * Killing task class.
     * 
     * @author LeonTG77
     */
    public class KillingTask extends BukkitRunnable {
        
        @Override
        public void run() {
            Player toKill = getLowestPlayer();

            if (toKill == null) {
                PlayerUtils.broadcast(PREFIX + "You were lucky, there were no one to kill.");
                return;
            }

            PlayerUtils.broadcast(PREFIX + toKill.getName() + " was on the lowest health and got perished.");
            
            // the damaging is so they get the damage sound when taking the damage to die.
            toKill.damage(0);
            toKill.setHealth(0);
        }
    }
}