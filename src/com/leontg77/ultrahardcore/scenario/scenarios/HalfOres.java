package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * 1/2 Ores scenario class.
 * 
 * @author LeonTG77
 */
public class HalfOres extends Scenario implements Listener {
    private final Set<UUID> hasMined = new HashSet<UUID>();

    public HalfOres() {
        super("1/2Ores", "For every 2 ores you mine, 1 drops.");
    }

    /**
     * A list of all ores in minecraft.
     */
    private static final Set<Material> ORES = ImmutableSet.of(
            Material.COAL_ORE, Material.IRON_ORE, Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE, Material.EMERALD_ORE,
            Material.DIAMOND_ORE, Material.GOLD_ORE, Material.LAPIS_ORE
    );

    @EventHandler(priority = EventPriority.LOW)
    public void on(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!ORES.contains(block.getType())) {
            return;
        }

        if (hasMined.contains(player.getUniqueId())) {
            hasMined.remove(player.getUniqueId());

            event.setCancelled(true);
            block.setType(Material.AIR);

            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
        } else {
            hasMined.add(player.getUniqueId());
        }
    }
}