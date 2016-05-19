package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Compensation scenario class
 * 
 * @author LeonTG77
 */
public class Compensation extends Scenario implements Listener {
    private final Arena arena;

    private final FeatureManager feat;
    private final TeamManager teams;

    public Compensation(Arena arena, TeamManager teams, FeatureManager feat) {
        super("Compensation", "When a player on a team dies, the player's max health is divided up and added to the max health of the player's teammates. The extra health received will regenerate in 30 seconds.");

        this.arena = arena;

        this.teams = teams;
        this.feat = feat;
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Recipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        if (result.getType() != Material.ARROW) {
            return;
        }

        CraftingInventory inv = event.getInventory();
        inv.getResult().setAmount(result.getAmount() * 4);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        if (arena.isEnabled()) {
            return;
        }

        Player player = event.getEntity();
        double maxHealth = player.getMaxHealth();
        
        Team team = teams.getTeam(player);

        if (team == null) {
            return;
        }
        
        team.removeEntry(player.getName());

        double hp = maxHealth / team.getSize();
        int hpRounded = (int) hp;

        double excessHealth = hp - hpRounded;
        int ticksRegen = hpRounded * 50;

        for (String entry : team.getEntries()) {
            Player teammate = Bukkit.getPlayer(entry);

            if (teammate == null) {
                continue;
            }

            teammate.setMaxHealth(teammate.getMaxHealth() + hp);
            teammate.setHealth(teammate.getHealth() + excessHealth);
            
            teammate.removePotionEffect(PotionEffectType.REGENERATION);
            teammate.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticksRegen, 0));
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