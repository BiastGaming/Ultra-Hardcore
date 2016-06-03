package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.GeneratorScenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * GlassWorld scenario class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GlassWorld extends GeneratorScenario {

    public GlassWorld() {
        super(
            "GlassWorld", 
            "All grass blocks are replaced with green glass, all dirt blocks are replaced with brown glass blocks, all stone is replaced with gray glass, sand is replaced with yellow glass, nether rack is replaced with red glass and all bedrock is replaced by black glass, mining the glass still gives the dirt/cobblestone it should however the bedrock glass is not breakable. Leaves will also drop sugar canes.",
            ChatColor.GOLD,
            "glassify",
            false,
            false,
            false,
            true
        );
    }

    @Override
    public void handleBlock(Block block) {
        switch (block.getType()) {
        case STONE:
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 8);
            break;
        case BEDROCK:
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 15);
            break;
        case DIRT:
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 12);
            break;
        case NETHERRACK:
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 14);
            break;
        case SAND:
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 4);
            break;
        case GRASS:
            block.setType(Material.STAINED_GLASS);
            block.setData((byte) 13);
            break;
        default:
            break;
        }
    }

    @EventHandler
    public void on(LeavesDecayEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Block block = event.getBlock();
        Location loc = block.getLocation().add(0.5, 0.7, 0.5);

        if (rand.nextInt(100) < 2) {
            BlockUtils.dropItem(loc, new ItemStack(Material.SUGAR_CANE, 1 + rand.nextInt(1)));
        }
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!game.getPlayers().contains(player) || player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        
        if (block.getType() != Material.STAINED_GLASS) {
            return;
        }
        
        Location toDrop = block.getLocation().clone().add(0.5, 0.7, 0.5);
        ItemStack item = player.getItemInHand();
        
        switch (BlockUtils.getDurability(block)) {
        case 15:
            player.sendMessage(PREFIX + "Hi I am bedrock, You can't mine me even if you wanted to!");
            event.setCancelled(true);
            break;
        case 8:
            if (item != null && item.containsEnchantment(Enchantment.SILK_TOUCH)) {
                BlockUtils.dropItem(toDrop, new ItemStack(Material.STONE));
            } else {
                BlockUtils.dropItem(toDrop, new ItemStack(Material.COBBLESTONE));
            }
            break;
        case 12:
            BlockUtils.dropItem(toDrop, new ItemStack(Material.DIRT));
            break;
        case 14:
            BlockUtils.dropItem(toDrop, new ItemStack(Material.NETHERRACK));
            break;
        case 4:
            BlockUtils.dropItem(toDrop, new ItemStack(Material.SAND));
            break;
        case 13:
            if (item != null && item.containsEnchantment(Enchantment.SILK_TOUCH)) {
                BlockUtils.dropItem(toDrop, new ItemStack(Material.GRASS));
            } else {
                BlockUtils.dropItem(toDrop, new ItemStack(Material.DIRT));
            }
            break;
        }
    }
}