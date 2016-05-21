package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Inventors scenario class
 * 
 * @author LeonTG77
 */
public class Inventors extends Scenario implements Listener {
    private final Set<String> craftedItems = new HashSet<String>();
    private static final String PREFIX = "§aInventors §8» §f";

    public Inventors() {
        super("Inventors", "The first person to craft any item will be broadcasted in chat.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(CraftItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        ItemStack item = event.getRecipe().getResult();
        Player player = (Player) event.getWhoClicked();

        String name = item.getData().toString();

        if (craftedItems.contains(name)) {
            return;
        }

        PlayerUtils.broadcast(PREFIX + player.getName() + "§7 was the first to craft §f" + name.toLowerCase().replaceAll("_", " ") + "§7.");
        craftedItems.add(name);
    }
}