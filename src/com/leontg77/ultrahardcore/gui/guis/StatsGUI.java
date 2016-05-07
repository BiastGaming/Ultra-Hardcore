package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
	
	/**
	 * Stats inventory GUI class constructor.
	 */
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
			inventories.put(name, Bukkit.createInventory(user.getPlayer(), InventoryType.HOPPER, "§4" + name + "'s Stats"));
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
		
		ItemStack general = new ItemStack (Material.SIGN);
		ItemMeta generalMeta = general.getItemMeta();
		
		generalMeta.setDisplayName("§8» §6General Stats §8«");
		lore.add(" ");
		lore.add("§8» §7Games played: §a" + user.getStat(Stat.GAMESPLAYED));
		lore.add("§8» §7Wins: §a" + user.getStat(Stat.WINS));
		lore.add(" ");
		lore.add("§8» §7Hostile kills: §a" + user.getStat(Stat.HOSTILEMOBKILLS));
		lore.add("§8» §7Animal kills: §a" + user.getStat(Stat.ANIMALKILLS));
		
		String sDamage = NumberUtils.makePercent(user.getStatDouble(Stat.DAMAGETAKEN));
		int iDamage = Integer.parseInt(sDamage.substring(2));
		
		lore.add("§8» §7Damage taken: §a" + NumberUtils.formatInt(iDamage) + "%");
		lore.add(" ");
		lore.add("§8» §7Longest Shot: §a" + NumberUtils.formatDouble(user.getStatDouble(Stat.LONGESTSHOT)));
		lore.add("§8» §7Levels Earned: §a" + user.getStat(Stat.LEVELS));
		lore.add(" ");
		
		generalMeta.setLore(lore);
		general.setItemMeta(generalMeta);
		inv.setItem(0, general);
		lore.clear();
		
		ItemStack pvpmining = new ItemStack (Material.DIAMOND_AXE);
		ItemMeta pvpminingMeta = pvpmining.getItemMeta();
		
		pvpminingMeta.setDisplayName("§8» §6PvP & Mining Stats §8«");
		lore.add(" ");
		lore.add("§8» §7Highest Arena Killstreak: §a" + user.getStat(Stat.ARENAKILLSTREAK));
		lore.add("§8» §7Highest Killstreak: §a" + user.getStat(Stat.KILLSTREAK));
		lore.add(" ");
		lore.add("§8» §7Kills: §a" + user.getStat(Stat.KILLS));
		lore.add("§8» §7Deaths: §a" + user.getStat(Stat.DEATHS));
		
		double kdr;
		
		if (user.getStat(Stat.DEATHS) == 0) {
			kdr = (double) user.getStat(Stat.KILLS);
		} else {
			kdr = ((double) user.getStat(Stat.KILLS)) / ((double) user.getStat(Stat.DEATHS));
		}
		
		lore.add("§8» §7KDR: §a" + NumberUtils.formatDouble(kdr));
		lore.add(" ");
		lore.add("§8» §7Diamonds mined: §a" + user.getStat(Stat.DIAMONDS));
		lore.add("§8» §7Gold mined: §a" + user.getStat(Stat.GOLD));
		lore.add(" ");
		lore.add("§8» §7Arena Kills: §a" + user.getStat(Stat.ARENAKILLS));
		lore.add("§8» §7Arena Deaths: §a" + user.getStat(Stat.ARENADEATHS));
		
		double arenakdr;
		
		if (user.getStat(Stat.ARENADEATHS) == 0) {
			arenakdr = (double) user.getStat(Stat.ARENAKILLS);
		} else {
			arenakdr = ((double) user.getStat(Stat.ARENAKILLS)) / ((double) user.getStat(Stat.ARENADEATHS));
		}
		
		lore.add("§8» §7Arena KDR: §a" + NumberUtils.formatDouble(arenakdr));
		lore.add(" ");
		pvpminingMeta.setLore(lore); 
		pvpminingMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		pvpminingMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		pvpmining.setItemMeta(pvpminingMeta);
		inv.setItem(2, pvpmining);
		lore.clear();
		
		ItemStack misc = new ItemStack (Material.NETHER_STALK);
		ItemMeta miscMeta = misc.getItemMeta();
		
		miscMeta.setDisplayName("§8» §6Misc Stats §8«");
		lore.add(" ");
		lore.add("§8» §7Golden Apples eaten: §a" + user.getStat(Stat.GOLDENAPPLESEATEN));
		lore.add("§8» §7Golden Heads eaten: §a" + user.getStat(Stat.GOLDENHEADSEATEN));
		lore.add("§8» §7Potions drunk: §a" + user.getStat(Stat.POTIONS));
		lore.add(" ");
		lore.add("§8» §7Nethers entered: §a" + user.getStat(Stat.NETHER));
		lore.add("§8» §7Ends entered: §a" + user.getStat(Stat.END));
		lore.add(" ");
		lore.add("§8» §7Horses tamed: §a" + user.getStat(Stat.HORSESTAMED));
		lore.add("§8» §7Wolves tamed: §a" + user.getStat(Stat.WOLVESTAMED));
		lore.add(" ");
		
		miscMeta.setLore(lore);
		misc.setItemMeta(miscMeta);
		inv.setItem(4, misc);
		lore.clear();
	}
}