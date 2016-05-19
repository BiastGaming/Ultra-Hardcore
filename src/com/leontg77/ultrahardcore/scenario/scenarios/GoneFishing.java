package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * GoneFishing scenario class
 * 
 * @author LeonTG77
 */
public class GoneFishing extends Scenario implements Listener {

    public GoneFishing() {
        super("GoneFishing", "Everyone is given an op fishing rod and a stack of anvils, enchantment tables cannot be crafted.");
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.getInventory().remove(Material.FISHING_ROD);
            online.getInventory().remove(Material.ANVIL);
            online.setLevel(0);
        }
    }

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(FinalHealEvent event) {
        ItemStack rod = new ItemStack (Material.FISHING_ROD);
        ItemMeta meta = rod.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 250, true);
        meta.addEnchant(Enchantment.LUCK, 250, true);
        meta.addEnchant(Enchantment.LURE, 3, true);
        rod.setItemMeta(meta);

        ItemStack anvil = new ItemStack (Material.ANVIL, 64);

        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerUtils.giveItem(online, rod);
            PlayerUtils.giveItem(online, anvil);
            online.setLevel(20000);
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        ItemStack item = event.getRecipe().getResult();

        if (item.getType() != Material.ENCHANTMENT_TABLE) {
            return;
        }

        event.getInventory().setResult(new ItemStack(Material.AIR));
    }
}