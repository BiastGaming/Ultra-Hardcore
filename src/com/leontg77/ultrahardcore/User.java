package com.leontg77.ultrahardcore;
 
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.ubl.UBL;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * User class.
 * <p>
 * This class contains methods for setting and getting stats, ranks, mute status and getting/saving/reloading the data file etc.
 * 
 * @author LeonTG77
 */
public class User {
    private final UUID uuid;

    private final Main plugin;
    private final Game game;

    private final PermissionsManager perm;

    private final GUIManager gui;
    private final UBL ubl;

    private FileConfiguration config;
    private File file;

    /**
     * Constuctor for player data.
     * <p>
     * This will set up the data for the player and create missing data.
     */
    protected User(Main plugin, File folder, UUID uuid, Game game, GUIManager gui, PermissionsManager perm, UBL ubl) {
        this.uuid = uuid;
        
        this.plugin = plugin;
        this.game = game;

        this.perm = perm;

        this.gui = gui;
        this.ubl = ubl;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        
        if (!folder.exists()) {
            folder.mkdir();
        }
        
        file = new File(folder, uuid + ".yml");
        
        if (!file.exists()) {
            try {
                file.createNewFile();
                creating = true;
            } catch (Exception e) {
                plugin.getLogger().severe(ChatColor.RED + "Could not create " + uuid + ".yml!");
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
        
        if (creating) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                config.set("username", player.getName());
                config.set("uuid", player.getUniqueId().toString());
                config.set("ip", player.getAddress().getAddress().getHostAddress());
            }

            config.set("firstjoined", new Date().getTime());
            config.set("lastlogin", new Date().getTime());
            config.set("lastlogout", -1l);
            config.set("rank", Rank.DEFAULT.name());

            config.set("muted.status", false);
            config.set("muted.reason", "NOT_MUTED");
            config.set("muted.time", -1);

            for (Stat stats : Stat.values()) {
                config.set("stats." + stats.name().toLowerCase(), 0);
            }

            saveConfig();
        }
    }

    private boolean creating = false;

    /**
     * Get the given player's ping.
     *
     * @return the players ping
     */
    public int getPing() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return -1;
        }

        final CraftPlayer craft = (CraftPlayer) player;

        return craft.getHandle().ping;
    }

    /**
     * Check if the user hasn't been welcomed to the server.
     *
     * @return True if he hasn't, false otherwise.
     */
    public boolean isNew() {
        if (creating) {
            creating = false;
            return true;
        }

        return creating;
    }

    /**
     * Get the player class for the user.
     *
     * @return The player class.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Get the uuid for the user.
     *
     * @return The uuid.
     */
    public String getUUID() {
        return uuid.toString();
    }

    /**
     * Get the file for the player.
     *
     * @return The file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the configuration file for the player.
     *
     * @return The configuration file.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Save the config file.
     */
    public void saveConfig() {
        try {
            for (FileConfiguration file : FileUtils.getUserFiles()) {
                if (file.getString("uuid", "none").equals(config.getString("uuid", "none"))) {
                    FileUtils.getUserFiles().remove(file);
                    break;
                }
            }

            config.save(file);

            FileUtils.getUserFiles().add(config);
        } catch (Exception e) {
            plugin.getLogger().severe(ChatColor.RED + "Could not save " + file.getName() + "!");
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Reload the config file.
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Set the rank for the player.
     *
     * @param rank The new rank.
     */
    public void setRank(Rank rank) {
        config.set("rank", rank.name());
        saveConfig();

        GameInfoGUI info = gui.getGUI(GameInfoGUI.class);

        info.updateStaff();

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            perm.removePermissions(player);
            perm.addPermissions(player);
        }
    }

    /**
     * Get the rank of the player.
     *
     * @return the rank.
     */
    public Rank getRank() {
        Rank rank;

        try {
            rank = Rank.valueOf(config.getString("rank"));
        } catch (Exception e) {
            rank = Rank.DEFAULT;
        }

        return rank;
    }

    /**
     * Get the color of the rank the player has.
     *
     * @return The rank color.
     */
    public String getRankColor() {
        if (game.isRecordedRound()) {
            return "§7";
        }

        switch (getRank()) {
        case DONATOR:
            return "§a";
        case HOST:
        case TRIAL:
            return "§c";
        case OWNER:
            if (uuid.toString().equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
                return "§3";
            } else {
                return "§4";
            }
        case STAFF:
            return "§a";
        default:
            return "§7";
        }
    }

    /**
     * Get all possible alt accounts of the user.
     * <p>
     * These are colored: Red = Banned, Green = Online and Gray = Offline.
     *
     * @return A list of alt accounts.
     */
    public Set<String> getAlts() {
        Set<String> altList = new HashSet<String>();

        String thisName = config.getString("username", "none1");
        List<String> thisIPs = config.getStringList("ips");

        BanList banlist = Bukkit.getBanList(Type.NAME);

        for (FileConfiguration file : FileUtils.getUserFiles()) {
            String name = file.getString("username", "none2");
            List<String> otherIPs = file.getStringList("ips");

            if (!thisIPs.stream().anyMatch(otherIPs::contains)) {
                continue;
            }

            if (thisName.equals(name)) {
                continue;
            }

            Player check = Bukkit.getPlayerExact(name);
            UUID uuid = UUID.fromString(file.getString("uuid", UUID.randomUUID().toString()));

            if (ubl.isBanned(uuid)) {
                altList.add("§6" + name + "§8");
            }
            else if (banlist.getBanEntry(name) != null) {
                altList.add("§4" + name + "§8");
            }
            else if (check != null) {
                altList.add("§a" + name + "§8");
            }
            else {
                altList.add("§c" + name + "§8");
            }
        }

        return altList;
    }

    /**
     * Set the death location of the player.
     *
     * @param loc The death loc.
     */
    public void setDeathLoc(Location loc) {
        if (loc == null) {
            config.set("locs.death", null);
            saveConfig();
            return;
        }

        config.set("locs.death.world", loc.getWorld().getName());
        config.set("locs.death.x", loc.getX());
        config.set("locs.death.y", loc.getY());
        config.set("locs.death.z", loc.getZ());
        config.set("locs.death.yaw", loc.getYaw());
        config.set("locs.death.pitch", loc.getPitch());
        saveConfig();
    }

    /**
     * Get the death location of the player.
     *
     * @return The death location.
     */
    public Location getDeathLoc() {
        if (!config.contains("locs.death")) {
            return null;
        }

        World world = Bukkit.getWorld(config.getString("locs.death.world"));

        if (world == null) {
            return null;
        }

        double x = config.getDouble("locs.death.x");
        double y = config.getDouble("locs.death.y");
        double z = config.getDouble("locs.death.z");
        float yaw = (float) config.getDouble("locs.death.yaw", 0);
        float pitch = (float) config.getDouble("locs.death.pitch", 0);

        Location loc = new Location(world, x, y, z, yaw, pitch);

        return loc;
    }

    /**
     * Set the last location of the player.
     *
     * @param loc The last loc.
     */
    public void setLastLoc(Location loc) {
        if (loc == null) {
            config.set("locs.last", null);
            saveConfig();
            return;
        }

        config.set("locs.last.world", loc.getWorld().getName());
        config.set("locs.last.x", loc.getX());
        config.set("locs.last.y", loc.getY());
        config.set("locs.last.z", loc.getZ());
        config.set("locs.last.yaw", loc.getYaw());
        config.set("locs.last.pitch", loc.getPitch());
        saveConfig();
    }

    /**
     * Get the last location of the player.
     *
     * @return The last location.
     */
    public Location getLastLoc() {
        if (!config.contains("locs.last")) {
            return null;
        }

        World world = Bukkit.getWorld(config.getString("locs.last.world"));

        if (world == null) {
            return null;
        }

        double x = config.getDouble("locs.last.x");
        double y = config.getDouble("locs.last.y");
        double z = config.getDouble("locs.last.z");
        float yaw = (float) config.getDouble("locs.last.yaw", 0);
        float pitch = (float) config.getDouble("locs.last.pitch", 0);

        Location loc = new Location(world, x, y, z, yaw, pitch);

        return loc;
    }

    /**
     * Set the fixed scatter location to be used for this player in the
     * {@link com.leontg77.ultrahardcore.managers.ScatterManager}.
     * @param x The x-coordinate the player should always be scattered at
     * @param z The z-coordinate the player should always be scattered at
     */
    public void setFixedScatterLocation(int x, int z) {
        config.set("locs.scatter.x", x);
        config.set("locs.scatter.z", z);
        saveConfig();
    }

    /**
     * Delete the fixed scatter location for this player, causing him to be scattered normally again.
     */
    public void deleteFixedScatterLocation() {
        config.set("locs.scatter", null);
        saveConfig();
    }

    /**
     * Get the fixed scatter location for the player in the given world with default parameters.
     * See {@link #getFixedScatterLocation(World, int, int)}.
     * @param world The world to scatter the player in
     * @return The fixed scatter location, or null if it's not set, invalid for the given world
     * or the maximum attempts have been reached.
     */
    public Location getFixedScatterLocation(World world) {
        return getFixedScatterLocation(world, 50, 50);
    }

    /**
     * Get the fixed scatter location for the player in the given world.
     * @param world The world to scatter the player in
     * @param maximumOffset The maximum distance the player should be randomly offset
     * @param maximumAttempts The maximum attempts of getting the scatter location
     * @return The fixed scatter location, or null if it's not set, invalid for the given world
     * or the maximum attempts have been reached.
     */
    public Location getFixedScatterLocation(World world, int maximumOffset, int maximumAttempts) {
        if (!config.contains("locs.scatter")) {
            return null;
        }

        int baseX = config.getInt("locs.scatter.x");
        int baseZ = config.getInt("locs.scatter.z");
        
        Random random = new Random();
        Location location;
        
        int attempts = 0;
        
        while (true) {
            if (attempts++ > maximumAttempts) {
                return null;
            }

            int offsetX = random.nextInt(maximumOffset*2) - maximumOffset + 1;
            int offsetZ = random.nextInt(maximumOffset*2) - maximumOffset + 1;

            location = new Location(world, baseX + offsetX + 0.5, 255, baseZ + offsetZ + 0.5);
            
            if (LocationUtils.isOutsideOfBorder(location)) {
                continue;
            }

            if (!ScatterManager.isValid(location)) {
                continue;
            }

            int y = LocationUtils.highestTeleportableYAtLocation(location);
            
            if (y == -1) {
                return null;
            }

            location.setY(y + 1);
            
            return location;
        }
    }

    private static final String IGNORE_PATH = "ignoreList";

    /**
     * Start ignoring the given player.
     *
     * @param player The player to ignore
     */
    public void ignore(Player player) {
        List<String> ignoreList = config.getStringList(IGNORE_PATH);
        ignoreList.add(player.getUniqueId().toString());

        config.set(IGNORE_PATH, ignoreList);
        saveConfig();
    }

    /**
     * Stop ignoring the given player.
     *
     * @param player The player to stop ignoring
     */
    public void unIgnore(Player player) {
        List<String> ignoreList = config.getStringList(IGNORE_PATH);
        ignoreList.remove(player.getUniqueId().toString());

        config.set(IGNORE_PATH, ignoreList);
        saveConfig();
    }

    /**
     * Check if the this User is ignoring the given player.
     *
     * @param player The player checking.
     * @return True if he is, false otherwise.
     */
    public boolean isIgnoring(Player player) {
        if (getRank().getLevel() >= Rank.STAFF.getLevel()) {
            return false;
        }

        User other = plugin.getUser(player);

        if (other.getRank().getLevel() >= Rank.STAFF.getLevel()) {
            return false;
        }

        return config.getStringList(IGNORE_PATH).contains(player.getUniqueId().toString());
    }

    /**
     * Mute the user.
     *
     * @param reason The reason of the mute.
     * @param unmute The date of unmute, null if permanent.
     */
    public void mute(String reason, Date unmute) {
        config.set("muted.status", true);
        config.set("muted.reason", reason);

        if (unmute == null) {
            config.set("muted.time", -1);
        } else {
            config.set("muted.time", unmute.getTime());
        }

        saveConfig();
    }

    /**
     * Unmute the user.
     */
    public void unmute() {
        config.set("muted.status", false);
        config.set("muted.reason", "NOT_MUTED");
        config.set("muted.time", -1);
        saveConfig();
    }

    /**
     * Check if the player is muted.
     *
     * @return <code>true</code> if the player is muted, <code>false</code> otherwise.
     */
    public boolean isMuted() {
        if (game.isRecordedRound() || game.isPrivateGame()) {
            return false;
        }

        Date date = new Date();

        // if the mute isnt permanent (perm == -1) and their mute time experied, return false and unmute.
        if (getMuteExpiration() != null && getMuteExpiration().getTime() < date.getTime()) {
            unmute();
        }

        return config.getBoolean("muted.status", false);
    }

    /**
     * Get the reason the player is muted.
     *
     * @return The reason of the mute, null if not muted.
     */
    public String getMutedReason() {
        return config.getString("muted.reason", "NOT_MUTED");
    }

    /**
     * Get the time in milliseconds for the unmute.
     *
     * @return The unmute time.
     */
    public Date getMuteExpiration() {
        final long unmute = config.getLong("muted.time", -1);

        if (unmute == -1) {
            return null;
        }

        return new Date(unmute);
    }

    /**
     * Set the given stat to a new value
     *
     * @param stat The stat setting.
     * @param value The new value.
     */
    public void setStat(Stat stat, double value) {
        if (game.isRecordedRound() || game.isPrivateGame()) {
            return;
        }

        final String statName = stat.name().toLowerCase();

        if (stat.isMinigameStat()) {
            if (!Bukkit.hasWhitelist()) {
                config.set("stats." + statName, value);
                saveConfig();
            }
        } else {
            if (game.isState(State.INGAME) || stat == Stat.WINS || stat == Stat.GAMESPLAYED) {
                config.set("stats." + statName, value);
                saveConfig();
            }
        }
    }

    /**
     * Increase the given stat by 1.
     *
     * @param stat the stat increasing.
     */
    public void increaseStat(Stat stat) {
        setStat(stat, getStatDouble(stat) + 1);
    }

    /**
     * Get the amount from the given stat as a int.
     *
     * @param stat the stat getting.
     * @return The amount in a int form.
     */
    public int getStat(Stat stat) {
        return config.getInt("stats." + stat.name().toLowerCase(), 0);
    }

    /**
     * Get the amount from the given stat as a double.
     *
     * @param stat the stat getting.
     * @return The amount in a double form.
     */
    public double getStatDouble(Stat stat) {
        return config.getDouble("stats." + stat.name().toLowerCase(), 0);
    }

    /**
     * Reset the players health, food, xp, inventory and effects.
     */
    public void reset() {
        resetHealth();
        resetFood();
        resetExp();
        resetInventory();
        resetEffects();
    }

    /**
     * Reset the players effects.
     */
    public void resetEffects() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        Collection<PotionEffect> effects = player.getActivePotionEffects();

        for (PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }
    }

    /**
     * Reset the players health.
     */
    public void resetHealth() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        player.setHealth(player.getMaxHealth());
    }

    /**
     * Reset the players food.
     */
    public void resetFood() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        player.setSaturation(5.0F);
        player.setExhaustion(0F);
        player.setFoodLevel(20);
    }

    /**
     * Reset the players xp.
     */
    public void resetExp() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0F);
    }

    /**
     * Reset the players inventory.
     */
    public void resetInventory() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        PlayerInventory inv = player.getInventory();

        inv.clear();
        inv.setArmorContents(null);
        player.setItemOnCursor(new ItemStack(Material.AIR));

        InventoryView openInventory = player.getOpenInventory();
        
        if (openInventory.getType() == InventoryType.CRAFTING) {
            openInventory.getTopInventory().clear();
        }
    }

    /**
     * The ranking enum class.
     * 
     * @author LeonTG77
     */
    public enum Rank {
        DEFAULT(1), DONATOR(2), SPEC(3), STAFF(4), TRIAL(5), HOST(6), OWNER(7);

        int level;

        private Rank(int level) {
            this.level = level;
        }

        /**
         * Get the level of the rank.
         * <p>
         * It goes in order from 1 to 7 with 7 being the highest rank and 1 being the lowest.
         *
         * @return The level.
         */
        public int getLevel() {
            return level;
        }
    }
    
    /**
     * The stats enum class.
     * 
     * @author LeonTG77
     */
    public enum Stat {
        WINS("Wins", false),
        GAMESPLAYED("Games played", false),
        KILLS("Kills", false),
        DEATHS("Deaths", false),
        DAMAGETAKEN("Damage taken", false),
        KILLSTREAK("Highest Killstreak", false),
        GOLDENAPPLESEATEN("Golden Apples Eaten", false),
        GOLDENHEADSEATEN("Golden Heads Eaten", false),
        HEARTSHEALED("Hearts Healed", false),
        HORSESTAMED("Horses Tamed", false),
        WOLVESTAMED("Wolves Tamed", false),
        CATSTAMED("Cats Tamed", false),
        POTIONS("Potions Drunk", false),
        NETHER("Went to Nether", false),
        BLOCKS("Blocks Mined", false),
        DIAMONDS("Mined diamonds", false),
        GOLD("Mined gold", false),
        HOSTILEMOBKILLS("Killed a monster", false),
        ANIMALKILLS("Killed an animal", false),
        LONGESTSHOT("Longest Shot", false),
        ARROWSHOTS("Arrows Shot", false),
        LEVELS("Levels Earned", false),
        IRONMAN("Iron Man", false),
        FIRSTBLOOD("First Blood", false),
        FIRSTDAMAGE("First Damage", false),
        FIRSTDEATH("First Death", false),
        PVEDAMAGEDEALT("PvE Damage Dealth", false),
        PVPDAMAGEDEALT("PvP Damage Dealth", false),
        FALLDAMAGE("Fall Damage", false),
        MELEEHITS("Melee Hits", false),
        BOWHITS("Bow hits", false),
        RODUSES("Rod Uses", false),
        SNOWBALLSHOTS("Snowball shots", false),
        EGGSTHROWN("Egg Throws", false),
        GLOWSTONE("Mined glowstone", false),
        PLACED("Blocks Placed", false),
        // Minigame Stats....
        BESTPARKOURTIME("Best Parkour Time", true),
        TIMES_KOTL("The King (Of the ladder)", true),
        ARENAKILLS("Arena Kills", true),
        ARENADEATHS("Arena Deaths", true),
        ARENAKILLSTREAK("Highest Arena Killstreak", true);

        private final boolean minigameStat;
        private final String name;

        private Stat(String name, boolean minigameStat) {
            this.minigameStat = minigameStat;
            this.name = name;
        }

        /**
         * Get the name of the stat.
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Check if the stat is a minigame stat.
         * 
         * @return True if it is, false otherwise.
         */
        public boolean isMinigameStat() {
            return minigameStat;
        }
    }
}