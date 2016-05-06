package com.leontg77.ultrahardcore.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Config inventory GUI class.
 * 
 * @author LeonTG77.
 */
public class ConfigGUI extends GUI implements Listener {
	private final Settings settings;
	
	private final FeatureManager feat;
	private final GUIManager gui;
	
	/**
	 * Config inventory GUI class constructor.
	 * 
	 * @param settings The settings class.
	 * @param feat The feature manager class.
	 * @param gui The gui manager class.
	 */
	public ConfigGUI(Settings settings, FeatureManager feat, GUIManager gui) {
		super("Config", "A inventory to toggle options for a game.");
		
		this.settings = settings;
		
		this.feat = feat;
		this.gui = gui;
	}

	private Inventory inv;

	@Override
	public void onSetup() { // add items but glass first.
		inv = Bukkit.createInventory(null, 54, "§4Game configuration.");
		
		glassify(inv); 
		update();
	}

	/**
	 * Get the config inventory.
	 * 
	 * @return The inventory.
	 */
	public Inventory get() {
		return inv;
	}

	/**
	 * Update the config inventory.
	 */
	public void update() {
		for (ToggleableFeature feature : feat.getToggleableFeatures()) {
			int slot = feature.getInventorySlot();
			
			if (slot >= inv.getSize() || slot < 0) {
				continue;
			}
			
			inv.setItem(slot, feature.getToggleItem());
		}
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Inventory inv = event.getInventory();
		
		if (item == null) {
			return;
		}

		if (!this.inv.getTitle().equals(inv.getTitle())) {
			return;
		}

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}
		
		if (item.getItemMeta().getDisplayName().length() < 10) {
			return;
		}
		
		// the substring is just getting the name since theres some things before and after the name
		String name = item.getItemMeta().getDisplayName().substring(6, item.getItemMeta().getDisplayName().length() - 4);
		
		Feature feature = feat.getFeature(name);
		
		if (feature == null || !(feature instanceof ToggleableFeature)) {
			return;
		}
		
		ToggleableFeature toggle = (ToggleableFeature) feature;
		toggle.toggle(settings);

		ItemMeta meta = item.getItemMeta();
		meta.setLore(toggle.getToggleItem().getItemMeta().getLore());
		item.setItemMeta(meta);
		
		PlayerUtils.broadcast(Main.PREFIX + "§6" + name + " §7has been " + (toggle.isEnabled() ? "§aenabled" : "§cdisabled") + "§7.");
		
		gui.getGUI(GameInfoGUI.class).update();
	}
}