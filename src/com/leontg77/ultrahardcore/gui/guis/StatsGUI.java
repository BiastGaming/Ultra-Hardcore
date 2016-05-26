package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Stats inventory GUI class.
 * 
 * @author LeonTG77
 */
public class StatsGUI extends GUI implements Listener {

    public StatsGUI() {
        super("Stats", "A inventory containing stats for a user.");
    }

    private final Map<String, Inventory> inventories = new HashMap<String, Inventory>();

    @Override
    public void onSetup() {}

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        
        Inventory inv = event.getInventory();

        if (!inv.getTitle().endsWith("'s Stats")) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Get the stats inventory for the given user.
     *
     * @param user The stats owner.
     * @return The inventory.
     */
    public Inventory get(User user) {
        String name = user.getPlayer().getName();

        if (!inventories.containsKey(name)) {
            inventories.put(name, Bukkit.createInventory(user.getPlayer(), 27, "§4" + name + "'s Stats"));
            update(user);
        }

        return inventories.get(name);
    }

    /**
     * Update the stats inventory for the given user.
     *
     * @param user The inventory owner.
     */
    public void update(User user) {
        Inventory inv = inventories.get(user.getPlayer().getName());
        List<String> lore = new ArrayList<String>();

        glassify(inv);
        
        ItemStack general = new ItemStack(Material.SIGN);
        ItemMeta generalMeta = general.getItemMeta();
        generalMeta.setDisplayName("§8» §6General Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Games Played: §a" + user.getStat(Stat.GAMESPLAYED));
        lore.add("§8» §7Games Won: §a" + user.getStat(Stat.WINS));
        lore.add(" ");
        lore.add("§8» §7Hostile kills: §a" + user.getStat(Stat.HOSTILEMOBKILLS));
        lore.add("§8» §7Animal kills: §a" + user.getStat(Stat.ANIMALKILLS));
        String sDamage = NumberUtils.makePercent(user.getStatDouble(Stat.DAMAGETAKEN));
        int iDamage = Integer.parseInt(sDamage.substring(2));
        lore.add(" ");
        lore.add("§8» §7Damage taken: §a" + NumberUtils.formatInt(iDamage) + "%");
        lore.add("§8» §7Levels Earned: §a" + user.getStat(Stat.LEVELS));
        lore.add(" ");
        generalMeta.setLore(lore);
        general.setItemMeta(generalMeta);
        inv.setItem(10, general);
        lore.clear();
        
        ItemStack minigame = new ItemStack(Material.REDSTONE);
        ItemMeta minigameMeta = minigame.getItemMeta();
        minigameMeta.setDisplayName("§8» §6Minigame Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Highest Arena Killstreak: §a" + user.getStat(Stat.ARENAKILLSTREAK));
        lore.add(" ");
        lore.add("§8» §7Arena Kills: §a" + user.getStat(Stat.ARENAKILLS));
        lore.add("§8» §7Arena Deaths: §a" + user.getStat(Stat.ARENADEATHS));
        lore.add(" ");
        lore.add("§8» §7Best Parkour Time: §a" + user.getStat(Stat.BESTPARKOURTIME));
        lore.add("§8» §7The King Of the Ladder: §a" + user.getStat(Stat.TIMES_KOTL));
        lore.add(" ");
        minigameMeta.setLore(lore);
        minigame.setItemMeta(minigameMeta);
        inv.setItem(11, minigame);
        lore.clear();
        
        ItemStack pvp = new ItemStack(Material.IRON_SWORD);
        ItemMeta pvpMeta = pvp.getItemMeta();
        pvpMeta.setDisplayName("§8» §6PvP Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Highest Killstreak: §a" + user.getStat(Stat.KILLSTREAK));
        lore.add(" ");
        lore.add("§8» §7Kills: §a" + user.getStat(Stat.KILLS));
        lore.add("§8» §7Deaths: §a" + user.getStat(Stat.DEATHS));
        lore.add(" ");
        lore.add("§8» §7Longest Shot: §a" + user.getStatDouble(Stat.LONGESTSHOT) + " blocks");
        lore.add("§8» §7Arrows shot: §a" + user.getStat(Stat.ARROWSHOTS));
        lore.add(" ");
        lore.add("§8» §7Melee Hits: §a" + user.getStat(Stat.MELEEHITS));
        lore.add("§8» §7Bow Hits: §a" + user.getStat(Stat.BOWHITS));
        lore.add(" ");
        String pvpSDamage = NumberUtils.makePercent(user.getStatDouble(Stat.PVPDAMAGEDEALT));
        int pvpIDamage = Integer.parseInt(pvpSDamage.substring(2));
        String pveSDamage = NumberUtils.makePercent(user.getStatDouble(Stat.PVEDAMAGEDEALT));
        int pveIDamage = Integer.parseInt(pveSDamage.substring(2));
        lore.add("§8» §7PvP Damage Dealth: §a" + NumberUtils.formatInt(pvpIDamage) + "%");
        lore.add("§8» §7PvE Damage Dealth: §a" + NumberUtils.formatInt(pveIDamage) + "%");
        lore.add(" ");
        pvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pvpMeta.setLore(lore);
        pvp.setItemMeta(pvpMeta);
        inv.setItem(12, pvp);
        lore.clear();
        
        ItemStack mining = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta miningMeta = mining.getItemMeta();
        miningMeta.setDisplayName("§8» §6Mining Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Blocks Placed: §a" + user.getStat(Stat.PLACED));
        lore.add("§8» §7Blocks Mined: §a" + user.getStat(Stat.BLOCKS));
        lore.add(" ");
        lore.add("§8» §7Gold Mined: §a" + user.getStat(Stat.GOLD));
        lore.add("§8» §7Diamonds Mined: §a" + user.getStat(Stat.DIAMONDS));
        lore.add(" ");
        miningMeta.setLore(lore);
        miningMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mining.setItemMeta(miningMeta);
        inv.setItem(13, mining);
        lore.clear();
        
        ItemStack healing = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta healingMeta = healing.getItemMeta();
        healingMeta.setDisplayName("§8» §6Healing Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Hearts Healed: §a" + user.getStat(Stat.HEARTSHEALED));
        lore.add(" ");
        lore.add("§8» §7Golden Apples eaten: §a" + user.getStat(Stat.GOLDENAPPLESEATEN));
        lore.add("§8» §7Golden Heads eaten: §a" + user.getStat(Stat.GOLDENHEADSEATEN));
        lore.add(" ");
        healingMeta.setLore(lore);
        healing.setItemMeta(healingMeta);
        inv.setItem(14, healing);
        lore.clear();
        
        ItemStack nether = new ItemStack(Material.NETHER_STALK);
        ItemMeta netherMeta = nether.getItemMeta();
        netherMeta.setDisplayName("§8» §6Nether Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Travels to Nether: §a" + user.getStat(Stat.NETHER));
        lore.add(" ");
        lore.add("§8» §7Potions Drunk: §a" + user.getStat(Stat.POTIONS));
        lore.add("§8» §7Glowstone Mined: §a" + user.getStat(Stat.GLOWSTONE));
        lore.add(" ");
        netherMeta.setLore(lore);
        nether.setItemMeta(netherMeta);
        inv.setItem(15, nether);
        lore.clear();
        
        ItemStack other = new ItemStack(Material.NAME_TAG);
        ItemMeta otherMeta = other.getItemMeta();
        otherMeta.setDisplayName("§8» §6Other Stats §8«");
        lore.add(" ");
        lore.add("§8» §7Ocelots Tamed: §a" + user.getStat(Stat.CATSTAMED));
        lore.add("§8» §7Horses Tamed: §a" + user.getStat(Stat.HORSESTAMED));
        lore.add("§8» §7Wolves Tamed: §a" + user.getStat(Stat.WOLVESTAMED));
        lore.add(" ");
        lore.add("§8» §7Thrown Snowballs: §a" + user.getStat(Stat.SNOWBALLSHOTS));
        lore.add("§8» §7Thrown Eggs: §a" + user.getStat(Stat.EGGSTHROWN));
        lore.add("§8» §7Rod Uses: §a" + user.getStat(Stat.RODUSES));
        lore.add(" ");
        otherMeta.setLore(lore);
        other.setItemMeta(otherMeta);
        inv.setItem(16, other);
        lore.clear();
    }
}