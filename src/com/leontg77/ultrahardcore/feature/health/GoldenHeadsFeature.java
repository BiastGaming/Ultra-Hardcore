package com.leontg77.ultrahardcore.feature.health;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.scenario.scenarios.AchievementHunters;
import com.leontg77.ultrahardcore.scenario.scenarios.VengefulSpirits;

/**
 * Golden head feature class.
 * 
 * @author LeonTG77
 */
public class GoldenHeadsFeature extends ToggleableFeature implements Listener {
	private static final String HEAD_NAME = ChatColor.GOLD + "Golden Head";
    
	private static final int TICKS_PER_HALF_HEART = 25;
	private static final short PLAYER_HEAD_DATA = 3;
	
	private static final ItemStack PLAYER_SKULL = new ItemStack(Material.SKULL_ITEM, 1, PLAYER_HEAD_DATA);

	private final Settings settings;
	private final Main plugin;

	private final AchievementHunters ach;
	private final VengefulSpirits spirit;
	
	private final Arena arena;
	private final Game game;
	
    private int healAmount;

	public GoldenHeadsFeature(Main plugin, Settings settings, Arena arena, Game game, AchievementHunters ach, VengefulSpirits spirit) {
		super("Golden Heads", "Crafted like a golden apple just with a head in the middle and heals more hearts.");

		final ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1))
        	.shape("AAA", "ABA", "AAA")
        	.setIngredient('A', Material.GOLD_INGOT)
        	.setIngredient('B', PLAYER_SKULL.getData());

		Bukkit.addRecipe(recipe);	
		
		healAmount = settings.getConfig().getInt("feature." + getName().toLowerCase() + ".heal", 8);
		
		icon.setType(Material.SKULL_ITEM);
		icon.setDurability(PLAYER_HEAD_DATA);
		
		slot = 1;
		
		this.plugin = plugin;
		this.settings = settings;

		this.spirit = spirit;
		this.ach = ach;
		
		this.arena = arena;
		this.game = game;
	}

	/**
	 * Set the amount of hearts heads should heal.
	 * 
	 * @param headheals The amount of hearts.
	 */
    public void setHealAmount(final double headheals) {
    	this.healAmount = (int) (headheals * 2);
        
        settings.getConfig().set("feature." + getName().toLowerCase() + ".heal", healAmount);
        settings.saveConfig();
    }

    /**
     * Get the amount of hearts heads heal.
     * 
     * @return Heal amount.
     */
    public double getHealAmount() {
        return ((double) healAmount) / 2;
    }

	@EventHandler
	public void on(PlayerDeathEvent event) {
        if (!isEnabled()) {
        	return;
        }

		final Player player = event.getEntity();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 
	    
	    if (!State.isState(State.INGAME) || !game.getWorlds().contains(player.getWorld())) {
	    	return;
	    }
		
		// incase an explotion, wait a tick.
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				Block block = player.getLocation().getBlock();
				
				block.setType(Material.NETHER_FENCE);
				block = block.getRelative(BlockFace.UP);
				block.setType(Material.SKULL);
			    
				Skull skull;
				
				try {
			        skull = (Skull) block.getState();
				} catch (Exception e) {
					// the skull wasn't placed (outside of the world probs), tell the console so and stop.
					Bukkit.getLogger().warning("Could not place player skull.");
					return;
				}
				
			    skull.setSkullType(SkullType.PLAYER);
			    skull.setRotation(getBlockDirection(player.getLocation()));
			    skull.setOwner(player.getName());
			    skull.update();
			    
			    block.setData((byte) 0x1, true);
			}
		}.runTaskLater(plugin, 1);
	}

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
    	// heads should be useable if vengeful spirits or achievement hunters is enabled.
        if (!isEnabled() && !spirit.isEnabled() && !ach.isEnabled()) {
        	return;
        }
        
    	Player player = event.getPlayer();
    	ItemStack item = event.getItem();

        if (!isGoldenHead(item)) {
        	return;
        }
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, TICKS_PER_HALF_HEART * healAmount, 1));
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
    	CraftingInventory inv = event.getInventory();
    	Recipe recipe = event.getRecipe();
    	
        if (recipe.getResult().getType() != Material.GOLDEN_APPLE) {
        	return;
        }

        ItemStack centre = inv.getMatrix()[4];

        if (centre == null || centre.getType() != Material.SKULL_ITEM) {
        	return;
        }

    	// heads should be craftable if vengeful spirits or achievement hunters is enabled.
        if (!isEnabled() && !spirit.isEnabled() && !ach.isEnabled()) {
        	inv.setResult(new ItemStack(Material.AIR));
            return;
        }

        SkullMeta meta = (SkullMeta) centre.getItemMeta();
        inv.setResult(getGoldenHeadItem(meta.hasOwner() ? meta.getOwner() : "Manually Crafted"));
    }

    /**
     * Get the golden head item by the given player name.
     * 
     * @param name The owner of the golden head.
     * @return The head ItemStack.
     */
    public ItemStack getGoldenHeadItem(String name) {
    	ItemStack item = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(HEAD_NAME);

        // add lore
        meta.setLore(ImmutableList.of(
                "Some say consuming the head of a",
                "fallen foe strengthens the blood",
                ChatColor.AQUA + "Made from the head of: " + name
        ));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Check if the given ItemStack is a golden head.
     * 
     * @param item The item checking.
     * @return True if it has the same name and a lore, false otherwise.
     */
    public boolean isGoldenHead(ItemStack item) {
        if (item.getType() != Material.GOLDEN_APPLE) {
        	return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta.hasLore() && meta.hasDisplayName() && meta.getDisplayName().equals(HEAD_NAME);
    }
	
	/**
	 * Get the block face direction bases on the given locations yaw.
	 * 
	 * @param loc the location.
	 * @return the block face.
	 */
	public static BlockFace getBlockDirection(Location loc) {
        double rotation = (loc.getYaw() + 180) % 360;
        
        if (rotation < 0) {
            rotation += 360.0;
        }
        
        if (0 <= rotation && rotation < 11.25) {
            return BlockFace.NORTH;
        } 
        else if (11.25 <= rotation && rotation < 33.75) {
            return BlockFace.NORTH_NORTH_EAST;
        } 
        else if (33.75 <= rotation && rotation < 56.25) {
            return BlockFace.NORTH_EAST;
        } 
        else if (56.25 <= rotation && rotation < 78.75) {
            return BlockFace.EAST_NORTH_EAST;
        } 
        else if (78.75 <= rotation && rotation < 101.25) {
            return BlockFace.EAST;
        } 
        else if (101.25 <= rotation && rotation < 123.75) {
            return BlockFace.EAST_SOUTH_EAST;
        } 
        else if (123.75 <= rotation && rotation < 146.25) {
            return BlockFace.SOUTH_EAST;
        } 
        else if (146.25 <= rotation && rotation < 168.75) {
            return BlockFace.SOUTH_SOUTH_EAST;
        } 
        else if (168.75 <= rotation && rotation < 191.25) {
            return BlockFace.SOUTH;
        } 
        else if (191.25 <= rotation && rotation < 213.75) {
            return BlockFace.SOUTH_SOUTH_WEST;
        } 
        else if (213.75 <= rotation && rotation < 236.25) {
            return BlockFace.SOUTH_WEST;
        } 
        else if (236.25 <= rotation && rotation < 258.75) {
            return BlockFace.WEST_SOUTH_WEST;
        } 
        else if (258.75 <= rotation && rotation < 281.25) {
            return BlockFace.WEST;
        } 
        else if (281.25 <= rotation && rotation < 303.75) {
            return BlockFace.WEST_NORTH_WEST;
        } 
        else if (303.75 <= rotation && rotation < 326.25) {
            return BlockFace.NORTH_WEST;
        } 
        else if (326.25 <= rotation && rotation < 348.75) {
            return BlockFace.NORTH_NORTH_WEST;
        } 
        else if (348.75 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } 
        else {
            return null;
        }
    }
}