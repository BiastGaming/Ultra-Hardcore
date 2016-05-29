package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * CraftableTP scenario class.
 * 
 * @author LeonTG77
 */
public class CraftableTP extends Scenario implements Listener {
    public static final String PREFIX = "§5Craftable TP §8» §7";

    public CraftableTP() {
        super("CraftableTP", "If you rename an ender pearl to a online player's name it and right click it will remove the ender pearl from your inventory and teleport you within a 50 blocks radius of that player.");
    }
    
    private final Random rand = new Random();

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Action action = event.getAction();
        Player player = event.getPlayer();
        
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.ENDER_PEARL) {
            return;
        }
        
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        
        String name = item.getItemMeta().getDisplayName();
        Player target = Bukkit.getPlayer(name);
        
        if (target == null || !game.getPlayers().contains(target)) {
            player.sendMessage(PREFIX + "There are no players called '§a" + name + "§7'.");
            return;
        }
        
        if (player == target) {
            player.sendMessage(PREFIX + "You can't teleport to yourself.");
            event.setCancelled(true);
            return;
        }
        
        ItemStack hand = item.clone();
        hand.setAmount(1);
        
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
        
        event.setCancelled(true);
        
        player.sendMessage(PREFIX + "Finding a safe location nearby §a" + target.getName() + "§7...");

        Location loc = target.getLocation().clone();

        boolean teleported = false;
        boolean goodYLevel = false;

        int tpTries = 0;
        int levelTries = 0;
        
        while (!teleported) {
            tpTries++;
            
            if (tpTries > 99) {
                player.sendMessage(PREFIX + "No safe location found, try again later.");
                PlayerUtils.giveItem(player, hand);
                
                teleported = true;
                break;
            }

            int x = loc.getBlockX() + rand.nextInt(50 * 2) - 50;
            int z = loc.getBlockZ() + rand.nextInt(50 * 2) - 50;
            
            while (!goodYLevel) {
                int y = loc.getBlockY() + rand.nextInt(50 * 2) - 50;
                
                Location newLoc = new Location(target.getWorld(), x, y, z);
                
                Material type = newLoc.getBlock().getType();
                
                if (type != Material.WATER && type != Material.STATIONARY_WATER && type != Material.LAVA && type != Material.STATIONARY_LAVA && type != Material.CACTUS && type != Material.AIR) {
                    Block oneUp = newLoc.getBlock().getRelative(BlockFace.UP, 1);
                    Block twoUp = newLoc.getBlock().getRelative(BlockFace.UP, 2);
                    
                    if (oneUp.getType() == Material.AIR && twoUp.getType() == Material.AIR) {
                        player.sendMessage(PREFIX + "Safe location found, teleporting...");
                        player.teleport(newLoc.add(0.5, 1, 0.5));
                        player.sendMessage(PREFIX + "Teleported.");
                        
                        teleported = true;
                        goodYLevel = true;
                    }
                } else {
                    levelTries++;
                    
                    if (levelTries > 9) {
                        goodYLevel = true;
                    }
                }
            }
        }
    }
}