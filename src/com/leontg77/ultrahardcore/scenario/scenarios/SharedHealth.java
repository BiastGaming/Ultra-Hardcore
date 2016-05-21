package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Shared Health scenario class.
 * 
 * @author LeonTG77 (Used to be dans1988 but I completly rewrote it)
 */
public class SharedHealth extends Scenario implements Listener {
    private final Main plugin;

    private final TeamManager teams;
    private final Game game;

    /**
     * Shared Health class constructor.
     *
     * @param plugin The main class.
     * @param game The game class.
     * @param teams The team manager class.
     */
    public SharedHealth(Main plugin, Game game, TeamManager teams) {
        super("SharedHealth", "All teammates share their health, does not apply for lava, fire or poison damage");

        this.plugin = plugin;
        
        this.teams = teams;
        this.game = game;
    }

    private final Map<UUID, Double> loggedOut = new HashMap<UUID, Double>();
    private final List<UUID> sharedDamage = new ArrayList<UUID>();

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (!loggedOut.containsKey(player.getUniqueId())) {
            return;
        }
        
        double damage = loggedOut.get(player.getUniqueId());

        if (damage == 0.0) {
            return;
        }
        
        double newHealth = ((player.getHealth() + damage) > 0.0 ? player.getHealth() + damage : 0.0);
        
        if (newHealth > player.getMaxHealth()) {
            newHealth = player.getMaxHealth();
        }

        loggedOut.remove(player.getUniqueId());
        player.setHealth(newHealth);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        Team team = teams.getTeam(player);

        if (team == null) {
            return;
        }

        teams.leaveTeam(player, false);

        if (sharedDamage.contains(player.getUniqueId())) {
            event.setDeathMessage(team.getPrefix() + player.getName() + team.getSuffix() + " died of sheaing health");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityRegainHealthEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        Team team = teams.getTeam(player);

        if (team == null || team.getSize() <= 1) {
            return;
        }

        double healAmount = event.getAmount();
        double teamSize = team.getSize();
        
        double gain = healAmount / teamSize;

        for (OfflinePlayer offline : teams.getPlayers(team)) {
            Player teammate = offline.getPlayer();

            if (teammate == null) {
                if (loggedOut.containsKey(offline.getUniqueId())) {
                    loggedOut.put(offline.getUniqueId(), loggedOut.get(offline.getUniqueId()) + gain);
                } else {
                    loggedOut.put(offline.getUniqueId(), gain);
                }
                continue;
            }

            double health = teammate.getHealth();
            double finalHP = health + gain;
            
            if (finalHP > teammate.getMaxHealth()) {
                finalHP = teammate.getMaxHealth();
            }
            
            teammate.setHealth(finalHP);
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(final EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        sharedDamage.remove(player.getUniqueId());

        if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.POISON) {
            return;
        }

        Team team = teams.getTeam(player);

        if (team == null || team.getSize() <= 1) {
            return;
        }

        double damage = event.getFinalDamage();
        double teamSize = team.getSize();
        
        double toDamage = damage / teamSize;

        for (OfflinePlayer offline : teams.getPlayers(team)) {
            Player teammate = offline.getPlayer();
            
            if (teammate == null) {
                if (loggedOut.containsKey(offline.getUniqueId())) {
                    loggedOut.put(offline.getUniqueId(), loggedOut.get(offline.getUniqueId()) - toDamage);
                } else {
                    loggedOut.put(offline.getUniqueId(), toDamage);
                }

                sharedDamage.add(offline.getUniqueId());
                continue;
            }
            
            double currentHealth = teammate.getHealth();
            double finalHealth = currentHealth - toDamage;

            if (finalHealth < 0.0D) {
                finalHealth = 0.0D;
            }

            if (teammate == player) {
                continue;
            }
            
            sharedDamage.add(teammate.getUniqueId());
            
            teammate.damage(0);
            teammate.setHealth(finalHealth);
        }

        double health = player.getHealth();
        double finalHP = health - toDamage;

        if (finalHP < 0.0D) {
            finalHP = 0;
        }

        if (finalHP == 0) {
            event.setDamage(event.getDamage() * 1000);
            return;
        }
        
        event.setDamage(0.0D);
        
        final Player toSet = player;
        final double healthToSet = finalHP;

        new BukkitRunnable() {
            public void run() {
                toSet.setHealth(healthToSet);
            }
        }.runTaskLater(plugin, 1);
    }
}