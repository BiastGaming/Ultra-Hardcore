package com.leontg77.ultrahardcore.gui.guis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.gui.GUI;

/**
 * Selector inventory GUI class.
 * 
 * @author LeonTG77
 */
public class SelectorGUI extends GUI implements Listener {
    private final Game game;

    /**
     * Selector inventory GUI class constructor.
     *
     * @param game The game class.
     */
    public SelectorGUI(Game game) {
        super("Selector", "A inventory containing all players playing the game.");

        this.game = game;
    }

    private final Map<Integer, Inventory> inventories = new HashMap<Integer, Inventory>();
    public final Map<String, Integer> currentPage = new HashMap<String, Integer>();

    @Override
    public void onSetup() {
        update();
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        update();
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        update();
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent event) {
        update();
    }

    @EventHandler
    public void on(PlayerGameModeChangeEvent event) {
        update();
    }

    @EventHandler
    public void on(GameStartEvent event) {
        update();
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory inv = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        if (!inv.getTitle().equals("§4Player Selector")) {
            return;
        }

        event.setCancelled(true);

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        int oldPlayerPage = currentPage.get(player.getName());
        IntUnaryOperator op = null;

        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aNext page")) {
            op = oldPage -> oldPage + 1;
        } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aPrevious page")) {
            op = oldPage -> oldPage - 1;
        } else {
            player.closeInventory();

            Player target = Bukkit.getPlayer(item.getItemMeta().getDisplayName().substring(2));

            if (target == null) {
                player.sendMessage(Main.PREFIX + "The player you clicked is not online.");
                return;
            }

            player.sendMessage(Main.PREFIX + "You teleported to §a" + target.getName() + "§7.");
            player.teleport(target);
            return;
        }

        int newPlayerPage = op.applyAsInt(oldPlayerPage);
        
        inventories.computeIfPresent(newPlayerPage, (page, inventory) -> {
            currentPage.put(player.getName(), page);
            player.openInventory(inventory);
            return inventory;
        });
    }

    /**
     * Get the selector inventory for page 1.
     *
     * @return The inventory for page 1.
     */
    public Inventory get() {
        return get(1);
    }

    /**
     * Get the selector inventory for the given page.
     *
     * @param page The page.
     * @return The inventory for that page.
     */
    public Inventory get(int page) {
        return inventories.get(page);
    }

    /**
     * Update the selector inventory.
     */
    public void update() {
        List<Player> players = game.getPlayers();
        Inventory inv = null;

        int pages = ((players.size() / 28) + 1);

        for (int current = 1; current <= pages; current++) {
            if (inventories.containsKey(current)) {
                inv = inventories.get(current);
            } else {
                inv = Bukkit.createInventory(null, 54, "§4Player Selector");
                inventories.put(current, inv);
            }

            inv.clear();

            for (int i = 0; i < 35; i++) {
                if (players.isEmpty()) {
                    continue;
                }

                if (isSide(i)) {
                    continue;
                }

                Player target = players.remove(0);

                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setDisplayName("§a" + target.getName());
                meta.setLore(Arrays.asList("§7Click to teleport."));
                meta.setOwner(target.getName());
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }

            ItemStack nextpage = new ItemStack (Material.ARROW);
            ItemMeta pagemeta = nextpage.getItemMeta();
            pagemeta.setDisplayName(ChatColor.GREEN + "Next page");
            pagemeta.setLore(Arrays.asList("§7Switch to the next page."));
            nextpage.setItemMeta(pagemeta);

            ItemStack prevpage = new ItemStack (Material.ARROW);
            ItemMeta prevmeta = prevpage.getItemMeta();
            prevmeta.setDisplayName(ChatColor.GREEN + "Previous page");
            prevmeta.setLore(Arrays.asList("§7Switch to the previous page."));
            prevpage.setItemMeta(prevmeta);

            if (current != 1) {
                inv.setItem(47, prevpage);
            }

            if (current != pages) {
                inv.setItem(51, nextpage);
            }
        }
    }
}