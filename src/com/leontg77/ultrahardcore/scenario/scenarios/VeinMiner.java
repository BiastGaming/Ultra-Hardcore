package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * VeinMiner scenario class.
 * 
 * @author LeonTG77
 */
public class VeinMiner extends Scenario implements Listener {
    private final Main plugin;

    public VeinMiner(Main plugin) {
        super("VeinMiner", "When you mine a part of a ore vein while shifting the entire thing mines.");

        this.plugin = plugin;
    }

    private static final Predicate<Material> IS_ORE = (m) -> m.name().endsWith("_ORE");

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.isSneaking()) {
            return;
        }

        if (!IS_ORE.test(block.getType())) {
            return;
        }

        List<Block> vein = new ArrayList<Block>();
        BlockUtils.getVein(block, vein);
        vein.remove(block);

        new BukkitRunnable() {
            public void run() {
                if (vein.isEmpty()) {
                    cancel();
                    return;
                }

                Block thisBlock = vein.remove(0);

                BlockUtils.blockBreak(null, thisBlock);
                thisBlock.breakNaturally(player.getItemInHand());
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}