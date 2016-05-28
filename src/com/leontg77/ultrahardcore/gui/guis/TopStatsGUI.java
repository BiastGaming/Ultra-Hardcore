package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.events.GameEndEvent;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Top Stats inventory GUI class.
 * 
 * @author LeonTG77
 */
public class TopStatsGUI extends GUI implements Listener {

    public TopStatsGUI() {
        super("Top Stats", "A inventory containing all the top 10 players with a stat.");
    }
    
    private final Inventory invTwo = Bukkit.createInventory(null, 54, "§4Top 10 Stats, Page 2");
    private final Inventory inv = Bukkit.createInventory(null, 54, "§4Top 10 Stats, Page 1");

    @Override
    public void onSetup() {
        update();
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getInventory();

        if (!this.inv.getTitle().equals(inv.getTitle()) && !this.invTwo.getTitle().equals(inv.getTitle())) {
            return;
        }

        event.setCancelled(true);
        
        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aNext page")) {
            player.openInventory(invTwo);
        } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aPrevious page")) {
            player.openInventory(this.inv);
        }
    }
    
    @EventHandler
    public void on(GameEndEvent event) {
        update();
    }

    /**
     * Get the top stats inventory.
     *
     * @return The inventory.
     */
    public Inventory get(int number) {
        if (number == 1) {
            return inv;
        } 
        else if (number == 2) {
            return invTwo;
        }
        else {
            return null;
        }
    }

    /**
     * Update the top stats inventory.
     */
    public void update() {
        glassify(invTwo);
        glassify(inv);

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
        
        invTwo.setItem(47, prevpage);
        inv.setItem(51, nextpage);
        
        List<String> data = new ArrayList<String>();
        int slot = 0;

        Map<String, Double> deaths = new HashMap<String, Double>();
        Map<String, Double> kills = new HashMap<String, Double>();

        Set<FileConfiguration> files = FileUtils.getUserFiles();

        for (Stat stat : Stat.values()) {
            data.clear();

            for (FileConfiguration config : files) {
                String name = config.getString("username");
                double number = config.getDouble("stats." + stat.name().toLowerCase());

                data.add(number + " " + name);

                if (stat == Stat.KILLS) {
                    kills.put(name, number);
                }

                if (stat == Stat.DEATHS) {
                    deaths.put(name, number);
                }
            }

            Collections.sort(data, new Comparator<String>() {
                public int compare(String a, String b) {
                    double aVal = Double.parseDouble(a.split(" ")[0]);
                    double bVal = Double.parseDouble(b.split(" ")[0]);

                    return Double.compare(aVal, bVal);
                }
            });

            addItem(data, stat.getName(), slot);
            slot += 2;
        }

        data.clear();

        for (String name : kills.keySet()) {
            int d = (int) deaths.get(name).doubleValue();
            int k = (int) kills.get(name).doubleValue();

            double kdr;

            if (d < 2) {
                continue;
            }

            kdr = ((double) k) / ((double) d);

            data.add(NumberUtils.formatDouble(kdr) + " " + name);
        }

        Collections.sort(data, new Comparator<String>() {
            public int compare(String a, String b) {
                double aVal = Double.parseDouble(a.split(" ")[0]);
                double bVal = Double.parseDouble(b.split(" ")[0]);

                return Double.compare(aVal, bVal);
            }
        });

        addItem(data, "KDR", 86);
        slot++;
    }

    /**
     * Add the stat item to the inventory.
     *
     * @param data The data list for the stat.
     * @param statName The stat name.
     * @param slot The slot of the item.
     */
    private void addItem(List<String> data, String statName, int slot) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setDisplayName("§8» §6" + statName + " §8«");

        List<String> lore = new ArrayList<String>();
        lore.add(" ");

        int number = 1;

        for (int i = data.size() - 1; i >= data.size() - 10; i--) {
            String line;
            
            try {
                line = data.get(i);
            } catch (Throwable e) {
                continue;
            }

            double value = Double.parseDouble(line.split(" ")[0]);
            String name = line.split(" ")[1];
            
            String text;

            if (number == 10) {
                text = "§6#" + number + "§8 | §7" + name + " §8» §a";
            } else {
                if (number == 1) {
                    meta.setOwner(name);
                }

                text = " §6#" + number + "§8  | §7" + name + " §8» §a";
            }
            
            switch (statName.toLowerCase()) {
            case "fall damage":
            case "damage taken":
            case "pve damage dealt":
            case "pvp damage dealt":
                String sDamage = NumberUtils.makePercent(value);
                int iDamage = Integer.parseInt(sDamage.substring(2));
                
                text += NumberUtils.formatInt(iDamage) + "%";
                break;
            case "best parkour time":
                text += DateUtils.secondsToString((int) value);
                break;
            case "longest shot":
                text += NumberUtils.formatDouble(value) + " blocks";
                break;
            case "kdr":
                text += NumberUtils.formatDouble(value);
                break;
            default:
                text += NumberUtils.formatInt((int) value);
                break;
            }

            lore.add(text);
            
            number++;
        }

        lore.add(" ");
        meta.setLore(lore);

        item.setItemMeta(meta);
        
        if (slot >= (inv.getSize() - 9)) {
            invTwo.setItem(slot - 46, item);
        } else {
            inv.setItem(slot, item);
        }
    }
}