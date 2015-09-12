package com.leontg77.uhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.cmds.AboardCommand;
import com.leontg77.uhc.cmds.ArenaCommand;
import com.leontg77.uhc.cmds.BanCommand;
import com.leontg77.uhc.cmds.BoardCommand;
import com.leontg77.uhc.cmds.BorderCommand;
import com.leontg77.uhc.cmds.BroadcastCommand;
import com.leontg77.uhc.cmds.ButcherCommand;
import com.leontg77.uhc.cmds.ClearChatCommand;
import com.leontg77.uhc.cmds.ClearInvCommand;
import com.leontg77.uhc.cmds.ClearXpCommand;
import com.leontg77.uhc.cmds.ConfigCommand;
import com.leontg77.uhc.cmds.EditCommand;
import com.leontg77.uhc.cmds.EndCommand;
import com.leontg77.uhc.cmds.FeedCommand;
import com.leontg77.uhc.cmds.GamemodeCommand;
import com.leontg77.uhc.cmds.GiveallCommand;
import com.leontg77.uhc.cmds.HOFCommand;
import com.leontg77.uhc.cmds.HealCommand;
import com.leontg77.uhc.cmds.HealthCommand;
import com.leontg77.uhc.cmds.HelpopCommand;
import com.leontg77.uhc.cmds.InvseeCommand;
import com.leontg77.uhc.cmds.KickCommand;
import com.leontg77.uhc.cmds.ListCommand;
import com.leontg77.uhc.cmds.MatchpostCommand;
import com.leontg77.uhc.cmds.MsCommand;
import com.leontg77.uhc.cmds.MsgCommand;
import com.leontg77.uhc.cmds.MuteCommand;
import com.leontg77.uhc.cmds.NearCommand;
import com.leontg77.uhc.cmds.PermaCommand;
import com.leontg77.uhc.cmds.PmCommand;
import com.leontg77.uhc.cmds.PvPCommand;
import com.leontg77.uhc.cmds.RandomCommand;
import com.leontg77.uhc.cmds.RankCommand;
import com.leontg77.uhc.cmds.ReplyCommand;
import com.leontg77.uhc.cmds.RulesCommand;
import com.leontg77.uhc.cmds.ScenarioCommand;
import com.leontg77.uhc.cmds.SethealthCommand;
import com.leontg77.uhc.cmds.SetmaxhealthCommand;
import com.leontg77.uhc.cmds.SetspawnCommand;
import com.leontg77.uhc.cmds.SkullCommand;
import com.leontg77.uhc.cmds.SpectateCommand;
import com.leontg77.uhc.cmds.SpeedCommand;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.cmds.StaffChatCommand;
import com.leontg77.uhc.cmds.StartCommand;
import com.leontg77.uhc.cmds.StatsCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.TempbanCommand;
import com.leontg77.uhc.cmds.TextCommand;
import com.leontg77.uhc.cmds.TimeLeftCommand;
import com.leontg77.uhc.cmds.TimerCommand;
import com.leontg77.uhc.cmds.TlCommand;
import com.leontg77.uhc.cmds.TpCommand;
import com.leontg77.uhc.cmds.TpsCommand;
import com.leontg77.uhc.cmds.UnbanCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.cmds.WhitelistCommand;
import com.leontg77.uhc.listeners.BlockListener;
import com.leontg77.uhc.listeners.EntityListener;
import com.leontg77.uhc.listeners.InventoryListener;
import com.leontg77.uhc.listeners.PlayerListener;
import com.leontg77.uhc.listeners.WorldListener;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for prefixes, adding recipes, enabling and disabling.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	private Settings settings = Settings.getInstance();
	private Logger logger = getLogger();
	public static Main plugin;
	
	public static BukkitRunnable countdown;
	public static Recipe headRecipe;
	public static Recipe melonRecipe;
	
	public static HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();
	public static HashMap<String, BukkitRunnable> relog = new HashMap<String, BukkitRunnable>();
	public static HashMap<String, Integer> teamKills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " is now disabled.");
		
		settings.getData().set("state", State.getState().name());
		settings.saveData();
		
		ArrayList<String> scens = new ArrayList<String>();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scens.add(scen.getName());
		}
		
		settings.getData().set("scenarios", scens);
		settings.saveData();
		
		for (Entry<String, Integer> tkEntry : teamKills.entrySet()) {
			settings.getData().set("teamkills." + tkEntry.getKey(), tkEntry.getValue());
		}
		settings.saveData();
		
		for (Entry<String, Integer> kEntry : kills.entrySet()) {
			settings.getData().set("kills." + kEntry.getKey(), kEntry.getValue());
		}
		settings.saveData();
		
		BiomeSwap.getManager().resetBiomes();
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		logger.info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		
		plugin = this;
		settings.setup();

		ScenarioManager.getInstance().setup();
		AntiStripmine.getManager().setup();
		Fireworks.getRandomizer().setup();
		Scoreboards.getManager().setup();
		BiomeSwap.getManager().setup();
		Parkour.getManager().setup();
		Arena.getManager().setup();
		Teams.getManager().setup();
		UBL.getManager().setup();
		
		State.setState(State.valueOf(settings.getData().getString("state", State.LOBBY.name())));
		addRecipes();
		
		Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WorldListener(), this);

		getCommand("aboard").setExecutor(new AboardCommand());
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("ban").setExecutor(new BanCommand());
		getCommand("board").setExecutor(new BoardCommand());
		getCommand("border").setExecutor(new BorderCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("butcher").setExecutor(new ButcherCommand());
		getCommand("clearchat").setExecutor(new ClearChatCommand());
		getCommand("clearinv").setExecutor(new ClearInvCommand());
		getCommand("clearxp").setExecutor(new ClearXpCommand());
		getCommand("config").setExecutor(new ConfigCommand());
		getCommand("edit").setExecutor(new EditCommand());
		getCommand("end").setExecutor(new EndCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("gamemode").setExecutor(new GamemodeCommand());
		getCommand("giveall").setExecutor(new GiveallCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("health").setExecutor(new HealthCommand());
		getCommand("helpop").setExecutor(new HelpopCommand());
		getCommand("hof").setExecutor(new HOFCommand());
		getCommand("invsee").setExecutor(new InvseeCommand());
		getCommand("kick").setExecutor(new KickCommand());
		getCommand("list").setExecutor(new ListCommand());
		getCommand("matchpost").setExecutor(new MatchpostCommand());
		getCommand("ms").setExecutor(new MsCommand());
		getCommand("msg").setExecutor(new MsgCommand());
		getCommand("mute").setExecutor(new MuteCommand());
		getCommand("near").setExecutor(new NearCommand());
		getCommand("perma").setExecutor(new PermaCommand());
		getCommand("pm").setExecutor(new PmCommand());
		getCommand("pvp").setExecutor(new PvPCommand());
		getCommand("random").setExecutor(new RandomCommand());
		getCommand("rank").setExecutor(new RankCommand());
		getCommand("reply").setExecutor(new ReplyCommand());
		getCommand("rules").setExecutor(new RulesCommand());
		getCommand("scenario").setExecutor(new ScenarioCommand());
		getCommand("sethealth").setExecutor(new SethealthCommand());
		getCommand("setmaxhealth").setExecutor(new SetmaxhealthCommand());
		getCommand("skull").setExecutor(new SkullCommand());
		getCommand("spectate").setExecutor(new SpectateCommand());
		getCommand("setspawn").setExecutor(new SetspawnCommand());
		getCommand("speed").setExecutor(new SpeedCommand());
		getCommand("spread").setExecutor(new SpreadCommand());
		getCommand("ac").setExecutor(new StaffChatCommand());
		getCommand("start").setExecutor(new StartCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("team").setExecutor(new TeamCommand());
		getCommand("tempban").setExecutor(new TempbanCommand());
		getCommand("text").setExecutor(new TextCommand());
		getCommand("timeleft").setExecutor(new TimeLeftCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("teamloc").setExecutor(new TlCommand());
		getCommand("tp").setExecutor(new TpCommand());
		getCommand("tps").setExecutor(new TpsCommand());
		getCommand("unban").setExecutor(new UnbanCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("whitelist").setExecutor(new WhitelistCommand());
		
		if (State.isState(State.LOBBY)) {
			File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
			
			Bukkit.getServer().setIdleTimeout(60);
		
			int totalDatafiles = 0;
			int totalStatsfiles = 0;
			
			for (File dataFiles : playerData.listFiles()) {
				dataFiles.delete();
				totalDatafiles++;
			}
			
			for (File statsFiles : stats.listFiles()) {
				statsFiles.delete();
				totalStatsfiles++;
			}

			plugin.getLogger().info("Deleted " + totalDatafiles + " player data files.");
			plugin.getLogger().info("Deleted " + totalStatsfiles + " player stats files.");
		}
		
		if (State.isState(State.INGAME)) {
			Bukkit.getServer().getPluginManager().registerEvents(new SpecInfo(), this);
		}
		
		for (Player online : PlayerUtils.getPlayers()) {	
			PlayerUtils.handlePermissions(online);
		}
		
		for (String scen : settings.getData().getStringList("scenarios")) {
			ScenarioManager.getInstance().getScenario(scen).enableScenario();
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {	
					if (Spectator.getManager().isSpectating(online) && online.getGameMode() != GameMode.SPECTATOR) {
						online.setGameMode(GameMode.SPECTATOR);
					}
					
					if (Game.getInstance().tabShowsHealthColor()) {
						ChatColor color;

						if (online.getHealth() < 6.66D) {
							color = ChatColor.RED;
						} else if (online.getHealth() < 13.33D) {
							color = ChatColor.YELLOW;
						} else {
							color = ChatColor.GREEN;
						}
					    
					    online.setPlayerListName(color + online.getName());
					}
					
					String uuid = online.getUniqueId().toString();
					
					if (online.isOp() && !(uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f") || uuid.equals("8b2b2e07-b694-4bd0-8f1b-ba99a267be41"))) {
						online.sendMessage(prefix() + "�cYou are not allowed to have OP.");
						online.setOp(false);
					}

					Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
					int percent = NumberUtils.makePercent(online.getHealth());
					
					Objective tabList = sb.getObjective("tabHealth");
					
					if (tabList != null) {
						Score score = tabList.getScore(online.getName());
						score.setScore(percent);
					}
					
					Objective bellowName = sb.getObjective("nameHealth");
					
					if (bellowName != null) {
						Score score = bellowName.getScore(online.getName());
						score.setScore(percent);
					}
				}
				
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().equals("lobby") || world.getName().equals("arena")) {
						if (world.getDifficulty() != Difficulty.PEACEFUL) {
							world.setDifficulty(Difficulty.PEACEFUL);
						}
						
						if (world.getName().equals("lobby") && world.getTime() != 18000) {
							world.setTime(18000);
						}
					} else {
						if (world.getDifficulty() != Difficulty.HARD) {
							world.setDifficulty(Difficulty.HARD);
						}
					}
				}
			}
		}, 1, 1);
	}
	
	/**
	 * Get the UHC prefix with an ending color.
	 * @param endcolor the ending color.
	 * @return The UHC prefix.
	 */
	public static String prefix() {
		String prefix = "�4�lUHC �8� �7";
		return prefix;
	}
	
	/**
	 * Adds the golden head recipe.
	 */
	@SuppressWarnings("deprecation")
	public void addRecipes() {
        ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        head.setItemMeta(meta); 

        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', new MaterialData(Material.SKULL_ITEM, (byte) 3));
        ShapedRecipe goldenmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON)).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.MELON);
        
        Bukkit.getServer().addRecipe(goldenhead);
        Bukkit.getServer().addRecipe(goldenmelon);
        
        headRecipe = goldenhead;
        melonRecipe = goldenmelon;

        plugin.getLogger().info("Golden Head recipe added.");
        plugin.getLogger().info("Golden Melon recipe added.");
	}
	
	/**
	 * The game state class.
	 * @author LeonTG77
	 */
	public enum State {
		LOBBY, SCATTER, INGAME;

		private static State currentState;
		
		/**
		 * Sets the current state to be #.
		 * @param state the state setting it to.
		 */
		public static void setState(State state) {
			Settings.getInstance().getData().set("state", state.name().toUpperCase());
			Settings.getInstance().saveData();
			
			currentState = state;
		}
		
		/**
		 * Checks if the state is #.
		 * @param state The state checking.
		 * @return True if it's the given state.
		 */
		public static boolean isState(State state) {
			return currentState == state;
		}
		
		/**
		 * Gets the current state.
		 * @return The state
		 */
		public static State getState() {
			return currentState;
		}
	}
	
	/**
	 * Border types enum class.
	 * @author LeonTG77
	 */
	public enum Border {
		NEVER, START, PVP, MEETUP;
	}
}