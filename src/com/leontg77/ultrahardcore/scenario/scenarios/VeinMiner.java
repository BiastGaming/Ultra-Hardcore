package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * VeinMiner scenario class.
 * 
 * @author LeonTG77
 */
public class VeinMiner extends Scenario implements Listener {
    private final List<Block> doneBlocks = Lists.newArrayList();

    public VeinMiner() {
        super("VeinMiner", "When you mine a part of a ore vein while shifting the entire thing mines, gravel works as well.");
    }

    private static final Predicate<Material> IS_ORE = (m) -> m.name().endsWith("_ORE");

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (doneBlocks.contains(block)) {
            return;
        }
        
        if (!player.isSneaking()) {
            return;
        }
        
        if (!IS_ORE.test(block.getType()) && block.getType() != Material.GRAVEL) {
            return;
        }

        List<Block> vein = BlockUtils.getVein(block);
        vein.remove(block);
        
        int xp = event.getExpToDrop();

        new BukkitRunnable() {
            public void run() {
                if (vein.isEmpty()) {
                    cancel();
                    return;
                }

                Block thisBlock = vein.remove(0);
                doneBlocks.add(thisBlock);
                
                BlockBreakEvent breaking = new BlockBreakEvent(thisBlock, player);
                Bukkit.getPluginManager().callEvent(breaking);
                
                if (!breaking.isCancelled()) {
                    BlockUtils.blockBreak(null, thisBlock);
                    thisBlock.breakNaturally(player.getItemInHand());
                }
                
                if (xp > 0) {
                    ExperienceOrb orb = player.getWorld().spawn(thisBlock.getLocation(), ExperienceOrb.class);
                    orb.setExperience(xp);
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}