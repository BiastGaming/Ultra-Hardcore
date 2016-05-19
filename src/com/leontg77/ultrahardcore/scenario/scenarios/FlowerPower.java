package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * FlowerPower scenario class
 * 
 * @author LeonTG77
 */
public class FlowerPower extends Scenario implements Listener {

    public FlowerPower() {
        super("FlowerPower", "If you break flowers they will drop an random item.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.RED_ROSE && block.getType() != Material.YELLOW_FLOWER && block.getType() != Material.DOUBLE_PLANT) {
            return;
        }

        if (block.getType() == Material.DOUBLE_PLANT) {
            int damage = BlockUtils.getDurability(block);

            if (damage == 2 || damage == 3) {
                return;
            }
        }

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);
        BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), randomItem());

        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    /**
     * Get an random item out of all minecraft items.
     *
     * @return An random item.
     */
    private ItemStack randomItem() {
        Random r = new Random();

        ArrayList<Material> list = new ArrayList<Material>();

        for (Material type : Material.values()) {
            if (type == Material.AIR || type == Material.BEDROCK || type == Material.BARRIER) {
                continue;
            }

            list.add(type);
        }

        Material m = list.get(r.nextInt(list.size()));
        int a = r.nextInt(m.getMaxStackSize());

        if (a == 0) {
            a = 1;
        }

        return new ItemStack(m, a);
    }
}