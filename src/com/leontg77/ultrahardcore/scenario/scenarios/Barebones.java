package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Barebones scenario class
 * 
 * @author LeonTG77
 */
public class Barebones extends Scenario implements Listener {
    private final CutClean cc;
    private final Game game;

    public Barebones(Game game, CutClean cc) {
        super("Barebones", "The Nether is disabled, and iron is the highest tier you can obtain through gearing up. When a player dies, they will drop 1 diamond, 1 golden apple, 32 arrows, and 2 string. You cannot craft an enchantment table, anvil, or golden apple. Mining any ore except coal or iron will drop an iron ingot.");

        this.game = game;
        this.cc = cc;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!game.getWorlds().contains(block.getWorld())) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ItemStack replaced = new ItemStack(cc.isEnabled() ? Material.IRON_INGOT : Material.IRON_ORE);

        switch (block.getType()) {
        case EMERALD_ORE:
        case REDSTONE_BLOCK:
        case GLOWING_REDSTONE_ORE:
        case LAPIS_ORE:
        case GOLD_ORE:
        case DIAMOND_ORE:
            break;
        default:
            return;
        }

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);
        BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), replaced);
        
        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        List<ItemStack> drops = event.getDrops();

        drops.add(new ItemStack(Material.GOLDEN_APPLE, 1));
        drops.add(new ItemStack(Material.DIAMOND, 1));
        drops.add(new ItemStack(Material.STRING, 2));
        drops.add(new ItemStack(Material.ARROW, 32));
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        final ItemStack item = event.getCurrentItem();

        if (item != null && item.getType() != Material.ANVIL && item.getType() != Material.GOLDEN_APPLE && item.getType() != Material.ENCHANTMENT_TABLE) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't craft " + item.getType().name().toLowerCase().replace("_", " ") + "s.");
        event.setCancelled(true);
    }
}