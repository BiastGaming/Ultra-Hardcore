package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * FrozenInTime scenario class.
 * 
 * @author LeonTG77
 */
public class FrozenInTime extends Scenario implements Listener {

    public FrozenInTime() {
        super("FrozenInTime", "Basically players are stuck amidst time being frozen. Stuff like sugarcane doesn't break when you break the source, gravel and sand don't go by the laws of gravity, mobs can't move. Liquids don't flow, if you place water/lava, not either. The time doesn't change, however, it will be night for episode 2 and 4. Trees don't decay, etc.");
    }
    
    @Override
    public void onEnable() {
        ItemStack diaPick = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        
        for (short i = 0; i <= diaPick.getType().getMaxDurability(); i++) {
            ItemStack newOne = diaPick.clone();
            newOne.setDurability(i);
            
            ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.OBSIDIAN, 1))
                    .addIngredient(Material.WATER_BUCKET)
                    .addIngredient(Material.LAVA_BUCKET)
                    .addIngredient(newOne.getData());
            
            Bukkit.addRecipe(recipe);
        }
    }

    @EventHandler
    public void on(BlockPhysicsEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        event.setCancelled(true);
    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(LeavesDecayEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        event.setCancelled(true);
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        
        if (result.getType() != Material.OBSIDIAN) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        PlayerUtils.giveItem(player, new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.BUCKET));
       
        for (ItemStack item : event.getInventory().getMatrix()) {
            item.setType(Material.AIR);
        }
    }
}