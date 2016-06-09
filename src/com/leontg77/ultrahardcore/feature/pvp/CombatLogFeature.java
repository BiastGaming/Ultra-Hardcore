package com.leontg77.ultrahardcore.feature.pvp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PlayerLeaveEvent;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.listeners.QuitMessageListener.LogoutReason;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Combat tag listener class.
 * 
 * @author LeonTG77
 */
public class CombatLogFeature extends Feature implements Listener {
    private final TeamManager teams;
    private final SpecManager spec;

    public CombatLogFeature(TeamManager teams, SpecManager spec) {
        super("Combat Log", "Kills people that log out in combat.");

        this.teams = teams;
        this.spec = spec;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            combatTicksLeft.replaceAll((uuid, integer) -> integer - 1);
            
            combatTicksLeft.entrySet().stream()
                    .filter(entry -> entry.getValue() <= 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
                    .forEach(this::leftCombat);
        }, 1L, 1L);
    }
    
    private final Map<Player, Long> combatTicksLeft = new HashMap<>();

    public long getCombatTicksLeft(Player player) {
        return combatTicksLeft.getOrDefault(player, 0L);
    }

    @EventHandler
    public void on(PvPEnableEvent event) {
        combatTicksLeft.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        combatTicksLeft.remove(event.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerLeaveEvent event) {
        Player player = event.getPlayer();
        boolean wasInCombat = combatTicksLeft.remove(player) != null;

        if (!wasInCombat) {
            return;
        }

        if (game.isState(State.INGAME) && event.getLogoutReason() != LogoutReason.LEFT) {
            return;
        }

        if (spec.isSpectating(player)) {
            return;
        }

        if (player.getWorld().getName().equals("lobby")) {
            return;
        }

        if (game.isState(State.INGAME)) {
            PlayerUtils.broadcast(Main.PREFIX + "§c" + player.getName() + "§7 left while in combat.");
        }

        player.setHealth(0.0D);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        if (game.isPrivateGame() || game.isRecordedRound()) {
            return;
        }
        
        if (event.isCancelled()) {
            return;
        }

        Entity damagedEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        if (!(damagedEntity instanceof Player)) {
            return;
        }

        Player damaged = (Player) damagedEntity;
        Player damager;

        if (damagerEntity instanceof Player) {
            damager = (Player) damagerEntity;
        } else if (damagerEntity instanceof Projectile) {
            Projectile proj = (Projectile) damagerEntity;
            ProjectileSource source = proj.getShooter();

            if (!(source instanceof Player)) {
                return;
            }

            damager = (Player) source;
        } else {
            return;
        }

        if (damaged.equals(damager)) {
            return;
        }

        List<Player> gamePlayers = game.getPlayers();
        
        if (!gamePlayers.contains(damaged) || !gamePlayers.contains(damager)) {
            return;
        }

        Team damagedTeam = teams.getTeam(damaged);
        Team damagerTeam = teams.getTeam(damager);
        
        if (damagedTeam != null && damagedTeam.equals(damagerTeam)) {
            return;
        }

        enteredCombat(damaged);
        enteredCombat(damager);
    }

    protected void enteredCombat(Player player) {
        combatTicksLeft.put(player, 300L);
    }

    protected void leftCombat(Player player) {
        combatTicksLeft.remove(player);
    }
}