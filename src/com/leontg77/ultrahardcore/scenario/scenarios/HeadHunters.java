package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * HeadHunters scenario class.
 * 
 * @author LeonTG77
 */
public class HeadHunters extends Scenario implements Listener {

    public HeadHunters() {
        super("HeadHunters", "When a player dies their head is spawned in a chest within a 75 block radius of where the player died. The chest can't spawn in a cave");
    }
    
    private final Random rand = new Random();

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getEntity();
        Location loc = player.getLocation().clone();

        int x = loc.getBlockX() + rand.nextInt(75 * 2) - 75;
        int z = loc.getBlockZ() + rand.nextInt(75 * 2) - 75;
        
        Block highest = LocationUtils.getHighestBlock(new Location(player.getWorld(), x, 255, z)).getBlock();
        highest.setType(Material.CHEST);

        Chest chest = (Chest) highest.getState();
        
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(player.getName());
        skull.setItemMeta(meta);
        
        chest.getInventory().setItem(13, skull);
    }
}