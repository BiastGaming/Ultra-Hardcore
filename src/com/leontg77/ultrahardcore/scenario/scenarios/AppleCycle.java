package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.rates.AppleRatesFeature;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.TreeUtils;
import com.leontg77.ultrahardcore.utils.TreeUtils.TreeType;

/**
 * AppleCycle scenario class.
 * 
 * @author LeonTG77
 */
public class AppleCycle extends Scenario implements Listener, CommandExecutor {
    private static final String PREFIX = "§cApple Cycle §8» §7";
    private static final double SAPLING_RATE = 0.05;

    private final FeatureManager feat;
    
    public AppleCycle(FeatureManager feat) {
        super("AppleCycle", "Every 10 minutes it will cycle to a new leaf block type, for the next 10 minutes only that leaf type will drop apples.");

        this.feat = feat;
        
        plugin.getCommand("appletree").setExecutor(this);
    }

    private BukkitRunnable task;
    private TreeType current;

    private int seconds = 600;

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }

        task = null;
        
        current = null;
        seconds = 600;
    }

    @Override
    public void onEnable() {
        seconds = 1;

        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            private Random rand = new Random();

            public void run() {
                seconds--;

                switch (seconds) {
                case 300:
                    PlayerUtils.broadcast(PREFIX + "Changing apple dropping tree in §a5§7 minutes!");
                    break;
                case 60:
                    PlayerUtils.broadcast(PREFIX + "Changing apple dropping tree in §a1§7 minute!");
                    break;
                case 30:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                    PlayerUtils.broadcast(PREFIX + "Changing apple dropping tree in §a" + seconds + "§7 seconds!");
                    break;
                case 1:
                    PlayerUtils.broadcast(PREFIX + "Changing apple dropping tree in §a1§7 second!");
                    break;
                case 0:
                    current = TreeType.values()[rand.nextInt(TreeType.values().length)];
                    PlayerUtils.broadcast(PREFIX + "§a" + NameUtils.capitalizeString(current.name(), true) + "§7 trees will now only drop apples!");

                    seconds = 600;
                    break;
                }
            }
        };

        task.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "AppleCycle is currenty disabled.");
            return true;
        }

        if (current == null) {
            sender.sendMessage(PREFIX + "§6No tree type has been set.");
            return true;
        }

        sender.sendMessage(PREFIX + "§a" + NameUtils.capitalizeString(current.name(), true) + "§7 trees will drop apples!");
        sender.sendMessage(PREFIX + "§7The apple tree type changes in §6" + DateUtils.secondsToString(seconds) + "§7.");
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // breaking leaves in creative shouldn't drop anything...
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
            return;
        }

        // this is just so I can cancel it
        // if it returns true it means I should cancel it and set the block to air.
        if (!handleLeafBreak(event)) {
            return;
        }

        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);

        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(LeavesDecayEvent event) {
        final Block block = event.getBlock();

        if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
            return;
        }

        // this is just so I can cancel it
        // if it returns true it means I should cancel it and set the block to air.
        if (!handleLeafBreak(event)) {
            return;
        }

        event.setCancelled(true);
        block.setType(Material.AIR);
    }

    /**
     * Handle a leaf break event, wether it's from decaying or a player breaking it.
     *
     * @param event The LeafDecayEvent or BlockBreakEvent that should trigger this.
     * @return True if the event that called this should be cancelled, false otherwise.
     */
    private boolean handleLeafBreak(BlockEvent event) {
        Block block = event.getBlock();
        int damage = BlockUtils.getDurability(block);

        TreeType tree = TreeUtils.getTree(block.getType(), damage);
        Random rand = new Random();

        if (tree == TreeType.UNKNOWN) {
            return false;
        }

        Location toDrop = block.getLocation().add(0.5, 0.7, 0.5);

        if (rand.nextDouble() < SAPLING_RATE) {
            BlockUtils.dropItem(toDrop, tree.getSapling());
        }

        if (tree != current) {
            return true;
        }

        if (rand.nextDouble() >= feat.getFeature(AppleRatesFeature.class).getAppleRates()) {
            return true;
        }

        BlockUtils.dropItem(toDrop, new ItemStack(Material.APPLE, 1));
        return true;
    }
}