package com.leontg77.ultrahardcore.minigames;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.minigames.listeners.ArenaListener;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.orelimiter.OreLimiter;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * PvP Arena class.
 * <p>
 * This class contains methods for enabling/disabling the arena, removing and adding players, giving the kit, scores and getting the players in the arena.
 * 
 * @author LeonTG77
 */
public class Arena {
    public static final String PREFIX = "§4Arena §8» §7";

    private final Main plugin;

    private final BoardManager board;
    private final Game game;

    private final ScatterManager scatter;
    private final WorldManager manager;

    private final ArenaListener listener;
    private final Set<UUID> players;

    private Scoreboard score;
    private Objective killboard;

    public Arena(Main plugin, Game game, BoardManager board, ScatterManager scatter, WorldManager manager) {
        this.plugin = plugin;

        this.board = board;
        this.game = game;

        this.score = board.getBoard();
        this.killboard = score.getObjective("arenaKills");

        this.scatter = scatter;
        this.manager = manager;

        this.listener = new ArenaListener(plugin, this);
        this.players = new HashSet<UUID>();
    }

    private boolean enabled = false;

    /**
     * List of all verified arena world seeds.
     */
    private static final List<Long> SEEDS = ImmutableList.of(
            -63189153649164370L,
            1156764802647643895L,
            -8206978923571129301L,
            -2155522542440683540L,
            7388199132007714211L,
            -8181738055916680558L,
            -3638092325106180679L,
            4542866204785804909L
    );

    /**
     * Setup the arena class.
     */
    public void setup() {
        if (score.getObjective("arenaKills") == null) {
            killboard = score.registerNewObjective("arenaKills", "dummy");
        }

        killboard.setDisplayName("§4Arena §8» §7§oUse /a to join§r");

        if (game.arenaBoard()) {
            killboard.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        plugin.getLogger().info("The arena has been setup.");
    }

//    private BukkitRunnable borderTask;
    private BukkitRunnable regenTask;

    /**
     * Enable the arena
     */
    public void enable() {
        this.enabled = true;

        World world = Bukkit.getWorld("arena");

        if (world != null) {
            world.getWorldBorder().setSize(400);
        }

        Bukkit.getPluginManager().registerEvents(listener, plugin);

        if (game.pregameBoard()) {
            board.setScore("§a ", 11);
            board.setScore("§8» §cArena:", 10);
            board.setScore("§8» §7/a ", 9);
        }

        if (game.arenaBoard()) {
            killboard.setDisplaySlot(DisplaySlot.SIDEBAR);

            game.setPregameBoard(false);
            game.setArenaBoard(true);
        }

        setScore("§8» §a§oNext Reset", 30);

        if (regenTask == null) {
            regenTask = new BukkitRunnable() {
                int time = 1800;

                public void run() {
                    time--;

                    setScore("§8» §a§oNext Reset", (int) ((time / 60) + 1));

                    switch (time) {
                    case 900:
                        PlayerUtils.broadcast(PREFIX + "The arena will reset in §a15 §7minutes.");

                        for (Player arena : getPlayers()) {
                            PacketUtils.sendTitle(arena, "§4Arena Reset!", "§7Next one is in §a15 §7minutes!", 1, 10, 1);
                        }
                        break;
                    case 600:
                        PlayerUtils.broadcast(PREFIX + "The arena will reset in §a10 §7minutes.");

                        for (Player arena : getPlayers()) {
                            PacketUtils.sendTitle(arena, "§4Arena Reset!", "§7Next one is in §a10 §7minutes!", 1, 10, 1);
                        }
                        break;
                    case 300:
                        PlayerUtils.broadcast(PREFIX + "The arena will reset in §a5 §7minutes.");

                        for (Player arena : getPlayers()) {
                            PacketUtils.sendTitle(arena, "§4Arena Reset!", "§7Next one is in §a5 §7minutes!", 1, 10, 1);
                        }
                        break;
                    case 60:
                        PlayerUtils.broadcast(PREFIX + "The arena will reset in §a1 §7minute.");

                        for (Player arena : getPlayers()) {
                            PacketUtils.sendTitle(arena, "§4Arena Reset!", "§7Next one is in §a1 §7minute!", 1, 10, 1);
                        }
                        break;
                    case 30:
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                        PlayerUtils.broadcast(PREFIX + "The arena will reset in §a" + time + " §7seconds.");

                        for (Player arena : getPlayers()) {
                            PacketUtils.sendTitle(arena, "§4Arena Reset!", "§7Next one is in §a" + time + " §7seconds!", 1, 10, 1);
                        }
                        break;
                    case 1:
                        PlayerUtils.broadcast(PREFIX + "The arena will reset in §a1 §7second.");

                        for (Player arena : getPlayers()) {
                            PacketUtils.sendTitle(arena, "§4Arena Reset!", "§7Next one is in §a1 §7second!", 1, 10, 1);
                        }
                        break;
                    case 0:
                        time = 1800;
                        reset();
                        break;
                    }
                }
            };

            regenTask.runTaskTimer(plugin, 20, 20);
        }

//        if (borderTask == null) {
//            borderTask = new BukkitRunnable() {
//                public void run() {
//                    World world = Bukkit.getWorld("arena");
//
//                    if (world == null) {
//                        return;
//                    }
//
//                    ;
//
//                    world.getWorldBorder().setSize(newRadius, 25); // Thanks @D4mnX for this
//                }
//            };
//
//            borderTask.runTaskTimer(plugin, 600, 600);
//        }
    }

    /**
     * Disable the arena
     */
    public void disable() {
        this.enabled = false;

        World world = Bukkit.getWorld("arena");

        if (world != null) {
            world.getWorldBorder().setSize(400);
        }

        HandlerList.unregisterAll(listener);

        for (Player player : getPlayers()) {
            User user = plugin.getUser(player);
            user.reset();

            if (player.isDead()) {
                player.spigot().respawn();
            }

            player.teleport(plugin.getSpawn());
            score.resetScores(player.getName());
        }

        if (game.pregameBoard()) {
            board.resetScore("§a ");
            board.resetScore("§8» §cArena:");
            board.resetScore("§8» §7/a ");
        }

        if (game.arenaBoard()) {
            for (String entry : score.getEntries()) {
                resetScore(entry);
            }

            if (!isResetting) {
                PlayerUtils.broadcast(PREFIX + "The arena board has been disabled.");
                board.getKillsObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

                game.setArenaBoard(false);
            }
        }

        resetScore("§8» §a§oNext Reset");
        players.clear();

        if (regenTask != null) {
            regenTask.cancel();
        }

//        if (borderTask != null) {
//            borderTask.cancel();
//        }
//
//        borderTask = null;
        regenTask = null;
    }

    public boolean isResetting = false;
    public boolean wasEnabled = false;

    /**
     * Reset the arena.
     */
    public void reset() {
        wasEnabled = isEnabled();
        isResetting = true;

        if (wasEnabled) {
            disable();
        }

        PlayerUtils.broadcast(PREFIX + "The arena is resetting, lag incoming.");

        World world = Bukkit.getWorld("arena");

        manager.deleteWorld(world);
        manager.createWorld("arena", 400, SEEDS.get(new Random().nextInt(SEEDS.size())), Environment.NORMAL, WorldType.NORMAL, false, OreLimiter.Type.NONE, false, 0.0, 0.0);

        world = Bukkit.getWorld("arena");

        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);

        PlayerUtils.broadcast(PREFIX + "World reset done, pregenning arena world.");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " set 200 0 0");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " fill 420");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill confirm");
    }

    /**
     * Check if the arena is enabled.
     *
     * @return True if the arena is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get a list of players in the arena.
     *
     * @return A list of players in the arena.
     */
    public List<Player> getPlayers() {
        List<Player> list = new ArrayList<Player>();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                list.add(player);
            }
        }

        return ImmutableList.copyOf(list);
    }

    /**
     * Adds the given player to the arena.
     *
     * @param player the player.
     */
    public void addPlayer(Player player) {
        World world = Bukkit.getWorld("arena");
        
        int radius = Math.min(40 + (players.size() * 10), 400) / 2;
        List<Location> locs = scatter.findScatterLocations(world, radius, 1);

        if (locs.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Could not teleport you to the arena.");
            return;
        }

        Location loc = locs.get(0);

        player.sendMessage(PREFIX + "You joined the arena.");
        player.sendMessage(PREFIX + "Modify your hotbar with §a/hotbar§7.");

        players.add(player.getUniqueId());
        giveKit(player);

        loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 7));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 7));

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(loc);

        player.setAllowFlight(false);
        player.setFlying(false);

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
    }

    /**
     * Removes the given player from the arena.
     *
     * @param player The player.
     * @param death If the removal was caused by dying
     */
    public void removePlayer(Player player, boolean death) {
        players.remove(player.getUniqueId());

        if (death) {
            return;
        }

        player.sendMessage(PREFIX + "You left the arena.");

        if (getScore(player.getName()) > 4) {
            PlayerUtils.broadcast(PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + getScore(player.getName()) + " §7was shut down from leaving!");
        }

        User user = plugin.getUser(player);
        user.reset();

        player.teleport(plugin.getSpawn());
        score.resetScores(player.getName());

        if (player.isDead()) {
            player.spigot().respawn();
        }
    }

    /**
     * Check if the arena contains a player.
     *
     * @param player the player.
     * @return <code>True</code> if the player is in the arena, <code>false</code> otherwise.
     */
    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }
    
    private final Random rand = new Random();

    /**
     * Gives the arena kit to the given player.
     *
     * @param player the player.
     */
    public void giveKit(Player player) {
        User user = plugin.getUser(player);

        if (!user.getConfig().contains("hotbar")) {
            giveDefaultKit(player);
            return;
        }

        for (int i = 0; i < 36; i++) {
            if (!user.getConfig().contains("hotbar." + i)) {
                giveDefaultKit(player);
                return;
            }

            if (user.getConfig().getConfigurationSection("hotbar." + i) == null) {
                giveDefaultKit(player);
                return;
            }

            ItemStack item = ItemStack.deserialize(user.getConfig().getConfigurationSection("hotbar." + i).getValues(false));

            if (item == null) {
                item = new ItemStack(Material.AIR);
            }

            if (item.getType() == Material.COBBLESTONE || item.getType() == Material.WOOD) {
                if (rand.nextDouble() < 0.1666) {
                    item.setType(Material.COBBLESTONE);
                } else {
                    item.setType(Material.WOOD);
                    item.setDurability((short) rand.nextInt(6));
                }
            }
            
            player.getInventory().setItem(i, item);
        }

        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
    }

    /**
     * Gives the arena kit to the given player.
     *
     * @param player the player.
     */
    public void giveDefaultKit(Player player) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack bow = new ItemStack(Material.BOW);

        ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
        ItemStack bucket = new ItemStack(Material.WATER_BUCKET);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack axe = new ItemStack(Material.IRON_AXE);
        axe.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack shovel = new ItemStack(Material.IRON_SPADE);
        shovel.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 32);
        
        if (rand.nextDouble() >= 0.1666) {
            cobble.setType(Material.WOOD);
            cobble.setDurability((short) rand.nextInt(6));
        }

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, bow);
        player.getInventory().setItem(2, bucket);
        player.getInventory().setItem(3, pickaxe);
        player.getInventory().setItem(4, cobble);
        player.getInventory().setItem(5, gapple);
        player.getInventory().setItem(6, shovel);
        player.getInventory().setItem(7, axe);
        player.getInventory().setItem(8, food);

        player.getInventory().setItem(27, new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.WORKBENCH, 16));
        player.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 4));

        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
    }

    /**
     * Sets the score of the given player.
     *
     * @param player the player setting it for.
     * @param newScore the new score.
     */
    public void setScore(String player, int newScore) {
        Score score = killboard.getScore(player);

        score.setScore(newScore);
    }

    /**
     * Gets the given score for the given string.
     *
     * @param string the wanted string.
     * @return The score of the string.
     */
    public int getScore(String string) {
        return killboard.getScore(string).getScore();
    }

    /**
     * Reset the score of the given string.
     *
     * @param string the string resetting.
     */
    public void resetScore(String string) {
        score.resetScores(string);
    }

    /**
     * Get the main scoreboard.
     *
     * @return The scoreboard.
     */
    public Scoreboard getBoard() {
        return score;
    }

    /**
     * Get the kill board objective.
     *
     * @return The objective.
     */
    public Objective getKillBoard() {
        return killboard;
    }
}