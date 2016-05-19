package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * TripleOres scenario class
 * 
 * @author LeonTG77
 */
public class TripleOres extends Scenario implements Listener {
    private final ScenarioManager scen;

    public TripleOres(ScenarioManager scen) {
        super("TripleOres", "When mining an ore it drops 3 of the dropped item.");

        this.scen = scen;
    }

    private final Set<Location> locs = new HashSet<Location>();

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getDrops(player.getItemInHand()).isEmpty()) {
            return;
        }

        boolean cutclean = scen.getScenario(CutClean.class).isEnabled();

        if (block.getType() == Material.COAL_ORE) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.COAL, 3));

            event.setCancelled(true);
            block.setType(Material.AIR);

            ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
            exp.setExperience(1);
        }

        if (block.getType() == Material.IRON_ORE) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);

            if (cutclean) {
                BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.IRON_INGOT, 3));

                ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
                exp.setExperience(2);
            } else {
                if (locs.contains(block.getLocation())) {
                    return;
                }

                BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.IRON_ORE, 3));
            }
        }

        if (block.getType() == Material.GOLD_ORE) {
            if (scen.getScenario(Barebones.class).isEnabled()) {
                return;
            }

            if (scen.getScenario(Goldless.class).isEnabled()) {
                return;
            }

            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);

            if (cutclean) {
                BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.GOLD_INGOT, 3));

                ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
                exp.setExperience(3);
            } else {
                if (locs.contains(block.getLocation())) {
                    return;
                }

                BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.GOLD_ORE, 3));
            }
        }

        if (block.getType() == Material.EMERALD_ORE) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.EMERALD, 3));

            event.setCancelled(true);
            block.setType(Material.AIR);

            ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
            exp.setExperience(4);
        }

        if (block.getType() == Material.DIAMOND_ORE) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.DIAMOND, 3));

            event.setCancelled(true);
            block.setType(Material.AIR);

            ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
            exp.setExperience(5);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (scen.getScenario(CutClean.class).isEnabled()) {
            return;
        }

        Block block = event.getBlockPlaced();

        if (block.getType() != Material.IRON_ORE && block.getType() != Material.GOLD_ORE) {
            return;
        }

        if (locs.contains(block.getLocation())) {
            return;
        }

        locs.add(block.getLocation());
    }
}