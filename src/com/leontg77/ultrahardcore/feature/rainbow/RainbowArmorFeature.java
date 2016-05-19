package com.leontg77.ultrahardcore.feature.rainbow;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Rainbow Armor feature.
 * 
 * @author LeonTG77
 */
public class RainbowArmorFeature extends Feature {
    private BukkitRunnable task;

    public RainbowArmorFeature(final Main plugin) {
        super("Rainbow Armor", "Makes all players wearing leather armor change colors in a rainbow way.");

        task = new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    PlayerInventory inv = online.getInventory();

                    ItemStack hemlet = inv.getHelmet();
                    ItemStack chestplate = inv.getChestplate();
                    ItemStack leggings = inv.getLeggings();
                    ItemStack boots = inv.getBoots();

                    if (hemlet != null && hemlet.getType() == Material.LEATHER_HELMET) {
                        inv.setHelmet(rainbowArmor(online, hemlet));
                    }

                    if (chestplate != null && chestplate.getType() == Material.LEATHER_CHESTPLATE) {
                        inv.setChestplate(rainbowArmor(online, chestplate));
                    }

                    if (leggings != null && leggings.getType() == Material.LEATHER_LEGGINGS) {
                        inv.setLeggings(rainbowArmor(online, leggings));
                    }

                    if (boots != null && boots.getType() == Material.LEATHER_BOOTS) {
                        inv.setBoots(rainbowArmor(online, boots));
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
    }

    /**
     * Change the color of the given type to a rainbow color.
     *
     * @param player the players armor.
     * @param type the type.
     * @return The new colored leather armor.
     */
    private ItemStack rainbowArmor(Player player, ItemStack item) {
        final LeatherArmorMeta lMeta = (LeatherArmorMeta) item.getItemMeta();

        int blue = lMeta.getColor().getBlue();
        int green = lMeta.getColor().getGreen();
        int red = lMeta.getColor().getRed();

        if (lMeta.getColor() == Bukkit.getItemFactory().getDefaultLeatherColor()) {
            blue = 0;
            green = 0;
            red = 255;
        }

        boolean changed = false;

        if (red == 255 && blue == 0) {
            green++;
            changed = true;
        }

        if (green == 255 && blue == 0) {
            red--;
            changed = true;
        }

        if (green == 255 && red == 0) {
            blue++;
            changed = true;
        }

        if (blue == 255 && red == 0) {
            green--;
            changed = true;
        }

        if (green == 0 && blue == 255) {
            red++;
            changed = true;
        }

        if (green == 0 && red == 255) {
            blue--;
            changed = true;
        }

        if (!changed) {
            blue = 0;
            green = 0;
            red = 255;
        }

        final ItemStack armor = new ItemStack (item.getType(), item.getAmount(), item.getDurability());
        final LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setColor(Color.fromBGR(blue, green, red));
        meta.setDisplayName(item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null);
        meta.setLore(item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<String>());

        for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
            meta.addEnchant(ench.getKey(), ench.getValue(), true);
        }

        armor.setItemMeta(meta);
        return armor;
    }
}