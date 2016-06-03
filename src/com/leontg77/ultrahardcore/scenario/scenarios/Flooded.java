package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.GeneratorScenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Flooded scenario class.
 * 
 * @author LeonTG77
 */
public class Flooded extends GeneratorScenario {

    public Flooded() {
        super(
            "Flooded", 
            "All air blocks between y32 -> y70 are filled with water. Everyone gets permanent water breathing & night vision, and start with a depth strider 3 book and a aqua affinity book, some sugar cane, and spawn eggs for a bunch of useful animals. It will also have permanent rain.", 
            ChatColor.AQUA, 
            "flood", 
            false, 
            false, 
            false, 
            true
        );
    }

    @Override
    public void handleBlock(Block block) {
        Location loc = block.getLocation();
        
        if (loc.getBlockY() < 30 || loc.getBlockY() > 70) {
            return;
        }

        if (block.getType() == Material.GRASS && loc.getBlockY() != 70) {
            block.setType(Material.DIRT);
        }

        if (!block.getType().isSolid() && !block.isLiquid()) {
            block.setType(Material.WATER);
        }
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        PotionEffect vision = new PotionEffect(PotionEffectType.NIGHT_VISION, NumberUtils.TICKS_IN_999_DAYS, 0);
        PotionEffect water = new PotionEffect(PotionEffectType.WATER_BREATHING, NumberUtils.TICKS_IN_999_DAYS, 0);

        ItemStack chicken = new ItemStack(Material.MONSTER_EGG, 12, (short) 93);
        ItemStack cow = new ItemStack(Material.MONSTER_EGG, 8, (short) 92);

        ItemStack cane = new ItemStack(Material.SUGAR_CANE, NumberUtils.randomIntBetween(3, 6));

        ItemStack depth = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) depth.getItemMeta();
        meta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, true);
        depth.setItemMeta(meta);

        ItemStack aqua = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta aquaMeta = (EnchantmentStorageMeta) aqua.getItemMeta();
        aquaMeta.addStoredEnchant(Enchantment.WATER_WORKER, 3, true);
        aqua.setItemMeta(aquaMeta);

        for (Player online : game.getPlayers()) {
            online.addPotionEffect(vision);
            online.addPotionEffect(water);

            PlayerUtils.giveItem(online, chicken, cow, cane, aqua, depth);
        }

        for (World world : game.getWorlds()) {
            if (world.getEnvironment() != Environment.NORMAL) {
                continue;
            }

            world.setThundering(false);
            world.setStorm(true);
        }
    }
}