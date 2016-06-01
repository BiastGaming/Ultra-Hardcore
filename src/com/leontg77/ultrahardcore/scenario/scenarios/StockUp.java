package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * StockUp scenario class.
 * 
 * @author LeonTG77
 */
public class StockUp extends Scenario implements Listener {

    public StockUp() {
        super("StockUp", "Each time a player dies, every other player currently alive gains 1 empty (unhealed) heart. When crafting arrows you get double the amount.");
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        for (Player online : game.getPlayers()) {
            online.setMaxHealth(online.getMaxHealth() + 2);
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Recipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        if (result.getType() != Material.ARROW) {
            return;
        }

        CraftingInventory inv = event.getInventory();
        inv.getResult().setAmount(result.getAmount() * 2);
    }
}