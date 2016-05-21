package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BloodDiamonds scenario class
 * 
 * @author LeonTG77
 */
public class BloodDiamonds extends Scenario implements Listener {

    public BloodDiamonds() {
        super("BloodDiamonds", "Every time you mine a diamond you take half a heart.");
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (block.getType() != Material.DIAMOND_ORE) {
            return;
        }

        PlayerUtils.damage(player, 1);
    }
}