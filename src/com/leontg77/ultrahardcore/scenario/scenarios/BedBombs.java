package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * BedBombs scenario class
 * 
 * @author LeonTG77
 */
public class BedBombs extends Scenario implements Listener {

    public BedBombs() {
        super("BedBombs", "Beds explode in the overworld.");
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (block == null) {
            return;
        }

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (block.getType() != Material.BED_BLOCK) {
            return;
        }

        block.setType(Material.AIR);
        event.setCancelled(true);

        Location loc = block.getLocation();
        block.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), NumberUtils.randomIntBetween(4, 6), false, true);
    }
}