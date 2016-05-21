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
 * BloodLapis scenario class
 * 
 * @author LeonTG77
 */
public class BloodLapis extends Scenario implements Listener {

    public BloodLapis() {
        super("BloodLapis", "Every time you mine lapis you take half a heart.");
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (block.getType() != Material.LAPIS_ORE) {
            return;
        }

        PlayerUtils.damage(player, 1);
    }
}