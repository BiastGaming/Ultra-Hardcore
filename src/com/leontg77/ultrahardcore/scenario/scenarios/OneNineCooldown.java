package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;

/**
 * OneNineCooldown scenario class.
 * 
 * @author D4nnX
 */
public class OneNineCooldown extends Scenario implements Listener {
    protected final Map<UUID, Long> lastHitTimes = Maps.newHashMap();

    private BukkitRunnable task;
    private final Main plugin;

    /**
     * OneNineCooldown class constructor.
     * 
     * @param plugin The main class.
     */
    public OneNineCooldown(Main plugin) {
        super("OneNineCooldown", "Simulation of 1.9 cooldown. If you hit before your bar times out, you do less damage");
        
        this.plugin = plugin;
    }


    @Override
    public void onEnable() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(OneNineCooldown.this::displayTitle);
            }
        };
        task.runTaskTimer(plugin, 1L, 1L);
    }

    @Override
    public void onDisable() {
        lastHitTimes.clear();
        
        if (task != null) {
        	task.cancel();
        }
    }

    protected void displayTitle(Player player) {
        Optional<Long> millisSinceLastHit = getMillisSinceLastHit(player);
        
        if (!millisSinceLastHit.isPresent()) {
            return;
        }

        int grayBars = millisSinceLastHit.get().intValue() / 50;
        int redBars = 12 - grayBars;
        String subtitle = "Cooldown: " + ChatColor.RED + StringUtils.repeat("\u275A", redBars) + ChatColor.GRAY + StringUtils.repeat("\u275A", grayBars);
        PacketUtils.sendTitle(player, "", subtitle, 0, 10, 0);
    }

    protected void playerClicked(Player player) {
        lastHitTimes.put(player.getUniqueId(), System.currentTimeMillis());
        displayTitle(player);
    }

    protected Optional<Long> getMillisSinceLastHit(Player player) {
        UUID uuid = player.getUniqueId();
        Long lastHitTime = lastHitTimes.get(uuid);
        
        if (lastHitTime == null) {
            return Optional.empty();
        }

        long difference = System.currentTimeMillis() - lastHitTime;
        
        if (difference > 600) {
            lastHitTimes.remove(uuid);
            return Optional.empty();
        }

        return Optional.of(difference);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        playerClicked(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void on(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        
        if (!(damager instanceof Player)) {
            return;
        }

        Player player = (Player) damager;
        Optional<Long> millisSinceLastHit = getMillisSinceLastHit(player);
        
        if (millisSinceLastHit.isPresent()) {
            double damageMultiplier = ((double) millisSinceLastHit.get()) / 600;
            event.setDamage(event.getDamage() * damageMultiplier);
        }

        playerClicked(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void on(PlayerQuitEvent event) {
        lastHitTimes.remove(event.getPlayer().getUniqueId());
    }
}