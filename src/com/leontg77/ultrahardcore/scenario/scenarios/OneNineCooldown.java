package com.leontg77.ultrahardcore.scenario.scenarios;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OneNineCooldown scenario class.
 * 
 * @author D4nnX
 */
public class OneNineCooldown extends Scenario implements Listener {

    protected final Main plugin;
    protected final Map<Player, Cooldown> cooldowns = Maps.newHashMap();

    /**
     * OneNineCooldown class constructor.
     * 
     * @param plugin The main class.
     */
    public OneNineCooldown(Main plugin) {
        super("OneNineCooldown", "Simulation of 1.9 cooldown. If you hit before your bar times out, you do less damage");
        
        this.plugin = plugin;
    }

    protected Optional<BukkitTask> task = Optional.empty();

    @Override
    public void onEnable() {
        if (task.isPresent()) {
            return;
        }

        task = Optional.of(Bukkit.getScheduler().runTaskTimer(plugin,
                () -> cooldowns.values().forEach(Cooldown::onTick),
                1L, 1L
        ));
    }

    @Override
    public void onDisable() {
        task.ifPresent(BukkitTask::cancel);
        cooldowns.clear();
    }

    protected Cooldown getCooldown(Player player) {
        return cooldowns.computeIfAbsent(player, Cooldown::new);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void on(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> getCooldown(player).handleItemUsed(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void on(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int newSlot = event.getNewSlot();
        ItemStack item = player.getInventory().getItem(newSlot);
        if (item == null) {
            return;
        }

        getCooldown(player).handleItemUsed(item.getType());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    protected void on(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        Player player = (Player) damager;
        Cooldown cooldown = getCooldown(player);
        Optional<Double> damageModifier = cooldown.getDamageModifier();
        damageModifier.ifPresent((mod) -> event.setDamage(event.getDamage() * mod));
    }

    @EventHandler
    protected void on(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer());
    }

    public class Cooldown {
        private final Player player;
        private long timeSinceLastClick = 0;

        public Cooldown(Player player) {
            this.player = player;
        }

        public void handleItemUsed() {
            handleItemUsed(player.getItemInHand().getType());
        }

        public void handleItemUsed(Material material) {
            // Method may be called from scheduled tasks, check if still enabled
            if (!isEnabled()) {
                return;
            }

            List<String> weaponMaterialEndings = ImmutableList.of("_SWORD", "_AXE", "_PICKAXE", "_SPADE");
            if (!weaponMaterialEndings.stream().anyMatch(material.name()::endsWith)) {
                return;
            }

            timeSinceLastClick = 0;
            updateActionbar();
        }

        public void onTick() {
            timeSinceLastClick++;
            updateActionbar();
        }

        public Optional<Double> getDamageModifier() {
            if (timeSinceLastClick > 12) {
                return Optional.empty();
            }

            // Taken from minecraft wiki
            double modifier = 0.2 + Math.pow((timeSinceLastClick + 0.5) / 12.5d, 2) * 0.8;
            modifier = Math.min(1d, modifier);
            modifier = Math.max(0.2d, modifier);
            return Optional.of(modifier);
        }

        public void updateActionbar() {
            if (timeSinceLastClick > 12) {
                return;
            }

            long grayBars = timeSinceLastClick;
            long darkGrayBars = 12 - timeSinceLastClick;
            String grayBarsString = "§f" + StringUtils.repeat("\u275A", (int)grayBars);
            String darkGrayBarsString = "§a" + StringUtils.repeat("\u275A", (int)darkGrayBars);
            String leftPadding = StringUtils.repeat(" ", 12);
            String bars = leftPadding + grayBarsString + darkGrayBarsString;
            PacketUtils.sendAction(player, bars);
        }
    }
}