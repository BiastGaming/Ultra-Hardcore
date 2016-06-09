package com.leontg77.ultrahardcore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.github.paperspigot.PaperSpigotConfig;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.commands.CommandHandler;
import com.leontg77.ultrahardcore.exceptions.InvalidUserException;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.listeners.AnvilListener;
import com.leontg77.ultrahardcore.listeners.ChatListener;
import com.leontg77.ultrahardcore.listeners.LoginListener;
import com.leontg77.ultrahardcore.listeners.LogoutListener;
import com.leontg77.ultrahardcore.listeners.PlayerListener;
import com.leontg77.ultrahardcore.listeners.ProtectionListener;
import com.leontg77.ultrahardcore.listeners.PushToSpawnListener;
import com.leontg77.ultrahardcore.listeners.QuitMessageListener;
import com.leontg77.ultrahardcore.listeners.SpectatorListener;
import com.leontg77.ultrahardcore.listeners.StatsListener;
import com.leontg77.ultrahardcore.listeners.WorldListener;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.FireworkManager;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.minigames.Parkour;
import com.leontg77.ultrahardcore.minigames.listeners.KingOfTheLadderListener;
import com.leontg77.ultrahardcore.protocol.EnchantPreview;
import com.leontg77.ultrahardcore.protocol.HardcoreHearts;
import com.leontg77.ultrahardcore.protocol.OnlineCount;
import com.leontg77.ultrahardcore.protocol.SaturationConcealer;
import com.leontg77.ultrahardcore.protocol.ShadowBans;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.ubl.UBL;
import com.leontg77.ultrahardcore.ubl.UBLListener;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.world.WorldManager;
import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine;
import com.leontg77.ultrahardcore.world.biomeswap.BiomeSwap;
import com.leontg77.ultrahardcore.world.orelimiter.OreLimiter;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerConnection;

/**
 * Main class of the UHC plugin.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
    public static final String NO_PERMISSION_MESSAGE = "§cYou don't have permission.";
 
    public static final String BORDER_PREFIX = "§cBorder §8» §7";
    public static final String ALERT_PREFIX = "§6Alert §8» §7";
    public static final String STAFF_PREFIX = "§cStaff §8» §7";
    public static final String SPEC_PREFIX = "§5Spec §8» §7";
    public static final String INFO_PREFIX = "§aInfo §8» §7";
    public static final String SCEN_PREFIX = "§9Scenario §8» §7";
    public static final String PREFIX = "§4§lUHC §8» §7";
    public static final String ARROW = "§8» §7";
    
    private Settings settings;
    private Data data;

    private OreLimiter oreLimiter;
    private AntiStripmine antiSM;
    private BiomeSwap swap;

    private WorldManager worlds;
    private UBL ubl;

    private BoardManager board;
    private TeamManager teams;

    private ScatterManager scatter;
    private SpecManager spec;

    private PermissionsManager perm;
    private FireworkManager firework;

    private GUIManager gui;

    private Timer timer;
    private Game game;

    private Announcer announcer;
    private Parkour parkour;
    private Arena arena;

    private EnchantPreview enchPreview;
    private HardcoreHearts hardHearts;
    private OnlineCount counter;

    private CommandHandler cmd;
    private ScenarioManager scen;
    private FeatureManager feat;

    @Override
    public void onDisable() {
        PluginDescriptionFile file = getDescription();
        getLogger().info(file.getName() + " is now disabled.");

        PLAYER_CONNECTION_LOGGER.removeAppender(pcAppender);
        data.store(feat, teams, scen);

        try {
            swap.resetBiomes();
        } catch (Exception e) {
            getLogger().warning("Could not reset biomes!");
        }

        if (game.isState(State.NOT_RUNNING)) {
            for (User user : users.values()) {
                FileConfiguration conf = user.getConfig();
                
                if (!conf.contains("uuid")) {
                    user.getFile().delete();
                    continue;
                } 
                
                if (conf.contains("locs")) {
                    conf.set("locs", null);
                }

                user.saveConfig();
            }
        }
    }

    private static final Logger PLAYER_CONNECTION_LOGGER = (Logger) LogManager.getLogger(PlayerConnection.class);

    private final QuitMessageListener quitMsg = new QuitMessageListener();
    private Appender pcAppender;

    @Override
    public void onEnable() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
        PluginManager manager = Bukkit.getPluginManager();
        
        PluginDescriptionFile file = getDescription();
        getLogger().info(file.getName() + " v" + file.getVersion() + " is now enabled.");
        getLogger().info("The plugin was created by LeonTG77.");

        try {
            PaperSpigotConfig.warnForExcessiveVelocity = false;
        } catch (Throwable e) {
            getLogger().log(Level.WARNING, "Could not disable excessive velocity warning", e);
        }
        
        settings = new Settings(this);
        settings.setup();

        swap = new BiomeSwap(this, settings);
        swap.setup();

        oreLimiter = new OreLimiter(settings);
        antiSM = new AntiStripmine(settings);

        worlds = new WorldManager(settings);
        worlds.loadWorlds();

        board = new BoardManager(this);

        teams = new TeamManager(this, board);

        scen = new ScenarioManager(this);

        perm = new PermissionsManager(this, scen);
        firework = new FireworkManager(this);

        gui = new GUIManager(this);
        spec = new SpecManager(this, teams, scen);

        game = new Game(this, settings, gui, board, spec);
        
        board.setup(game);
        teams.setup();
        
        scatter = new ScatterManager(this, teams, game);
        
        feat = new FeatureManager(this, game, settings);

        parkour = new Parkour(this, game, settings, spec);
        parkour.setup();

        arena = new Arena(this, game, board, scatter, worlds);
        arena.setup();

        announcer = new Announcer(this, game);
        announcer.startAnnouncer();

        enchPreview = new EnchantPreview(this);
        hardHearts = new HardcoreHearts(this);
        counter = new OnlineCount(this, game);

        timer = new Timer(this, game, gui, scen, feat, board, spec);
        cmd = new CommandHandler(this);

        data = new Data(this, settings);
        
        FileUtils.updateUserFiles(this);
        BlockUtils.setPlugin(this);
        
        ubl = new UBL(this);
        ubl.reload();
        
        scen.registerScenarios(arena, game, timer, teams, spec, settings, feat, scatter, board);
        feat.registerFeatures(arena, game, timer, board, teams, spec, enchPreview, hardHearts, scen, gui);
        
        cmd.registerCommands(game, data, arena, parkour, settings, gui, board, spec, feat, scen, worlds, timer, teams, firework, scatter, ubl);
        gui.registerGUIs(game, timer, settings, feat, scen, worlds);

        data.restore(feat, teams, scen);
        
        game.setTimer(timer);
        
        ShadowBans shadow = new ShadowBans(this, settings.getConfig(), settings::saveConfig);
        shadow.loadData();
        
        protocol.addPacketListener(new SaturationConcealer(this));
        protocol.addPacketListener(shadow);
        counter.enable();

        // register all listeners.
        manager.registerEvents(shadow, this);
        manager.registerEvents(new KingOfTheLadderListener(this), this);
        
        manager.registerEvents(new AnvilListener(this), this);
        manager.registerEvents(new ChatListener(this, game, teams, spec), this);
        manager.registerEvents(new LoginListener(this, game, settings, spec, scatter, perm), this);
        manager.registerEvents(new LogoutListener(this, game, gui, spec, perm), this);
        manager.registerEvents(new PlayerListener(this, game, spec), this);
        manager.registerEvents(new ProtectionListener(game), this);
        manager.registerEvents(new PushToSpawnListener(this, parkour), this);
        manager.registerEvents(new SpectatorListener(this, game, spec, gui, feat.getFeature(NetherFeature.class)), this);
        manager.registerEvents(new StatsListener(this, arena, game, board, teams, feat.getFeature(GoldenHeadsFeature.class)), this);
        manager.registerEvents(new WorldListener(this, game, arena), this);
        manager.registerEvents(new UBLListener(ubl, game), this);

        manager.registerEvents(oreLimiter, this);
        manager.registerEvents(antiSM, this);

        manager.registerEvents(quitMsg, this);
        pcAppender = quitMsg;
        pcAppender.start();
        PLAYER_CONNECTION_LOGGER.addAppender(pcAppender);

        for (Player online : Bukkit.getOnlinePlayers()) {
            perm.addPermissions(online);
        }

        switch (game.getState()) {
        case NOT_RUNNING:
            FileUtils.deletePlayerDataAndStats(this);
            Bukkit.setIdleTimeout(60);
            break;
        case INGAME:
            manager.registerEvents(spec.getSpecInfo(), this);
            Bukkit.setIdleTimeout(10);
            break;
        default:
            Bukkit.setIdleTimeout(60);
            break;
        }
    }

    /**
     * Gets the servers tps.
     *
     * @return The servers tps.
     */
    public double getTps() {
        double tps = MinecraftServer.getServer().recentTps[0];
        String converted = NumberUtils.formatDouble(tps);

        return Double.parseDouble(converted);
    }

    /**
     * Get the amount of online players minus the spectators.
     *
     * @return The online count.
     */
    public int getOnlineCount() {
        int players = Bukkit.getOnlinePlayers().size();
        int specs = 0;

        for (String list : spec.getSpectators()) {
            if (Bukkit.getPlayer(list) != null) {
                specs++;
            }
        }

        players = players - specs;

        return players;
    }

    /**
     * Get the spawnpoint of the lobby.
     *
     * @return The lobby spawnpoint.
     */
    public Location getSpawn() {
        FileConfiguration config = settings.getConfig();

        World world = Bukkit.getWorld(config.getString("spawn.world", "lobby"));

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        double x = config.getDouble("spawn.x", 0.5);
        double y = config.getDouble("spawn.y", 33.0);
        double z = config.getDouble("spawn.z", 0.5);
        float yaw = (float) config.getDouble("spawn.yaw", 0);
        float pitch = (float) config.getDouble("spawn.pitch", 0);

        Location loc = new Location(world, x, y, z, yaw, pitch);
        return loc;
    }

    protected final File folder = new File(getDataFolder() + File.separator + "users" + File.separator);
    protected final Map<UUID, User> users = new HashMap<UUID, User>();

    /**
     * Gets the data of the given player.
     * <p>
     * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
     *
     * @param player the player.
     * @return The data instance for the player.
     */
    public User getUser(Player player) {
        if (users.containsKey(player.getUniqueId())) {
            return users.get(player.getUniqueId());
        }

        User user = new User(this, folder, player.getUniqueId(), game, gui, perm, ubl);
        users.put(player.getUniqueId(), user);

        return user;
    }

    /**
     * Gets the data of the given OFFLINE player.
     * <p>
     * If the data doesn't exist it will create a new data file and threat the player as a newly joined one.
     *
     * @param offline the offline player.
     * @return The data instance for the player.
     *
     * @throws InvalidUserException If the offline player has never joined this server.
     */
    public User getUser(OfflinePlayer offline) throws InvalidUserException {
        if (!fileExist(offline.getUniqueId())) {
            throw new InvalidUserException(offline);
        }

        if (users.containsKey(offline.getUniqueId())) {
            return users.get(offline.getUniqueId());
        }

        User user = new User(this, folder, offline.getUniqueId(), game, gui, perm, ubl);
        users.put(offline.getUniqueId(), user);

        return user;
    }

    /**
     * Check if the userdata folder has a file with the given uuid.
     *
     * @param uuid The uuid checking for.
     * @return True if it exist, false otherwise.
     */
    public boolean fileExist(UUID uuid) {
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }

        for (File file : folder.listFiles()) {
            String fileName = file.getName().substring(0, file.getName().length() - 4);

            if (fileName.equals(uuid.toString())) {
                return true;
            }
        }

        return false;
    }
}