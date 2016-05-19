package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * GoldRush scenario class
 * 
 * @author LeonTG77
 */
public class GoldRush extends Scenario implements Listener {

    public GoldRush() {
        super("GoldRush", "You cannot craft leather or iron armor.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack item = event.getRecipe().getResult();

        if (!isLeatherOrIronArmor(item)) {
            return;
        }

        event.getInventory().setResult(new ItemStack(Material.AIR));
    }

    /**
     * Check if the given item is leather armor or iron armor.
     *
     * @param item The item checking.
     * @return True if it is, false if not.
     */
    private boolean isLeatherOrIronArmor(ItemStack item) {
        switch (item.getType()) {
        case LEATHER_HELMET:
        case LEATHER_CHESTPLATE:
        case LEATHER_LEGGINGS:
        case LEATHER_BOOTS:
        case IRON_HELMET:
        case IRON_CHESTPLATE:
        case IRON_LEGGINGS:
        case IRON_BOOTS:
            return true;
        default:
            return false;
        }
    }
}