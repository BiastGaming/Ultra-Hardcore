package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * EightLeggedFreaks scenario class
 * 
 * @author LeonTG77
 */
public class EightLeggedFreaks extends Scenario implements Listener {
    private final Game game;

    public EightLeggedFreaks(Game game) {
        super("EightLeggedFreaks", "All hostile mobs spawn as different types of spiders with different abilities.");

        this.game = game;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        for (World world : game.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                switch (entity.getType()) {
                case CAVE_SPIDER:
                case CREEPER:
                case ENDERMAN:
                case SILVERFISH:
                case SKELETON:
                case SLIME:
                case SPIDER:
                case WITCH:
                case ZOMBIE:
                    entity.remove();
                default:
                    break;
                }
            }
        }
    }

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final LivingEntity entity = event.getEntity();

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        if (entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Creeper) {
            entity.getWorld().spawn(entity.getLocation(), Spider.class);
            event.setCancelled(true);
            return;
        }

        if (entity instanceof Witch || entity instanceof Enderman) {
            entity.getWorld().spawn(entity.getLocation(), CaveSpider.class);
            event.setCancelled(true);
            return;
        }

        if (entity.getType() == EntityType.CAVE_SPIDER) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 1));
            return;
        }

        if (entity.getType() == EntityType.SPIDER) {
            Random rand = new Random();

            if (rand.nextDouble() <= 0.03) {
                entity.setMaxHealth(12);
                entity.setHealth(12);

                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 7));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 1));

                entity.setCustomNameVisible(true);
                entity.setCustomName("§4§lJumper");
                return;
            }

            if (rand.nextDouble() <= 0.03) {
                entity.setMaxHealth(12);
                entity.setHealth(12);

                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1726272000, 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 1));

                entity.setCustomNameVisible(true);
                entity.setCustomName("§4§lMother");
                return;
            }

            if (rand.nextDouble() <= 0.03) {
                entity.setMaxHealth(12);
                entity.setHealth(12);

                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 3));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 1));

                entity.setCustomNameVisible(true);
                entity.setCustomName("§4§lSpinner");
                return;
            }

            entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1726272000, 1));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        DamageCause cause = event.getCause();
        Entity entity = event.getEntity();

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        if (entity.getType() != EntityType.SPIDER) {
            return;
        }

        if (cause == DamageCause.FALL) {
            if (entity.getCustomName() == null || !entity.getCustomName().equals("§4§lJumper")) {
                return;
            }

            event.setCancelled(true);
            return;
        }

        if (cause == DamageCause.SUFFOCATION) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        if (damager.getType() != EntityType.SPIDER) {
            return;
        }

        Player player = (Player) entity;
        Random rand = new Random();

        if (damager.getCustomName() == null) {
            return;
        }

        if (damager.getCustomName().equals("§4§lMother")) {
            if (rand.nextDouble() <= 0.1) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
            }
            return;
        }

        if (damager.getCustomName().equals("§4§lSpinner")) {
            player.getLocation().getBlock().setType(Material.WEB);

            if (rand.nextDouble() <= 0.1) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        LivingEntity entity = event.getEntity();
        Random rand = new Random();

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        if (entity.getType() == EntityType.CAVE_SPIDER) {
            if (rand.nextDouble() <= 0.15) {
                event.getDrops().add(new ItemStack(Material.GOLD_INGOT));
            }
            return;
        }

        if (entity.getType() == EntityType.SPIDER) {
            if (entity.getCustomName() == null) {
                if (rand.nextDouble() <= 0.05) {
                    event.getDrops().add(new ItemStack(Material.GOLD_INGOT));
                }
                return;
            }

            if (entity.getCustomName().equals("§4§lMother")) {
                event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 2));
                entity.getWorld().spawn(entity.getLocation(), CaveSpider.class);
                entity.getWorld().spawn(entity.getLocation(), CaveSpider.class);
                entity.getWorld().spawn(entity.getLocation(), CaveSpider.class);
                return;
            }

            if (entity.getCustomName().equals("§4§lSpinner")) {
                event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 2));
                return;
            }

            if (entity.getCustomName().equals("§4§lJumper")) {
                event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 2));
            }
        }
    }
}