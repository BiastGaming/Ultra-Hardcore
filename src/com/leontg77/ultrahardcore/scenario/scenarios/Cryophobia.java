package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Cryophobia scenario class
 * 
 * @author Bergasms
 */
public class Cryophobia extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    public Cryophobia(Main plugin, Game game) {
        super("Cryophobia", "A layer of ice will rise slowly from the bottom of the map, faster as the game goes on, filling caves and eventually reaching high up above the surface. Breaking ice causes damage and ill effects. Creepers explode into a ball of ice, while skeletons have geared up for the winter. The biome of the entire map has also been switched to a cold taiga, meaning snow falls and water freezes everywhere on the map.");

        this.plugin = plugin;
        this.game = game;
    }

    private int heightCountdown = 150;
    private int levelHeight = 0;
    private int maxheight = 80;

    private int heightUpdateTimer;
    private int processTimer;

    private boolean[][] biomeLookup = new boolean[500][500];
    private int[][] existingHeights = new int[500][500];
    private Material replaceMaterial = Material.ICE;

    private HashMap<String, ChunkProcess> chunkQueue = new HashMap<String, ChunkProcess>();
    private LinkedList<String> priorityQueue = new LinkedList<String>();;

    private World world;

    @Override
    public void onDisable() {
        if (heightUpdateTimer != -1) {
            Bukkit.getServer().getScheduler().cancelTask(this.heightUpdateTimer);
            heightUpdateTimer = -1;
        }

        if (processTimer != -1) {
            Bukkit.getServer().getScheduler().cancelTask(this.processTimer);
            processTimer = -1;
        }
    }

    @Override
    public void onEnable() {
        world = game.getWorld();
    }

    @EventHandler
    public void on(GameStartEvent event) {
        for (int i = 0; i < 500; i++) {
            for (int j = 0; j < 500; j++) {
                existingHeights[i][j] = levelHeight;
                biomeLookup[i][j] = false;
            }
        }

        for (Chunk chunk : world.getLoadedChunks()) {
            chunkLoaded(chunk);
        }

        processTimer = -1;

        heightUpdateTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                heightCountdown -= 1;

                if (heightCountdown <= 0) {
                    if (levelHeight < maxheight) {
                        levelHeight += 1;
                    } else {
                        onDisable();
                    }

                    didRaiseHeight();

                    if (levelHeight <= 12) {
                        heightCountdown = 150;
                    } else if (levelHeight <= 20) {
                        heightCountdown = 75;
                    } else if (levelHeight <= 40) {
                        heightCountdown = 60;
                    } else {
                        heightCountdown = 60;
                    }
                }
            }
        }, 20L, 20L);

        didRaiseHeight();
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        chunkLoaded(event.getChunk());
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        chunkUnloaded(event.getChunk());
    }

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        LivingEntity entity = event.getEntity();
        Entity proj = event.getProjectile();

        if (!(entity instanceof Skeleton)) {
            return;
        }

        if (!(proj instanceof Arrow)) {
            return;
        }

        event.setCancelled(true);
        entity.launchProjectile(Snowball.class, proj.getVelocity());

        entity.getWorld().playSound(entity.getLocation(), Sound.SHOOT_ARROW, 1, 1);
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        LivingEntity entity = event.getEntity();
        Random rand = new Random();

        if (entity instanceof Animals) {
            if (rand.nextInt(10) <= 1) {
                entity.getWorld().spawn(event.getLocation(), Snowman.class);
            }

            event.setCancelled(rand.nextBoolean());
            return;
        }

        if (!(entity instanceof Skeleton)) {
            return;
        }

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemStack[] items = { helmet, chestplate, leggings, boots };
        String[] names = { "Beanie", "Parka", "Ski Pants", "Snowboard Boots" };
        Color[] colours = { Color.BLACK, Color.BLUE, Color.BLUE, Color.RED };

        int ind = 0;

        for (ItemStack item : items) {
            LeatherArmorMeta lma = (LeatherArmorMeta) item.getItemMeta();
            lma.setColor(colours[ind]);
            lma.setDisplayName(names[ind]);
            item.setItemMeta(lma);
            ind++;
        }

        entity.getEquipment().setArmorContents(items);
    }

    @EventHandler
    public void onExplodeDeath(EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof Creeper)) {
            return;
        }

        final Location loc = event.getEntity().getLocation();
        final World world = loc.getWorld();

        if (world != this.world) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                Location l = loc;

                world.getBlockAt(l).setType(Material.PACKED_ICE);
                world.getBlockAt(l.add(0.0D, 1.0D, 0.0D)).setType(Material.PACKED_ICE);
                world.getBlockAt(l.add(0.0D, -2.0D, 0.0D)).setType(Material.PACKED_ICE);
                world.getBlockAt(l.add(1.0D, 1.0D, 0.0D)).setType(Material.PACKED_ICE);
                world.getBlockAt(l.add(-2.0D, 0.0D, 0.0D)).setType(Material.PACKED_ICE);
                world.getBlockAt(l.add(1.0D, 0.0D, 1.0D)).setType(Material.PACKED_ICE);
                world.getBlockAt(l.add(0.0D, 0.0D, -2.0D)).setType(Material.PACKED_ICE);

                l = loc;

                world.getBlockAt(l.add(1.0D, -1.0D, 1.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(-1.0D, 0.0D, 0.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(-1.0D, 0.0D, 0.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(0.0D, 0.0D, -1.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(0.0D, 0.0D, -1.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(1.0D, 0.0D, 0.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(1.0D, 0.0D, 0.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(0.0D, 0.0D, 1.0D)).setType(Material.ICE);
                world.getBlockAt(l.add(0.0D, 0.0D, 1.0D)).setType(Material.ICE);

                Random rand = new Random();
                int times = 3 + rand.nextInt(2);

                for (int i = 0; i < times; i++) {
                    l = loc;
                    world.getBlockAt(l.add(rand.nextInt(4) - 2, rand.nextInt(2), rand.nextInt(4) - 2)).setType(Material.ICE);
                }
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(damager instanceof Snowball)) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        Snowball snowball = (Snowball) damager;
        ProjectileSource shooter = snowball.getShooter();

        if (!(shooter instanceof Skeleton)) {
            return;
        }

        event.setDamage(1.0D + new Random().nextFloat());
    }

    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();

        if (block.getType() != Material.LEAVES) {
            return;
        }

        int damage = BlockUtils.getDurability(block);

        if (damage != 1 && damage != 5 && damage != 9 && damage != 13) {
            return;
        }

        Random rand = new Random();

        if (rand.nextInt(40) == 0) {
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE));
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Block from = event.getBlock();
        Block to = event.getToBlock();

        if (from.getType() != Material.ICE && to.getType() != Material.WATER) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();

        if (block.getType() != Material.ICE && block.getType() != Material.PACKED_ICE) {
            return;
        }

        Random rand = new Random();

        if (rand.nextInt(4) == 0) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 1));
        }

        if (rand.nextInt(8) == 0) {
            event.getPlayer().damage(1.0D);
        }
    }

    protected void didRaiseHeight() {
        priorityQueue.remove("sentinel");
        priorityQueue.addLast("sentinel");

        if (processTimer == -1) {
            processTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                public void run() {
                    processChunkIfRequired();
                }
            }, 1L, 1L);
        }
    }

    protected void processChunkIfRequired() {
        int attempts = 10;

        for (int a = 0; a < attempts; a++) {
            String pQueue = (String) priorityQueue.getFirst();

            if (pQueue.equalsIgnoreCase("sentinel")) {
                priorityQueue.remove("sentinel");
                a = 100;

                if (processTimer != -1) {
                    Bukkit.getServer().getScheduler().cancelTask(processTimer);
                    processTimer = -1;
                }
                return;
            }

            ChunkProcess cp = (ChunkProcess) chunkQueue.get(pQueue);

            boolean addToMiddle = false;

            if ((cp != null) && (cp.x >= 0) && (cp.x < 500) && (cp.z >= 0) && (cp.z < 500) && (this.existingHeights[cp.x][cp.z] < this.levelHeight)) {
                World w = world;

                if (w != null) {
                    Chunk c = w.getChunkAt(cp.x - 250, cp.z - 250);

                    if (c != null) {
                        int upToHeight = existingHeights[cp.x][cp.z];

                        if ((cp.stopHeight < levelHeight) && (levelHeight - upToHeight < 8)) {
                            cp.stopHeight = levelHeight;
                        }

                        for (int y = upToHeight; y <= cp.stopHeight; y++) {
                            for (int i = 0; i < 16; i++) {
                                for (int j = 0; j < 16; j++) {
                                    if (!c.getBlock(i, y, j).getType().isSolid()) {
                                        c.getBlock(i, y, j).setType(replaceMaterial);
                                    }
                                }
                            }
                        }

                        a = 100;

                        existingHeights[cp.x][cp.z] = cp.stopHeight;
                        int stopAt = cp.stopHeight + 8;

                        if (stopAt > levelHeight) {
                            stopAt = levelHeight;
                        }

                        cp.stopHeight = stopAt;

                        if (cp.stopHeight < levelHeight) {
                            addToMiddle = true;
                        }
                    }
                }
            }

            priorityQueue.removeFirst();

            if (addToMiddle) {
                int idx = priorityQueue.size() / 2;

                priorityQueue.add(idx, pQueue);
            } else {
                priorityQueue.addLast(pQueue);
            }
        }
    }

    public void chunkLoaded(Chunk chunk) {
        if (priorityQueue == null) {
            return;
        }

        if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        if (!chunk.getWorld().getName().equals(world.getName())) {
            return;
        }

        int xp = chunk.getX() + 250;
        int zp = chunk.getZ() + 250;

        if (!biomeLookup[xp][zp]) {
            biomeLookup[zp][zp] = true;

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    chunk.getBlock(i, 128, j).setBiome(Biome.COLD_TAIGA);
                }
            }
        }

        int stopAt = existingHeights[xp][zp] + 8;

        if (stopAt > levelHeight) {
            stopAt = levelHeight;
        }

        ChunkProcess cp = new ChunkProcess(xp, zp, stopAt);

        chunkQueue.put(cp.stringRep(), cp);
        priorityQueue.add(cp.stringRep());

        priorityQueue.remove("sentinel");
        priorityQueue.addLast("sentinel");
    }

    public void chunkUnloaded(Chunk chunk) {
        if (priorityQueue == null) {
            return;
        }

        if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        int xp = chunk.getX() + 250;
        int zp = chunk.getZ() + 250;

        ChunkProcess cp = new ChunkProcess(xp, zp, 0);

        chunkQueue.remove(cp.stringRep());
        priorityQueue.remove(cp.stringRep());
    }

    private class ChunkProcess {
        public int x;
        public int z;
        public int stopHeight;

        public ChunkProcess(int x, int z, int stopHeight) {
            this.x = x;
            this.z = z;
            this.stopHeight = stopHeight;
        }

        public String stringRep() {
            return this.x + ":" + this.z;
        }
    }
}