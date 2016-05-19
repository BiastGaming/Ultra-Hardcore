package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * MonstersInc scenario class.
 * 
 * @author LeonTG77
 */
public class MonstersInc extends Scenario implements Listener {
    public static final String PREFIX = "§bMonster's Inc §8§ §7";

    private final ScatterManager scatter;

    public MonstersInc(ScatterManager scatter) {
        super("MonstersInc", "If you place a door on the map, go through it and there's 2 or more doors on the map, you will be teleported to one of those doors. If there's no doors other than your door, you will be teleported to a random spot on the map.");

        this.scatter = scatter;
    }

    private final List<Location> doors = new ArrayList<Location>();

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();

        if (!isDoor(block.getType())) {
            return;
        }

        doors.add(block.getLocation());
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();

        if (doors.contains(block.getRelative(BlockFace.UP).getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (!doors.contains(block.getLocation())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (block == null || !isDoor(block.getType())) {
            return;
        }

        if (!doors.contains(block.getLocation())) {
            return;
        }

        Location l = block.getLocation();
        List<Location> door = new ArrayList<Location>();

        for (Location lo : doors) {
            if (l.getBlockX() == lo.getBlockX() && l.getBlockY() == lo.getBlockY() && l.getBlockZ() == lo.getBlockZ()) {
                continue;
            }

            door.add(lo);
        }

        Location loc;

        if (door.isEmpty()) {
            List<Location> locs = scatter.findScatterLocations(block.getWorld(), 500, 1);

            if (locs.isEmpty()) {
                player.sendMessage(PREFIX + "There are no other doors or random locations.");
                return;
            }

            player.sendMessage(PREFIX + "There are no doors, finding a random location...");
            loc = locs.get(0);
        } else {
            loc = door.get(new Random().nextInt(door.size()));
        }

        player.sendMessage(PREFIX + "Teleported to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
        player.teleport(new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 0.5, loc.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
    }

    /**
     * Check if the given material is a door.
     *
     * @param type The material checking.
     * @return True if it is, false otherwise.
     */
    private boolean isDoor(Material type) {
        switch (type) {
        case ACACIA_DOOR:
        case WOODEN_DOOR:
        case DARK_OAK_DOOR:
        case BIRCH_DOOR:
        case JUNGLE_DOOR:
        case SPRUCE_DOOR:
            return true;
        default:
            return false;
        }
    }
}