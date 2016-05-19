package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * InfinieEnchanter scenario class
 * 
 * @author LeonTG77
 */
public class InfiniteEnchanter extends Scenario implements Listener {

    public InfiniteEnchanter() {
        super("InfiniteEnchanter", "Everyone is given 128 enchantment tables, anvils and bookshelves.");
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.getInventory().remove(Material.ENCHANTMENT_TABLE);
            online.getInventory().remove(Material.BOOKSHELF);
            online.getInventory().remove(Material.ANVIL);
            online.setLevel(0);
        }
    }

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(FinalHealEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.getInventory().addItem(new ItemStack (Material.ENCHANTMENT_TABLE, 128));
            online.getInventory().addItem(new ItemStack (Material.BOOKSHELF, 128));
            online.getInventory().addItem(new ItemStack (Material.ANVIL, 128));
            online.setLevel(23000);
        }
    }
}