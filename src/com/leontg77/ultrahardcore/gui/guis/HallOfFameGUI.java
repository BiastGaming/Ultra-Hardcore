package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * HallOfFame inventory GUI class.
 * 
 * @author LeonTG77
 */
public class HallOfFameGUI extends GUI implements Listener {
	private final Settings settings;
	private final Main plugin;
	
	/**
	 * HallOfFame inventory GUI class constructor.
	 * 
	 * @param settings The settings class.
	 */
	public HallOfFameGUI(Main plugin, Settings settings) {
		super("HOF", "A inventory with all games of hosts and it's winners and game information.");
		
		this.settings = settings;
		this.plugin = plugin;
	}

	private final Map<String, Map<Integer, Inventory>> hostInvs = new HashMap<String, Map<Integer, Inventory>>();
	
	public final Map<String, Integer> currentPage = new HashMap<String, Integer>();
	public final Map<String, String> currentHost = new HashMap<String, String>();

	@Override
	public void onSetup() {
		for (String host : settings.getHOF().getKeys(false)) {
			update(host);
		}
	}
	
	@EventHandler
    public void on(InventoryClickEvent event) {	
		if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();

		ItemStack item = event.getCurrentItem();
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().contains("'s HoF, Page")) {
			return;
		}
			
		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}
		
		String host = currentHost.get(player.getName());
		int page = currentPage.get(player.getName());
		
		Map<Integer, Inventory> pages = hostInvs.get(host);
		
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aNext page")) {
			page++;
			
			if (!pages.containsKey(page)) {
				return;
			}
			
			currentPage.put(player.getName(), page);
			player.openInventory(pages.get(page));
		}
		
		if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§aPrevious page")) {
			page--;
			
			if (!pages.containsKey(page)) {
				return;
			}
			
			currentPage.put(player.getName(), page);
			player.openInventory(pages.get(page));
		}
		
		event.setCancelled(true);
	}
	
	/**
	 * Get the hof inventory for the given host.
	 * 
	 * @param user The host.
	 * @return The inventory.
	 */
	public Inventory get(String host) {
		return get(host, 1);
	}
	
	/**
	 * Get the hof inventory for the given host with a page number.
	 * 
	 * @param user The host.
	 * @param page The page to get.
	 * @return The inventory.
	 */
	public Inventory get(String host, int page) {
		return hostInvs.get(host).get(page);
	}

	/**
	 * Update the hof inventory for the given host.
	 * 
	 * @param host The host.
	 */
	public void update(String host) {
		Set<String> keys = settings.getHOF().getConfigurationSection(host + ".games").getKeys(false);
		List<String> list = new ArrayList<String>(keys);
		
		Inventory inv = null;
		
		int pages = ((list.size() / 28) + 1);
		
		hostInvs.put(host, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			if (hostInvs.get(host).containsKey(current)) {
				inv = hostInvs.get(host).get(current);
			} else {
				inv = Bukkit.createInventory(null, 54, "§4" + host + "'s HoF, Page " + current);
				hostInvs.get(host).put(current, inv);
			}
			
			for (int slot = 0; slot < 35; slot++) {
				if (list.isEmpty()) {
					continue;
				}
				
				if (isSide(slot)) {
					continue;
				}
				
				String target = list.remove(0);
				boolean isSpecial = target.endsWith("50") || target.endsWith("00") || target.endsWith("25") || target.endsWith("75");
				
				String path = host + ".games." + target;
				
				ItemStack item = new ItemStack (Material.GOLDEN_APPLE, 1, isSpecial ? (short) 1 : (short) 0);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§8» §6" + host + "'s #" + target + " §8«");
				
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§7" + settings.getHOF().getString(host + ".games." + target + ".date", "N/A"));
				lore.add(" ");
				lore.add("§8» §cWinners:");
				
				for (String winners : settings.getHOF().getStringList(path + ".winners")) {
					lore.add("§8» §7" + winners);
				}
				
				lore.add(" ");
				lore.add("§8» §cKills:");
				lore.add("§8» §7" + settings.getHOF().getString(host + ".games." + target + ".kills", "-1"));
				
				String teamsize = settings.getHOF().getString(host + ".games." + target + ".teamsize", "FFA");
				
				if (!teamsize.isEmpty()) {
					lore.add(" ");
					lore.add("§8» §cTeamsize:");
					lore.add("§8» §7" + teamsize);
				}
				
				lore.add(" ");
				lore.add("§8» §cScenario:");
				
				for (String scenario : settings.getHOF().getString(host + ".games." + target + ".scenarios", "Vanilla+").split(", ")) {
					lore.add("§8» §7" + scenario);
				}
				
				lore.add(" ");
				meta.setLore(lore);
				item.setItemMeta(meta);
				inv.setItem(slot, item);
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
			
			String name = settings.getHOF().getString(host + ".name", host);
			
			ItemStack head = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta headMeta = (SkullMeta) head.getItemMeta();
			headMeta.setDisplayName("§8§ §6Host Info §8«");
			headMeta.setOwner(name);
			
			ArrayList<String> headLore = new ArrayList<String>();
			headLore.add(" ");
			headLore.add("§8» §7Total games hosted: §6" + settings.getHOF().getConfigurationSection(host + ".games").getKeys(false).size());
			
			try {
				headLore.add("§8» §7Rank: §6" + NameUtils.capitalizeString(plugin.getUser(PlayerUtils.getOfflinePlayer(name)).getRank().name(), false));
			} catch (Exception e) {
				headLore.add("§8» §7Rank: §6This host has never joined the server.");
			}
			
			headLore.add(" ");
			headLore.add("§8» §7Host name: §6" + host);
			headLore.add("§8» §7IGN: §6" + name);
			headLore.add(" ");
			headMeta.setLore(headLore);
			head.setItemMeta(headMeta);
			
			inv.setItem(49, head);
			
			if (current != 1) {
				inv.setItem(47, prevpage);
			}
			
			if (current != pages) {
				inv.setItem(51, nextpage);
			}
		}
	}
}