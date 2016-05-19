package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * DoubleOrNothing scenario class
 * 
 * @author LeonTG77
 */
public class DoubleOrNothing extends Scenario implements Listener {
    private final CutClean cc;
    private final Game game;

    public DoubleOrNothing(Game game, CutClean cc) {
        super("DoubleOrNothing", "On mine of iron, diamond, emerald or gold ore you have a 50% chance of 2 of the ore dropping or no ores dropping.");

        this.game = game;
        this.cc = cc;
    }

    private final Random rand = new Random();

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        Location toDrop = block.getLocation().add(0.5, 0.7, 0.5);

        switch (block.getType()) {
        case IRON_ORE:
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);

            if (rand.nextBoolean()) {
                BlockUtils.dropItem(toDrop, new ItemStack(cc.isEnabled() ? Material.IRON_INGOT : Material.IRON_ORE, 2));
            }
            break;
        case GOLD_ORE:
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);

            if (rand.nextBoolean()) {
                BlockUtils.dropItem(toDrop, new ItemStack(cc.isEnabled() ? Material.GOLD_INGOT : Material.GOLD_ORE, 2));
            }
            break;
        case DIAMOND_ORE:
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);

            if (rand.nextBoolean()) {
                BlockUtils.dropItem(toDrop, new ItemStack(Material.DIAMOND, 2));
            }
            break;
        case EMERALD_ORE:
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);

            if (rand.nextBoolean()) {
                BlockUtils.dropItem(toDrop, new ItemStack(Material.EMERALD));
            }
            break;
        default:
            break;
        }
    }
}