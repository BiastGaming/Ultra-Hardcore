package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BloodCycle scenario class.
 * 
 * @author LeonTG77
 */
public class BloodCycle extends Scenario implements Listener, CommandExecutor {

    public BloodCycle() {
        super("BloodCycle", "Every 10 minutes it will cycle to a new ore, these ores consist of Emerald, Diamond, Gold, Iron, Coal, Lapis, and Redstone. If it cycles to one of these ores, when you mine it; it has a chance of damaging you by half-a-heart, but you could it lucky and it will cycle to no ore.");

        plugin.getCommand("ore").setExecutor(this);
    }

    private static final List<Material> ORES = ImmutableList.of(
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE,
            Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE
    );

    private static final String PREFIX = "§4§lBloodCycle §8» §7";

    private BukkitRunnable task;
    private Material current;

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
                    PlayerUtils.broadcast(PREFIX + "Changing damaging ore in §a5§7 minutes!");
                    break;
                case 60:
                    PlayerUtils.broadcast(PREFIX + "Changing damaging ore in §a1§7 minute!");
                    break;
                case 30:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                    PlayerUtils.broadcast(PREFIX + "Changing damaging ore in §a" + seconds + "§7 seconds!");
                    break;
                case 1:
                    PlayerUtils.broadcast(PREFIX + "Changing damaging ore in §a1§7 second!");
                    break;
                case 0:
                    current = ORES.get(rand.nextInt(ORES.size()));
                    PlayerUtils.broadcast(PREFIX + "§a" + NameUtils.capitalizeString(current.name(), true) + "§7 now has a chance of damaging you!");

                    seconds = 600;
                    break;
                }
            }
        };

        task.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!ORES.contains(block.getType()) && block.getType() != Material.GLOWING_REDSTONE_ORE) {
            return;
        }

        if (!isCorrectOre(block)) {
            return;
        }

        final Random rand = new Random();

        switch (block.getType()) {
        case COAL_ORE:
            if (rand.nextDouble() > 0.15) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        case IRON_ORE:
            if (rand.nextDouble() > 0.15) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        case GOLD_ORE:
            if (rand.nextDouble() > 0.5) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (rand.nextDouble() > 0.35) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        case LAPIS_ORE:
            if (rand.nextDouble() > 0.3) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        case DIAMOND_ORE:
            if (rand.nextDouble() > 0.75) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        case EMERALD_ORE:
            if (rand.nextDouble() > 0.4) {
                return;
            }

            PlayerUtils.damage(player, 1);
            break;
        default:
            break;
        }
    }

    /**
     * Check if the given block is the correct one for blood cycle.
     *
     * @param block The block to check.
     * @return True if it is, false otherwise.
     */
    private boolean isCorrectOre(Block block) {
        if (current == null) {
            return false;
        }

        if (current == Material.REDSTONE_ORE) {
            return block.getType() == Material.REDSTONE_ORE || block.getType() == Material.GLOWING_REDSTONE_ORE;
        }

        if (current == block.getType()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "BloodCycle is not enabled.");
            return true;
        }

        if (current == null) {
            sender.sendMessage(PREFIX + "§6No ore type has been set.");
            return true;
        }

        sender.sendMessage(PREFIX + "§a" + NameUtils.capitalizeString(current.name(), true) + "§7 has a chance of damaging you!");
        sender.sendMessage(PREFIX + "§7The ore type changes in §6" + DateUtils.secondsToString(seconds) + "§7.");
        return true;
    }
}