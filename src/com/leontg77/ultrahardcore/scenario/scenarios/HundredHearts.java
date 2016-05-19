package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * 100Hearts scenario class.
 * 
 * @author LeonTG77
 */
public class HundredHearts extends Scenario implements Listener {
    private final FeatureManager feat;

    public HundredHearts(FeatureManager feat) {
        super("100Hearts", "Everyone has 100 hearts, golden apples heal 20% of your max health.");

        this.feat = feat;
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setMaxHealth(20);
        }
    }

    @Override
    public void onEnable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setMaxHealth(200);
            online.setHealth(200);
        }
    }

    @EventHandler
    public void on(FinalHealEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setMaxHealth(200);
            online.setHealth(200);
        }
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() != Material.GOLDEN_APPLE || item.getDurability() == 1) {
            return;
        }
        
        GoldenHeadsFeature ghead = feat.getFeature(GoldenHeadsFeature.class);
        
        player.removePotionEffect(PotionEffectType.REGENERATION);
        int ticks;
        
        if (ghead.isGoldenHead(item)) {
            ticks = (int) ((player.getMaxHealth() * (ghead.getHealAmount() / 10)) * 25);
        } else {
            ticks = (int) ((player.getMaxHealth() * 0.2) * 25);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, 1));
    }
}