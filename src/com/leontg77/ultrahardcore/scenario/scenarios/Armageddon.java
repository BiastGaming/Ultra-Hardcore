package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Armageddon scenario class.
 * 
 * @author Bergasms, modified by LeonTG77.
 */
public class Armageddon extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    public Armageddon(Main plugin, Game game) {
        super("Armageddon", "Lava, gravel, sand, potions, and some items fall from the sky.");

        this.plugin = plugin;
        this.game = game;
    }

    private BukkitRunnable task = null;
    private World world;

    private static final long TICKS_TO_START = 12000L;
    private static final long TICK_INTERVAL = 50L;
    private static final int FREQUENCY = 5;

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }

        task = null;
    }

    @Override
    public void onEnable() {
        world = game.getWorld();
        
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        onEnable();

        task = new BukkitRunnable() {
            public void run() {
                if (world == null) {
                    return;
                }

                for (int i = 0; i < FREQUENCY; i++) {
                    dropArmageddonItem();
                }
            }
        };

        task.runTaskTimer(plugin, TICKS_TO_START, TICK_INTERVAL);
    }

    /**
     * Drop an armageddon item in the game world.
     */
    @SuppressWarnings("deprecation")
    private void dropArmageddonItem() {
        final Chunk[] chunks = world.getLoadedChunks();
        final Random rand = new Random();

        if (chunks.length == 0) {
            return;
        }

        final Chunk chunk = chunks[rand.nextInt(chunks.length)];
        final Location loc = new Location(chunk.getWorld(), chunk.getX() * 16 + rand.nextInt(16), 255.0D, chunk.getZ() * 16 + rand.nextInt(15));

        handlePotionDrop(loc);
        final int randomInt = rand.nextInt(100);

        if (randomInt > 90) {
            int itemChoice = rand.nextInt(10);

            switch (itemChoice) {
            case 0:
                BlockUtils.dropItem(loc, new ItemStack(Material.GOLD_INGOT, 1));
                break;
            case 1:
                BlockUtils.dropItem(loc, new ItemStack(Material.DIAMOND, 1));
                break;
            case 2:
                BlockUtils.dropItem(loc, new ItemStack(Material.NETHER_STALK, 1));
                break;
            case 3:
                BlockUtils.dropItem(loc, new ItemStack(Material.BLAZE_ROD, 1));
                break;
            case 4:
                BlockUtils.dropItem(loc, new ItemStack(Material.STRING, 3));
                break;
            case 5:
                world.spawnFallingBlock(loc, Material.MELON_BLOCK, (byte) 0);
                break;
            case 6:
                world.spawnFallingBlock(loc, Material.IRON_BLOCK, (byte) 0);
                break;
            case 7:
                world.spawnFallingBlock(loc, Material.MOB_SPAWNER, (byte) 0);
                break;
            case 8:
                BlockUtils.dropItem(loc, new ItemStack(Material.BLAZE_POWDER, 1));
                break;
            case 9:
                BlockUtils.dropItem(loc, new ItemStack(Material.BOOK, 1));
                break;
            }
        }
        else if (randomInt > 70) {
            handlePotionDrop(loc);
        }
        else if (randomInt > 40) {
            world.spawnFallingBlock(loc, rand.nextBoolean() ? Material.SAND : Material.GRAVEL, (byte) 0);
        }
        else {
            world.spawnFallingBlock(loc, Material.LAVA, (byte) 0);
        }
    }

    /**
     * Handle a potion drop.
     *
     * @param loc The location to use.
     */
    private void handlePotionDrop(Location loc) {
        Random rand = new Random();
        int itemChoice = rand.nextInt(30);

        if (itemChoice > 20) {
            dropPotion(loc, PotionType.INSTANT_DAMAGE, rand.nextInt(1));
        }
        else if (itemChoice > 15) {
            dropPotion(loc, PotionType.POISON, rand.nextInt(1));
        }
        else if (itemChoice > 10) {
            dropPotion(loc, PotionType.WEAKNESS, rand.nextInt(1));
        }
        else if (itemChoice > 5) {
            dropPotion(loc, PotionType.SLOWNESS, rand.nextInt(1));
        }
        else {
            PotionType[] types = PotionType.values();
            dropPotion(loc, types[rand.nextInt(types.length)], rand.nextInt(1));
        }
    }

    /**
     * Drop a potion.
     *
     * @param loc The location to drop it at.
     * @param type The potion type to use.
     * @param modifier The tier of the potion.
     */
    private void dropPotion(Location loc, PotionType type, int modifier) {
        if (type == PotionType.WATER) {
            return;
        }

        final Potion potion = new Potion(type, 1 + modifier);
        potion.setSplash(true);

        // idk why, but berg said this works and it does so I won't question him
        if (potion.getType() != PotionType.INSTANT_DAMAGE && potion.getType() != PotionType.INSTANT_HEAL) {
            potion.extend();
        }

        final ItemStack itemStack = potion.toItemStack(1);

        // I don't want air to be dropped, it spams the console.
        if (itemStack.getType() == Material.AIR) {
            return;
        }

        final Chicken entity = world.spawn(loc, Chicken.class);
        final ThrownPotion pot = entity.launchProjectile(ThrownPotion.class, new Vector(0, -1, 0));

        pot.setVelocity(new Vector(0, -1, 0));
        pot.setItem(itemStack);

        entity.setHealth(0);
    }
}
