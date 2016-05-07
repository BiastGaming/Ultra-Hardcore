package com.leontg77.ultrahardcore.utils;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.ImmutableSet;

/**
 * Block utilities class.
 * <p>
 * Contains block related methods.
 * 
 * @author LeonTG77
 */
public class BlockUtils {
	private static final Random rand = new Random();

	/**
	 * Display the block breaking sound and particles.
	 * 
	 * @param player The player who mined the block.
	 * @param block The block that was broken.
	 */
	@SuppressWarnings("deprecation")
	public static void blockBreak(final Player player, final Block block) {
		// Loop all players that are in the world.
		for (Player online : block.getWorld().getPlayers()) {
			// we do not want to display it to the breaker himself, that is done
			// automaticly.
			if (player != null && online == player) {
				continue;
			}

			// play the effect STEP_SOUND with the given block id at the blocks
			// location.
			online.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
		}
	}

	/**
	 * Drop the given item to drop on the given location as if a normal block
	 * break would drop it.
	 * 
	 * @param loc The location dropping at.
	 * @param toDrop The item dropping.
	 */
	public static void dropItem(final Location loc, final ItemStack toDrop) {
		// wait 2 ticks before dropping the item, because then the block won't push the item.
		new BukkitRunnable() {
			public void run() {
				// spawn item.
				Item item = loc.getWorld().dropItem(loc, toDrop);
				item.setVelocity(randomOffset());
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin("UHC"), 2);
	}

	/**
	 * Make the given players item in hand lose 1 durability point and break if
	 * out of durability.
	 * 
	 * @param player The item owner.
	 */
	public static void degradeDurabiliy(final Player player) {
		final ItemStack item = player.getItemInHand();

		// if the item is air, a bow or it's max durability is 0 (only
		// weapons/tools doesn't have it as 0) return.
		if (item.getType() == Material.AIR || item.getType() == Material.BOW || item.getType().getMaxDurability() == 0) {
			return;
		}

		short durability = item.getDurability();

		// incase of unbreaking enchantment.
		if (item.containsEnchantment(Enchantment.DURABILITY)) {
			double chance = (100 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1));

			if (rand.nextDouble() <= (chance / 100)) {
				durability++;
			}
		} else {
			durability++;
		}

		// if theres no more durability left, play the break sound, remove the
		// item and return.
		if (durability >= item.getType().getMaxDurability()) {
			player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
			player.setItemInHand(new ItemStack(Material.AIR));
			return;
		}

		item.setDurability(durability);
		player.setItemInHand(item);
	}

	/**
	 * Get a random offset for item dropping.
	 * 
	 * @return A vector with a random offset.
	 */
	private static Vector randomOffset() {
		// don't ask me for why these numbers, I was just testing different
		// onces and these seemed to work the best.
		double offsetX = rand.nextDouble() / 20;
		double offsetZ = rand.nextDouble() / 20;

		offsetX = offsetX - (rand.nextDouble() / 20);
		offsetZ = offsetZ - (rand.nextDouble() / 20);

		return new Vector(offsetX, 0.2, offsetZ);
	}

	/**
	 * Get the durability of the given block.
	 * 
	 * @param block The block checking.
	 * @return The durability of the block.
	 */
	public static int getDurability(final Block block) {
		return block.getState().getData().toItemStack().getDurability();
	}

	private static final int VEIN_LIMIT = 30;

	/**
	 * Get the ore vein at the given location.
	 * 
	 * @param loc The location to get it at.
	 * @return The list of the locations of the vein blocks, null if this wasn't an ore.
	 */
	public static void getVein(Block start, List<Block> vein) {
		if (vein.size() > VEIN_LIMIT) {
			return;
		}

		Block next = null;
		
		for (BlockFace face : ImmutableSet.of(BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
			next = start.getRelative(face);

			if (next.getType().equals(start.getType()) && !vein.contains(next)) {
				vein.add(next);
				getVein(next, vein);
			}
		}
	}
}