package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * EnchantParanoia scenario class
 * 
 * @author LeonTG77
 */
public class EnchantParanoia extends Scenario implements Listener {
    private BukkitRunnable task;

    public EnchantParanoia() {
        super("EnchantParanoia", "You cannot see what enchants you have §4§lNOT FINISHED!");
    }

    @Override
    public void onDisable() {
        task.cancel();

//        EnchantPreview.disable();
    }

    @Override
    public void onEnable() {
//        EnchantPreview.enable();

        task = new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    for (ItemStack contents : online.getOpenInventory().getBottomInventory().getContents()) {
                        if (contents == null) {
                            continue;
                        }

                        if (!(contents.getItemMeta() instanceof EnchantmentStorageMeta)) {
                            continue;
                        }

                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) contents.getItemMeta();

                        ArrayList<String> lore = new ArrayList<String>();

                        for (int i = 0; i < meta.getEnchants().size(); i++) {
                            lore.add("§7Who knows...?");
                        }

                        meta.setLore(lore);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        contents.setItemMeta(meta);
                    }

                    for (ItemStack contents : online.getOpenInventory().getTopInventory().getContents()) {
                        if (contents == null) {
                            continue;
                        }

                        if (!(contents.getItemMeta() instanceof EnchantmentStorageMeta)) {
                            continue;
                        }

                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) contents.getItemMeta();

                        ArrayList<String> lore = new ArrayList<String>();

                        for (int i = 0; i < meta.getStoredEnchants().size(); i++) {
                            lore.add("§7Who knows...?");
                        }

                        meta.setLore(lore);
                        meta.addItemFlags(ItemFlag.values());
                        contents.setItemMeta(meta);
                    }

                    for (ItemStack contents : online.getInventory().getContents()) {
                        if (contents == null) {
                            continue;
                        }

                        if (!(contents.getItemMeta() instanceof EnchantmentStorageMeta)) {
                            continue;
                        }

                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) contents.getItemMeta();

                        ArrayList<String> lore = new ArrayList<String>();

                        for (int i = 0; i < meta.getStoredEnchants().size(); i++) {
                            lore.add("§7Who knows...?");
                        }

                        meta.setLore(lore);
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        contents.setItemMeta(meta);
                    }
                }
            }
        };

//        task.runTaskTimer(Main.plugin, 20, 20);
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<String>();

        for (int i = 0; i < event.getEnchantsToAdd().size(); i++) {
            lore.add("§7Who knows...?");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        ItemStack contents = event.getCurrentItem();

        if (contents == null) {
            return;
        }

        if (!(contents.getItemMeta() instanceof EnchantmentStorageMeta)) {
            return;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) contents.getItemMeta();

        ArrayList<String> lore = new ArrayList<String>();

        for (int i = 0; i < meta.getStoredEnchants().size(); i++) {
            lore.add("§7Who knows...?");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.values());

        PlayerUtils.broadcast(lore + " |L");
        PlayerUtils.broadcast(meta.getItemFlags() + " |IF");
        PlayerUtils.broadcast(meta.getEnchants() + " |E");
        PlayerUtils.broadcast(meta.getStoredEnchants() + " |SE");
        PlayerUtils.broadcast(meta.getLore() + " |LORE");

        contents.setItemMeta(meta);
    }
}