package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Grandpas scenario class.
 * 
 * @author LeonTG77
 */
public class Grandpas extends Scenario implements Listener {
    private static final PotionEffectType SLOWNESS = PotionEffectType.SLOW;
    
    private final ItemStack stick;
    
    public Grandpas() {
        super("Grandpas", "You have slowness 2 unless you're holding your walking stick (a stick of knockback 2). Players receive a walking stick at the start of the game.");
        
        this.stick = new ItemStack(Material.STICK, 1);
        
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName("§aWalking Stick");
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        stick.setItemMeta(meta);
    }

    private final Set<UUID> scheduledItem = Sets.newHashSet();
    
    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        on(new GameStartEvent());
    }
    
    @EventHandler
    public void on(GameStartEvent event) {
        for (OfflinePlayer offline : game.getOfflinePlayers()) {
            Player online = offline.getPlayer();
            
            if (online == null) {
                scheduledItem.add(offline.getUniqueId());
                continue;
            }
            
            PlayerUtils.giveItem(online, stick);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (scheduledItem.contains(player.getUniqueId())) {
            PlayerUtils.giveItem(player, stick);
            scheduledItem.remove(player.getUniqueId());
        }
        
        ItemStack item = player.getItemInHand();
        
        if (player.hasPotionEffect(SLOWNESS)) {
            player.removePotionEffect(SLOWNESS);
        }
        
        if (!stick.equals(item)) {
            player.addPotionEffect(new PotionEffect(SLOWNESS, NumberUtils.TICKS_IN_999_DAYS, 1));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerItemHeldEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        int newSlot = event.getNewSlot();
        
        ItemStack item = player.getInventory().getItem(newSlot);
        
        if (player.hasPotionEffect(SLOWNESS)) {
            player.removePotionEffect(SLOWNESS);
        }
        
        if (!stick.equals(item)) {
            player.addPotionEffect(new PotionEffect(SLOWNESS, NumberUtils.TICKS_IN_999_DAYS, 1));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerPickupItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        
        if (player.hasPotionEffect(SLOWNESS)) {
            player.removePotionEffect(SLOWNESS);
        }
        
        if (!stick.equals(item)) {
            player.addPotionEffect(new PotionEffect(SLOWNESS, NumberUtils.TICKS_IN_999_DAYS, 1));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerDropItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        
        if (player.hasPotionEffect(SLOWNESS)) {
            player.removePotionEffect(SLOWNESS);
        }
        
        if (!stick.equals(item)) {
            player.addPotionEffect(new PotionEffect(SLOWNESS, NumberUtils.TICKS_IN_999_DAYS, 1));
        }
    }
}