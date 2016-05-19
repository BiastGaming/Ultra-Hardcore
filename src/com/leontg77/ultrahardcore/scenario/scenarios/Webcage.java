package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Webcage scenario class.
 * 
 * @author LeonTG77
 */
public class Webcage extends Scenario implements Listener {
    private final Game game;

    /**
     * Webcage scenario class constructor.
     */
    public Webcage(Game game) {
        super("Webcage", "A small \"cage\" of cobweb is formed around you after you die. This makes it hard to get your items but creates an interesting situation for melee where you are \"trapped\" after killing someone.");

        this.game = game;
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();
        Location loc = player.getLocation();

        if (!game.getWorlds().contains(loc.getWorld())) {
            return;
        }

        int bX = loc.getBlockX();
        int bY = loc.getBlockY();
        int bZ = loc.getBlockZ();

        for (int y = bY - 10; y <= bY + 10; y++) {
            for (int x = bX - 10; x <= bX + 10; x++) {
                for (int z = bZ - 10; z <= bZ + 10; z++) {
                    Block block = loc.getWorld().getBlockAt(x, y, z);

                    int distance = (int) block.getLocation().distance(new Location(loc.getWorld(), bX, bY, bZ));

                    if (distance == 4 && block.getType() == Material.AIR) {
                        block.setType(Material.WEB);
                        block.getState().update(true);
                    }
                }
            }
        }
    }
}