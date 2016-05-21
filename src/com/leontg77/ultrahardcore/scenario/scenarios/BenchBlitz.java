package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BenchBlitz scenario class
 * 
 * @author LeonTG77
 */
public class BenchBlitz extends Scenario implements Listener {
    private static final String PREFIX = "§6§lBenchBlitz §8» §7";

    private final Game game;

    public BenchBlitz(Game game) {
        super("BenchBlitz", "You can only craft one crafting table.");

        this.game = game;
    }

    private final Set<UUID> hasCrafted = new HashSet<UUID>();

    @Override
    public void onDisable() {
        hasCrafted.clear();
    }

    @Override
    public void onEnable() {
        onDisable();
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        // this cast is safe, fyi.
        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = event.getCurrentItem();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (item.getType() != Material.WORKBENCH) {
            return;
        }

        if (event.isShiftClick()) {
            player.sendMessage(ChatColor.RED + "You cannot shift click to craft crafting tables.");
            event.setCancelled(true);
            return;
        }

        if (hasCrafted.contains(player.getUniqueId())) {
            player.sendMessage(PREFIX + "You already crafted your only crafting table.");
            event.setCancelled(true);
            return;
        }

        player.sendMessage(PREFIX + "That's your only crafting table, Don't lose it!");
        hasCrafted.add(player.getUniqueId());
    }
}