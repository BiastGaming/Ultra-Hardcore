package com.leontg77.ultrahardcore.feature.xp;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Nerfed Quartz XP feature class.
 * 
 * @author LeonTG77
 */
public class NerfedQuartzXPFeature extends ToggleableFeature implements Listener {

    public NerfedQuartzXPFeature() {
        super("Nerfed Quartz XP", "Make the xp from quartz be 50% less when dropped.");

        icon.setType(Material.QUARTZ);
        slot = 30;
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!isEnabled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getType() != Material.QUARTZ_ORE) {
            return;
        }

        event.setExpToDrop(event.getExpToDrop() / 2);
    }
}