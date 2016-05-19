package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Krenzinator scenario class
 * 
 * @author dans1988
 */
public class Krenzinator extends Scenario implements Listener {
    private static final ShapelessRecipe diamond = new ShapelessRecipe(new ItemStack(Material.DIAMOND)).addIngredient(9, Material.REDSTONE_BLOCK);

    public Krenzinator() {
        super("Krenzinator", "Play UHC like Krenzinator does, Reddit post: https://redd.it/2ee99q");
    }

    @Override
    public void onDisable() {
        Iterator<Recipe> it = Bukkit.recipeIterator();

        while (it.hasNext()) {
            Recipe res = it.next();

            if (res.equals(diamond)) {
                it.remove();
            }
        }
    }

    @Override
    public void onEnable() {
        Bukkit.addRecipe(diamond);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player victim = event.getEntity();

        if (!victim.getUniqueId().toString().equals("f6eb67da-99f1-4352-b5c5-c0440be575f1") && !victim.getUniqueId().toString().equals("42d908a4-c270-4059-b796-53d217f9429f")) {
            return;
        }

        event.getDrops().add(new ItemStack(Material.DIAMOND, 10));
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getVehicle() instanceof Horse) && !(event.getEntered() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntered();
        Horse horse = (Horse) event.getVehicle();
        
        if (horse.getVariant().equals(Horse.Variant.DONKEY)) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't mount horses in this gamemode! (Only Donkeys are allowed!)");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Entity damager = event.getDamager();

        if (!(damager instanceof Egg)) {
            return;
        }

        event.setDamage(1.0);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event)  {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }
        
        ItemStack item = event.getCurrentItem();
        
        if (!item.getType().equals(Material.DIAMOND_SWORD)) {
            return;
        }
        
        PlayerUtils.damage((Player) event.getWhoClicked(), 2.0);
    }
}