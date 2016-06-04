package com.leontg77.ultrahardcore.feature.pvp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PlayerLeaveEvent;
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
    private final Main plugin;

    private final TeamManager teams;
    private final SpecManager spec;

    public CombatLogFeature(Main plugin, TeamManager teams, SpecManager spec) {
        super("Combat Log", "Kills people that log out in combat.");

        this.plugin = plugin;

        this.teams = teams;
        this.spec = spec;
    }

    public final Map<UUID, BukkitRunnable> combatTask = new HashMap<UUID, BukkitRunnable>();
    public final Map<UUID, Integer> combat = new HashMap<UUID, Integer>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        if (game.isPrivateGame() || game.isRecordedRound()) {
            return;
        }
        
        Player player = event.getEntity();

        if (!combat.containsKey(player.getUniqueId())) {
            return;
        }

        combatTask.get(player.getUniqueId()).cancel();
        combatTask.remove(player.getUniqueId());
        combat.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerLeaveEvent event) {
        if (game.isPrivateGame() || game.isRecordedRound()) {
            return;
        }
        
        Player player = event.getPlayer();

        if (game.isState(State.INGAME) && event.getLogoutReason() != LogoutReason.LEFT) {
            return;
        }

        if (!combat.containsKey(player.getUniqueId())) {
            return;
        }

        if (spec.isSpectating(player)) {
            return;
        }

        if (player.getWorld().getName().equals("lobby")) {
            return;
        }

        combatTask.get(player.getUniqueId()).cancel();
        combatTask.remove(player.getUniqueId());
        combat.remove(player.getUniqueId());

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

        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        if (!(damager instanceof Projectile) && !(damager instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        if (spec.isSpectating(player)) {
            return;
        }

        Team pteam = teams.getTeam(player);

        if (damager instanceof Player) {
            Player killer = (Player) damager;

            // shouldn't work if killer is speccing.
            if (spec.isSpectating(killer)) {
                return;
            }

            Team team = teams.getTeam(killer);

            if (pteam != null && pteam.equals(team)) {
                return;
            }

            handle(killer.getUniqueId());
        }

        if (damager instanceof Projectile) {
            Projectile proj = (Projectile) damager;
            ProjectileSource source = proj.getShooter();

            if (!(source instanceof Player)) {
                return;
            }

            Player killer = (Player) source;

            Team team = teams.getTeam(killer);

            if (pteam != null && pteam.equals(team)) {
                return;
            }

            // shouldn't work if killer is speccing.
            if (spec.isSpectating(killer)) {
                return;
            }

            handle(killer.getUniqueId());
        }

        handle(player.getUniqueId());
    }

    /**
     * Handle combat log for the given UUID.
     *
     * @param uuid The UUID.
     */
    private void handle(UUID uuid) {
        if (game.isPrivateGame() || game.isRecordedRound()) {
            return;
        }
        
        combat.put(uuid, 15);

        if (!combatTask.containsKey(uuid)) {
            combatTask.put(uuid, new BukkitRunnable() {
                public void run() {
                    combat.put(uuid, combat.get(uuid) - 1);

                    if (combat.get(uuid) == 0) {
                        combatTask.remove(uuid);
                        combat.remove(uuid);

                        cancel();
                    }
                }
            });

            combatTask.get(uuid).runTaskTimer(plugin, 20L, 20L);
        }
    }
}