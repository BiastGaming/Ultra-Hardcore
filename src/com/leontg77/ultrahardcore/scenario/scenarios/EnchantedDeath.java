package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * EnchantedDeath scenario class.
 * 
 * @author LeonTG77
 */
public class EnchantedDeath extends Scenario implements Listener {

    /**
     * EnchantedDeath scenario class constructor.
     */
    public EnchantedDeath() {
        super("EnchantedDeath", "You cannot craft enchantment tables, however if a player dies to PvP they will drop an enchantment table.");
    }

    @EventHandler(priority = EventPriority.LOWEST) // lowest priority incase any other things need the drops, like timebomb
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == null) {
            return;
        }

        event.getDrops().add(new ItemStack(Material.ENCHANTMENT_TABLE));
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack item = event.getRecipe().getResult();

        if (item == null) {
            return;
        }

        if (item.getType() == Material.ENCHANTMENT_TABLE) {
            inv.setResult(new ItemStack(Material.AIR));
        }
    }
}