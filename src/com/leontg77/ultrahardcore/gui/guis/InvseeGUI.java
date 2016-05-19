package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Invsee inventory GUI class.
 * 
 * @author LeonTG77
 */
public class InvseeGUI extends GUI implements Listener {
    private final Main plugin;

    /**
     * Invsee inventory GUI class constructor.
     *
     * @param plugin The main class.
     */
    public InvseeGUI(Main plugin) {
        super("Invsee", "A inventory for online players.");

        this.plugin = plugin;
    }

    private final Map<String, Inventory> inventories = new HashMap<String, Inventory>();

    @Override
    public void onSetup() {
        new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    update(online);
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        if (!inv.getTitle().endsWith("'s Inventory")) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Get the inventory for the given target.
     *
     * @param target The target to use.
     * @return Their inventory.
     */
    public Inventory get(Player target) {
        if (!inventories.containsKey(target.getName())) {
            inventories.put(target.getName(), Bukkit.createInventory(target, 54, "§4" + target.getName() + "'s Inventory"));
        }

        return inventories.get(target.getName());
    }

    /**
     * Update the inventory of the given target.
     *
     * @param target The target to use.
     */
    public void update(Player target) {
        Inventory inv = get(target);

        if (inv == null) {
            return;
        }

        inv.setItem(0, target.getInventory().getHelmet());
        inv.setItem(1, target.getInventory().getChestplate());
        inv.setItem(2, target.getInventory().getLeggings());
        inv.setItem(3, target.getInventory().getBoots());
        inv.setItem(5, target.getItemInHand());
        inv.setItem(6, target.getItemOnCursor());

        List<String> lore = new ArrayList<String>();

        ItemStack info = new ItemStack (Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§8» §6Player Info §8«");

        lore.add("§8» §7Name: §a" + target.getName());
        lore.add(" ");

        int health = (int) target.getHealth();

        lore.add("§8» §7Hearts: §6" + (((double) health) / 2) + "§4♥");
        lore.add("§8» §7Percent: §6" + NumberUtils.makePercent(target.getHealth()) + "%");
        lore.add(" ");
        lore.add("§8» §7Hunger: §6" + (((double) target.getFoodLevel()) / 2));
        lore.add("§8» §7XP level: §6" + target.getLevel());
        lore.add(" ");
        lore.add("§8» §7Location: §6x:" + target.getLocation().getBlockX() + ", y:" + target.getLocation().getBlockY() + ", z:" + target.getLocation().getBlockZ() + " (" + target.getWorld().getEnvironment().name().replaceAll("_", "").toLowerCase().replaceAll("normal", "overworld") + ")");
        lore.add(" ");
        lore.add("§8» §cPotion effects:");

        if (target.getActivePotionEffects().size() == 0) {
            lore.add("§8» §7None");
        }

        for (PotionEffect effects : target.getActivePotionEffects()) {
            lore.add("§8» §7P:§6" + NameUtils.getPotionName(effects.getType()) + " §7T:§6" + (effects.getAmplifier() + 1) + " §7D:§6" + DateUtils.ticksToString(effects.getDuration() / 20));
        }

        infoMeta.setLore(lore);
        info.setItemMeta(infoMeta);
        inv.setItem(8, info);
        lore.clear();

        for (int i = 9; i < 18; i++) {
            ItemStack glass = new ItemStack (Material.STAINED_GLASS_PANE, 1, (short) 15);
            ItemMeta glassMeta = glass.getItemMeta();

            glassMeta.setDisplayName("§0:>");
            glass.setItemMeta(glassMeta);

            inv.setItem(i, glass);
        }

        int i = 18;

        for (ItemStack item : target.getInventory().getContents()) {
            inv.setItem(i, item);
            i++;
        }

        for (HumanEntity human : inv.getViewers()) {
            ((Player) human).updateInventory();
        }
    }
}