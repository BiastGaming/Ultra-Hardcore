package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Aurophobia scenario class.
 * 
 * @author LeonTG77
 */
public class Aurophobia extends Scenario implements Listener {
    private final Game game;

    /**
     * Aurophobia scenario class constructor.
     *
     * @param game The game class.
     */
    public Aurophobia(Game game) {
        super("Aurophobia", "Whenever you mine Gold you have a chance of getting damaged by a heart, getting spawned 2 silverfish on you, losing the gold, getting blindness for 10 seconds, or gettting a cave spider spawned on you.");

        this.game = game;
    }

    private final Random rand = new Random();

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.GOLD_ORE) {
            return;
        }

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (rand.nextDouble() <= 0.05) {
            player.damage(2);
        }

        if (rand.nextDouble() <= 0.05) {
            player.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), Silverfish.class);
            player.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), Silverfish.class);
        }

        if (rand.nextDouble() <= 0.05) {
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);

            event.setCancelled(true);
            block.setType(Material.AIR);
        }

        if (rand.nextDouble() <= 0.05) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
        }

        if (rand.nextDouble() <= 0.03) {
            player.getWorld().spawn(block.getLocation().add(0.5, 0.1, 0.5), CaveSpider.class);
        }
    }
}