package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * PotionHealing scenario class.
 * 
 * @author D4mnX
 * @see https://github.com/D4mnX/PotHealing
 */
public class PotionHealing extends Scenario implements Listener {
    private final FeatureManager feat;
    
    public PotionHealing(FeatureManager feat) {
        super("PotionHealing", "When a golden apple is crafted, it becomes a Tier I Health Pot. When a golden head is crafted, it becomes a Tier II Health Pot.");
        
        this.feat = feat;
    }
    
    @Override
    public void onEnable() {
        feat.getFeature(GoldenHeadsFeature.class).setHealAmount(4);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack result = inv.getResult();

        if (result == null) {
            return;
        }

        if (!result.getType().equals(Material.GOLDEN_APPLE)) {
            return;
        }

        if (result.getDurability() != 0) {
            return;
        }
        
        GoldenHeadsFeature ghead = feat.getFeature(GoldenHeadsFeature.class);

        boolean isGoldenHead = ghead.isGoldenHead(result);
        int potionLevel = isGoldenHead ? 2 : 1;
        
        Potion potion = new Potion(PotionType.INSTANT_HEAL, potionLevel);
        ItemStack potionItemStack = potion.toItemStack(1);
        
        inv.setResult(potionItemStack);
    }
}