package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Cripple scenario class.
 *  
 * @author LeonTG77
 */
public class Coco extends Scenario implements Listener {
    private final Main plugin;

    public Coco(Main plugin) {
        super("Coco", "Everyone starts out with 5 cocoa beans. If you right click the cocoa bean, you 'eat' it and receive speed I for 30 seconds and strength I for 10 seconds. After 30 seconds, you receive 15 seconds of slowness I and 5 seconds of weakness I.");

        this.plugin = plugin;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Action action = event.getAction();
        final Player player = event.getPlayer();

        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        final ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.INK_SACK || item.getDurability() != 3) {
            return;
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType() != PotionEffectType.SPEED && effect.getType() != PotionEffectType.INCREASE_DAMAGE && effect.getType() != PotionEffectType.SLOW && effect.getType() != PotionEffectType.WEAKNESS) {
                continue;
            }

            player.removePotionEffect(effect.getType());
        }

        if (item.getAmount() == 1) {
            item.setType(Material.AIR);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0));

        new BukkitRunnable() {
            public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
            }
        }.runTaskLater(plugin, 600);
    }
}