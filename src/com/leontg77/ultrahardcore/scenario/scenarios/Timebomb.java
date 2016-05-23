package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * TimeBomb scenario class
 * 
 * @author LeonTG77
 */
public class Timebomb extends Scenario implements Listener {
    public static final String PREFIX = "§4Timebomb §8» §7";

    private final Main plugin;
    private final Game game;

    public Timebomb(Main plugin, Game game) {
        super("Timebomb", "After killing a player all of their items will appear in a double chest rather than dropping on the ground. You then have 30 seconds to loot what you want and get the hell away from it. This is because the chest explodes after the time is up.");

        this.plugin = plugin;
        this.game = game;
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getEntity();
        final Location loc = player.getLocation().clone();

        if (!game.getWorlds().contains(loc.getWorld())) {
            return;
        }

        Block block = loc.getBlock();

        block = block.getRelative(BlockFace.DOWN);
        block.setType(Material.CHEST);

        Chest chest = (Chest) block.getState();

        block = block.getRelative(BlockFace.NORTH);
        block.setType(Material.CHEST);

        for (ItemStack item : event.getDrops()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            chest.getInventory().addItem(item);
        }

        event.getDrops().clear();

        final ArmorStand stand = player.getWorld().spawn(chest.getLocation().clone().add(0.5, 0, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.setSmall(true);

        stand.setGravity(false);
        stand.setVisible(false);
        
        new BukkitRunnable() {
            private int time = 31; // add one for countdown.l
            
            public void run() {
                time--;
                
                if (time == 0) {
                    PlayerUtils.broadcast(Main.PREFIX + "§a" + player.getName() + "'s §fcorpse has exploded!");

                    loc.getBlock().setType(Material.AIR);

                    loc.getWorld().createExplosion(loc.getBlockX() + 0.5, loc.getBlockY() + 0.5, loc.getBlockZ() + 0.5, 10, false, true);
                    loc.getWorld().strikeLightning(loc); // Using actual lightning to kill the items.

                    stand.remove();
                    cancel();
                    return;
                }
                else if (time == 1) {
                    stand.setCustomName("§4" + time + "s");
                }
                else if (time == 2) {
                    stand.setCustomName("§c" + time + "s");
                }
                else if (time == 3) {
                    stand.setCustomName("§6" + time + "s");
                }
                else if (time <= 15) {
                    stand.setCustomName("§e" + time + "s");
                }
                else {
                    stand.setCustomName("§a" + time + "s");
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}