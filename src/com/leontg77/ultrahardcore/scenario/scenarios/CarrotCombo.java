package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * CarrotCombo scenario class
 * 
 * @author LeonTG77
 */
public class CarrotCombo extends Scenario implements Listener {

    public CarrotCombo() {
        super("CarrotCombo", "When you craft a sword, it gives you a carrot with an enchantment equal to the sword");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler(ignoreCancelled = true)
    public void on(final PrepareItemCraftEvent event)  {
        if (!State.isState(State.INGAME)) {
            return;
        }
        
        final ItemStack result = event.getInventory().getResult();
        int sharpness;
        
        switch (result.getType()) {
        case WOOD_SWORD:
            sharpness = 2;
            break;
        case GOLD_SWORD:
            sharpness = 3;
            break;
        case STONE_SWORD:
            sharpness = 4;
            break;
        case IRON_SWORD:
            sharpness = 5;
            break;
        case DIAMOND_SWORD:
            sharpness = 6;
            break;
        default:
            // not an item we want to care about, so we stop here.
            return;
        }

        ItemStack resultItem = new ItemStack(Material.CARROT_ITEM, 1);
        resultItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, sharpness);
        event.getInventory().setResult(resultItem);
    }
}