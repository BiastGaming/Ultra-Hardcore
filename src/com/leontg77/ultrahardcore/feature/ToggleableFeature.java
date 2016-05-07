package com.leontg77.ultrahardcore.feature;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.Settings;

/**
 * A toggleable feature is a feature you can disable and enable.
 * 
 * @author LeonTG77
 */
public abstract class ToggleableFeature extends Feature {
	protected boolean enabled = false;
	
	protected ItemStack icon = new ItemStack(Material.BARRIER);
	protected int slot = 0;
	
	/**
	 * ToggleableFeature constructor.
	 * 
	 * @param name The name of the feature.
	 * @param description A short description of the feature.
	 * @param toggleItem The item to be used as a toggle item.
	 * @param invSlot The slot for the toggle item.
	 */
	public ToggleableFeature(String name, String description) {
		super(name, description);
	}

	/**
	 * Called when the feature disables.
	 */
	public void onDisable() {}
	
	/**
	 * Called when the feature enables.
	 */
	public void onEnable() {}
	
	/**
	 * Enable the feature.
	 * 
	 * @return True if successful, false otherwise.
	 */
	public boolean enable(Settings settings) {
		if (isEnabled()) {
			return false;
		}
		
		settings.getConfig().set("feature." + getName().toLowerCase() + ".enabled", true);
		settings.saveConfig();
		
		this.enabled = true;
		onEnable();
		return true;
	}
	
	/**
	 * Disable the feature.
	 * 
	 * @return True if successful, false otherwise.
	 */
	public boolean disable(Settings settings) {
		if (!isEnabled()) {
			return false;
		}
		
		settings.getConfig().set("feature." + getName().toLowerCase() + ".enabled", false);
		settings.saveConfig();
		
		this.enabled = false;
		onDisable();
		return true;
	}    
	
	/**
	 * Toggle the scenario.
	 * 
	 * @return True if successful, false otherwise.
	 */
	public boolean toggle(Settings settings) {
		return isEnabled() ? disable(settings) : enable(settings);
	} 
	
	/**
	 * Check if the feature is enabled
	 * 
	 * @return True if enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Get the item to be used for the config inventory for this option.
	 * 
	 * @return The item to use.
	 */
	public ItemStack getToggleItem() {
		final ItemMeta meta = icon.getItemMeta();
		
		final String currentDesc = getDescription();
		String newDesc = "";
		
		int i = 0;
		
		for (String split : currentDesc.split(" ")) {
			if (i >= 3 && split.length() > 3) {
				newDesc += split + ";";
				i = 0;
			} else {
				newDesc += split + " ";
				i++;
			}
		}
		
		final List<String> lore = new ArrayList<String>();

		lore.add(" ");
		lore.add("§8» §7Currently: " + (isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
		lore.add(" ");
		lore.add("§8» §cDescription:");
		
		for (String split : newDesc.split(";")) {
			lore.add("§8» §7" + split);
		}
		
		lore.add(" ");
		
		meta.setDisplayName("§8» §6" + getName() + " §8«");
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
		icon.setItemMeta(meta);
		
		return icon;
	}
	
	/**
	 * Get the slot for the toggle item.
	 * 
	 * @return The slot for the item to be placed in.
	 */
	public int getInventorySlot() {
		return slot;
	}
}