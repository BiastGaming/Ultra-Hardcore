package com.leontg77.ultrahardcore.feature.other;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.gui.guis.HallOfFameGUI;
import com.leontg77.ultrahardcore.gui.guis.StatsGUI;
import com.leontg77.ultrahardcore.gui.guis.TopStatsGUI;

/**
 * Text commands feature class.
 *  
 * @author LeonTG77
 */
public class TextCommandsFeature extends Feature implements Listener {
    private final GUIManager gui;

    public TextCommandsFeature(GUIManager gui) {
        super("Text Commands", "Different texts in spawn that you can click.");
        
        this.gui = gui;
    }

    @EventHandler
    public void on(PlayerInteractAtEntityEvent event) {
        Entity clicked = event.getRightClicked();
        Player player = event.getPlayer();
        
        if (!(clicked instanceof ArmorStand)) {
            return;
        }
        
        ArmorStand stand = (ArmorStand) clicked;
        
        if (stand.isVisible()) {
            return;
        }

        World world = player.getWorld();
        
        if (!world.getName().equals("lobby")) {
            return;
        }
        
        User user = plugin.getUser(player);

        switch (stand.getCustomName()) {
        case "Game Info":
            player.openInventory(gui.getGUI(GameInfoGUI.class).get());
            break;
        case "Your Stats":
            player.openInventory(gui.getGUI(StatsGUI.class).get(user));
            break;
        case "Top Stats":
            player.openInventory(gui.getGUI(TopStatsGUI.class).get(1));
            break;
        case "Hall of Fame":
            try {
                String host = game.getHostHOFName();
                
                HallOfFameGUI hof = gui.getGUI(HallOfFameGUI.class);
                Inventory inv = hof.get(host);

                hof.currentHost.put(player.getName(), host);
                hof.currentPage.put(player.getName(), 1);

                player.openInventory(inv);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "There are no HOF's to open.");
            }
            break;
        }
    }
}