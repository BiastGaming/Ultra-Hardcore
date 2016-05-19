package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Astrophobia scenario class
 * 
 * @author Bergasms, modified by LeonTG77
 */
public class Astrophobia extends Scenario implements Listener {
    private final Main plugin;

    private final Timer timer;
    private final Game game;

    public Astrophobia(Main plugin, Timer timer, Game game) {
        super("Astrophobia", "The sun is gone, and the world is in eternal night. Deadly meteors impact the surface at random, leaving craters and flames, and sometimes ores. Aliens arrive from the sky wearing protective armor and shooting powerful weapons. Their tracking bombs (charged creepers) are fired onto the world to target any players that come near them. After defeating one, players can spawn a charged creeper of their own.");

        this.plugin = plugin;

        this.timer = timer;
        this.game = game;
    }

    private BukkitRunnable task = null;

    private static final long TICKS_TO_START = 6000;
    private static final long TICK_INTERVAL = 100L;

    private static final int FREQUENCY = 5;
    private static final int FUSE = 150;

    private static final double CHANCE_PER_DIAMOND = 0.1;
    private static final double CHANCE_PER_GOLD = 0.3;
    private static final double CHANCE_PER_IRON = 0.6;

    @Override
    public void onDisable() {
        game.getWorld().setGameRuleValue("doDaylightCycle", "true");

        if (task != null) {
            task.cancel();
        }

        task = null;
    }

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        game.getWorld().setGameRuleValue("doDaylightCycle", "false");
        game.getWorld().setTime(18000);

        task = new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < FREQUENCY; i++) {
                    dropAstroItem();
                }
            }
        };

        task.runTaskTimer(plugin, Math.max(0, TICKS_TO_START - (timer.getTimeSinceStartInSeconds() * 20)), TICK_INTERVAL);
    }

    @EventHandler
    public void on(BlockIgniteEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Block block = event.getBlock();

        if (game.getWorld() != null && block.getWorld() != game.getWorld()) {
            return;
        }

        final IgniteCause cause = event.getCause();

        if (cause != IgniteCause.SPREAD) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(EntityShootBowEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Entity proj = event.getProjectile();
        final Entity entity = event.getEntity();

        if (event.isCancelled()) {
            return;
        }

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        if (!(proj instanceof Arrow)) {
            return;
        }

        if (!(entity instanceof Skeleton)) {
            return;
        }

        final Skeleton skelly = (Skeleton) entity;

        if (skelly.getEquipment().getHelmet().getType() != Material.GLASS) {
            return;
        }

        final Firework firework = game.getWorld().spawn(skelly.getEyeLocation(), Firework.class);
        proj.setPassenger(firework);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Entity damager = event.getDamager();

        if (!(damager instanceof Arrow)) {
            return;
        }

        if (damager.getPassenger() == null) {
            return;
        }

        if (!game.getWorlds().contains(damager.getWorld())) {
            return;
        }

        final Firework firework = (Firework) damager.getPassenger();
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();

        final Builder builder = FireworkEffect.builder();

        builder.with(Type.BALL_LARGE);
        builder.with(Type.BURST);
        builder.withColor(Color.RED, Color.RED);
        builder.withTrail();

        final FireworkEffect effect = builder.build();

        fireworkMeta.addEffect(effect);
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();

        event.setDamage(6.0D);
    }

    @EventHandler
    public void on(EntityExplodeEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Entity entity = event.getEntity();

        if (!entity.hasMetadata("meteor")) {
            return;
        }

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        final Location loc = event.getLocation();
        final Random rand = new Random();

        final List<Block> blocks = event.blockList();

        for (Block block : blocks) {
            block.setType(Material.AIR);
        }

        blocks.clear();

        if (rand.nextBoolean()) {
            return;
        }

        final double chance = rand.nextDouble();

        if (chance < CHANCE_PER_DIAMOND) {
            BlockUtils.dropItem(loc, new ItemStack(Material.DIAMOND, 1));
            return;
        }

        if (chance < CHANCE_PER_DIAMOND + CHANCE_PER_GOLD) {
            BlockUtils.dropItem(loc, new ItemStack(Material.GOLD_ORE, 2));
            return;
        }

        if (chance < CHANCE_PER_DIAMOND + CHANCE_PER_GOLD + CHANCE_PER_IRON) {
            BlockUtils.dropItem(loc, new ItemStack(Material.IRON_ORE, 2));
        }
    }

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Entity entity = event.getEntity();

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        if (entity instanceof Monster && timer.getTimeSinceStart() < 10 && entity.getLocation().getY() > 60) {
            event.setCancelled(true);
        }

        if (!(entity instanceof Creeper)) {
            return;
        }

        if (event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
            return;
        }

        final Creeper creep = (Creeper) entity;
        event.setCancelled(false);

        creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 10), true);
        creep.setPowered(true);

        creep.getEquipment().setItemInHand(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
        creep.getEquipment().setItemInHandDropChance(100.0F);
        creep.setCanPickupItems(false);
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        if (!(entity instanceof Creeper)) {
            return;
        }

        Creeper creeper = (Creeper) entity;

        if (!creeper.isPowered()) {
            return;
        }

        event.setDroppedExp(0);
    }

    /**
     * Drop an astrophobia item.
     */
    private void dropAstroItem() {
        if (game.getWorld() == null) {
            return;
        }

        final Chunk[] chunks = game.getWorld().getLoadedChunks();
        final Random rand = new Random();

        if (chunks.length == 0) {
            return;
        }

        final Chunk chunk = chunks[rand.nextInt(chunks.length)];
        final Location loc = new Location(game.getWorld(), chunk.getX() * 16 + rand.nextInt(16), 255.0D, chunk.getZ() * 16 + rand.nextInt(15));

        final int chance = rand.nextInt(100);

        final Vector vec = new Vector(rand.nextFloat() * 10.0F - 5.0F, -1.0F * rand.nextFloat(), rand.nextFloat() * 10.0F - 5.0F);

        if (chance < 70) {
            final TNTPrimed meteor = (TNTPrimed) game.getWorld().spawn(loc, TNTPrimed.class);

            meteor.setVelocity(new Vector((rand.nextFloat() * 10.0F - 5.0F) / 2.0F, -1.0F * rand.nextFloat(), (rand.nextFloat() * 10.0F - 5.0F) / 2.0F));
            meteor.setFallDistance(150.0F);
            meteor.setFuseTicks(FUSE);

            meteor.setMetadata("meteor", new FixedMetadataValue(plugin, "isMeteor"));

            meteor.setIsIncendiary(rand.nextBoolean());
            meteor.setYield(Math.max((float) ((1000.0D - loc.distance(new Location(game.getWorld(), 0.0D, 63.0D, 0.0D))) / 100.0D), 7.0F));
            return;
        }

        if (chance < 85) {
            final Creeper creep = game.getWorld().spawn(loc.subtract(0.0D, 155.0D, 0.0D), Creeper.class);

            creep.setPowered(true);

            creep.setFallDistance(-500.0F);
            creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 10), true);
            creep.setVelocity(vec);

            creep.getEquipment().setItemInHand(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
            creep.getEquipment().setItemInHandDropChance(100.0F);
            creep.setCanPickupItems(false);
            return;
        }

        if (chance < 100) {
            for (int i = 2; i < 8; i++) {
                final Skeleton skele = game.getWorld().spawn(loc.subtract(0.0D, 155.0D, 0.0D), Skeleton.class);

                skele.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 10), true);
                skele.setCanPickupItems(false);
                skele.setFallDistance(-500.0F);

                final ItemStack entitychest = new ItemStack(Material.LEATHER_CHESTPLATE);
                final ItemStack entityleg = new ItemStack(Material.LEATHER_LEGGINGS);
                final ItemStack entityboots = new ItemStack(Material.LEATHER_BOOTS);

                final ItemStack[] items = { entitychest, entityleg, entityboots };
                final String[] names = { "Space Suit", "Space Pants", "Space Boots" };
                final Color[] colours = { Color.GRAY, Color.GRAY, Color.GRAY };

                int index = 0;

                for (ItemStack item : items) {
                    final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

                    meta.setColor(colours[index]);
                    meta.setDisplayName(names[index]);
                    item.setItemMeta(meta);

                    index++;
                }

                skele.getEquipment().setArmorContents(items);

                final ItemStack entityhelm = new ItemStack(Material.GLASS);
                skele.getEquipment().setHelmet(entityhelm);
                skele.setVelocity(vec);

                skele.getEquipment().setItemInHand(new ItemStack(Material.BOW));
            }
        }
    }
}