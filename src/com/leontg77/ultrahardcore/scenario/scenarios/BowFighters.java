package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BowFighers scenario class
 * 
 * @author Original code by: dans1988, Rewritten by LeonTG77.
 */
public class BowFighters extends Scenario implements Listener {

    public BowFighters() {
        super("BowFighters", "Everyone is given 1 arrow, 2 string and an Infinity book. The highest tier of melee weapon you can get is wood sword/stone axe.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(final GameStartEvent event) {
        final ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemStack string = new ItemStack(Material.STRING, 2);
        final ItemStack arrow = new ItemStack(Material.ARROW);
        
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.ARROW_INFINITE, 1, true);
        book.setItemMeta(meta);
        
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerUtils.giveItem(online, book, arrow, string);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final PlayerPickupItemEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }
        
        final Item item = event.getItem();
        final ItemStack stack = item.getItemStack();
        
        switch (stack.getType()) {
        case STONE_SWORD:
        case IRON_SWORD:
        case DIAMOND_SWORD:
        case IRON_AXE:
        case DIAMOND_AXE:
            event.setCancelled(true);
            item.remove();
            break;
        default:
            break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final InventoryClickEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }
        
        final ItemStack item = event.getCurrentItem();
        
        if (item == null) {
            return;
        }
        
        switch (item.getType()) {
        case STONE_SWORD:
        case IRON_SWORD:
        case DIAMOND_SWORD:
        case IRON_AXE:
        case DIAMOND_AXE:
            Player player = (Player) event.getWhoClicked();
            
            event.setCurrentItem(new ItemStack(Material.AIR));
            event.setCancelled(true);
            
            player.sendMessage(ChatColor.RED + "You can't pick that up in bowfighters!");
            break;
        default:
            break;
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(final CraftItemEvent event)  {
        if (!State.isState(State.INGAME)) {
            return;
        }
        
        final ItemStack item = event.getCurrentItem();
        
        // never tested if this would happend, but incase..
        if (item == null) {
            return;
        }

        switch (item.getType()) {
        case STONE_SWORD:
        case IRON_SWORD:
        case DIAMOND_SWORD:
        case IRON_AXE:
        case DIAMOND_AXE:
            Player player = (Player) event.getWhoClicked();

            player.sendMessage(ChatColor.RED + "You can't craft that in bowfighters!");
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(final EntityDamageByEntityEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Entity damager = event.getDamager();
        final Entity entity = event.getEntity();
        
        if (!(entity instanceof Player) || !(damager instanceof Player)) {
            return;
        }

        final Player attacker = (Player) damager;
        final ItemStack tool = attacker.getItemInHand();
        
        if (tool == null || tool.getType() != Material.DIAMOND_PICKAXE) {
            return;
        }

        attacker.sendMessage(ChatColor.RED + "PvP damage with that item is disabled!");
        event.setCancelled(true);
    }
}