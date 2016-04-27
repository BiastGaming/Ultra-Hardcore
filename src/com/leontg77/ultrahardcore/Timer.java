package com.leontg77.ultrahardcore;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import com.leontg77.ultrahardcore.commands.game.TimerCommand;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.pvp.StalkingFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Astrophobia;
import com.leontg77.ultrahardcore.scenario.scenarios.DragonRush;
import com.leontg77.ultrahardcore.scenario.scenarios.Kings;
import com.leontg77.ultrahardcore.scenario.scenarios.SlaveMarket;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.EntityUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * The timer class for all the runnables
 * <p>
 * This class contains methods for starting countdowns, timers, RR timer and RR countdown.
 * 
 * @author LeonTG77
 */
public class Timer {
	private final Main plugin;
	
	private final GUIManager gui;
	private final Game game;
	
	private final ScenarioManager scen;
	private final FeatureManager feat;
	
	private final BoardManager board;
	private final SpecManager spec;
	
	public Timer(Main plugin, Game game, GUIManager gui, ScenarioManager scen, FeatureManager feat, BoardManager board, SpecManager spec) {
		this.plugin = plugin;
		
		this.game = game;
		this.gui = gui;
		
		this.scen = scen;
		this.feat = feat;
		
		this.board = board;
		this.spec = spec;
	}
 
	private BukkitRunnable taskSeconds;
	private BukkitRunnable taskMinutes;

	private int time;
	private int pvp;
	private int meetup;
	
	private int timeInSeconds;
	private int pvpInSeconds;
	private int meetupInSeconds;
	
	/**
	 * Set the time since the game started.
	 * 
	 * @param time The new time.
	 */
	public void setTimeSinceStart(int time) {
		this.timeInSeconds = (time * 60);
		this.time = time;
	}

	/**
	 * Get the time since the game started.
	 * 
	 * @return The time.
	 */
	public int getTimeSinceStart() {
		return time;
	}
	
	/**
	 * Get the time in seconds since the game started.
	 * 
	 * @return The time in seconds.
	 */
	public int getTimeSinceStartInSeconds() {
		return timeInSeconds;
	}
	
	/**
	 * Set the time until pvp enables.
	 * 
	 * @param pvp The new time.
	 */
	public void setPvP(int pvp) {
		this.pvpInSeconds = (pvp * 60);
		this.pvp = pvp;
	}

	/**
	 * Get the time until pvp enables.
	 * 
	 * @return The time.
	 */
	public int getPvP() {
		return pvp;
	}
	
	/**
	 * Get the time in seconds until pvp enables.
	 * 
	 * @return The time in seconds.
	 */
	public int getPvPInSeconds() {
		return pvpInSeconds;
	}
	
	/**
	 * Set the time until meetup.
	 * 
	 * @param meetup The new time.
	 */
	public void setMeetup(int meetup) {
		this.meetupInSeconds = (meetup * 60);
		this.meetup = meetup;
	}

	/**
	 * Get the time until meetup.
	 * 
	 * @return The time.
	 */
	public int getMeetup() {
		return meetup;
	}
	
	/**
	 * Get the time in seconds until meetup.
	 * 
	 * @return The time in seconds.
	 */
	public int getMeetupInSeconds() {
		return meetupInSeconds;
	}
	
	/**
	 * Start the countdown for the game.
	 */
	public void start() {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 30 &7Game starting in &8»&a");
		
		for (int i = 0; i < 150; i++) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.sendMessage("§0");
			}
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "The game will start in §a30§7 seconds.");
		PlayerUtils.broadcast(Main.PREFIX + "Opening game info inventory in §a5§7 seconds.");

		new BukkitRunnable() {
			public void run() {
				GameInfoGUI info = gui.getGUI(GameInfoGUI.class);
				
				for (Player online : game.getPlayers()) {
					online.openInventory(info.get());
				}
			}
		}.runTaskLater(plugin, 100);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "This is a §a" + game.getAdvancedTeamSize(false, true) + game.getScenarios());
			}
		}.runTaskLater(plugin, 250);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "Use §a/scen §7to view scenario information about this game.");
			}
		}.runTaskLater(plugin, 300);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "If you want to find the matchpost, use §a/post");
			}
		}.runTaskLater(plugin, 350);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "If you have any questions, use §a/uhc§7! If that didn't help ask us with §a/helpop§7!");
			}
		}.runTaskLater(plugin, 400);

		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§45", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "The game starting in §45.");
			}
		}.runTaskLater(plugin, 500);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§c4", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "The game starting in §c4.");
			}
		}.runTaskLater(plugin, 520);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§63", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "The game starting in §63.");
			}
		}.runTaskLater(plugin, 540);

		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "The game starting in §e2.");
			}
		}.runTaskLater(plugin, 560);

		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "The game starting in §a1.");
			}
		}.runTaskLater(plugin, 580);
		
		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				PlayerUtils.broadcast(Main.PREFIX + "The game has started, Good luck & Have Fun!");
				PlayerUtils.broadcast(Main.PREFIX + "PvP will be enabled in: §a" + game.getPvP() + " minutes.");
				if (scen.getScenario(DragonRush.class).isEnabled()) {
					PlayerUtils.broadcast(Main.PREFIX + "The dragon wins after: §a" + game.getMeetup() + " minutes.");
				} else {
					PlayerUtils.broadcast(Main.PREFIX + "Meetup is in: §a" + game.getMeetup() + " minutes.");
				}
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");

				Bukkit.getPluginManager().registerEvents(spec.getSpecInfo(), plugin);
				
				State.setState(State.INGAME);

				game.setPregameBoard(false);
				game.setArenaBoard(false);
				
				for (String entry : board.getBoard().getEntries()) {
					board.resetScore(entry);
				}
				
				board.setScore("§8» §c§oPvE", 1);
				board.setScore("§8» §c§oPvE", 0);
				
				pvp = game.getPvP();
				meetup = game.getMeetup();
				
				pvpInSeconds = (pvp * 60);
				meetupInSeconds = (meetup * 60);
				
				Bukkit.getServer().setIdleTimeout(10);
				timer();
				
				for (World world : game.getWorlds()) {
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					world.setTime(0);
					
					world.setGameRuleValue("doDaylightCycle", "true");
					world.setThundering(false);
					world.setStorm(false);
					
					world.setSpawnFlags(false, true);
					
					for (Entity mob : world.getEntities()) {
						if (EntityUtils.isButcherable(mob.getType())) {
							mob.remove();
						}
					}
				}
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(20));
					
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);

					online.awardAchievement(Achievement.OPEN_INVENTORY);
					online.removeAchievement(Achievement.MINE_WOOD);
					
					spec.getSpecInfo().getTotal(online).clear();
					
					User user = plugin.getUser(online);
					
					if (spec.isSpectating(online)) {
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating & catching cheaters!", 1, 20, 1);
					} else {
						PacketUtils.sendTitle(online, "§aGo!", "§7Good luck & Have fun!", 1, 20, 1);
						
						if (online.getGameMode() != GameMode.SURVIVAL) {
							online.setGameMode(GameMode.SURVIVAL);
						}
					}

					user.resetEffects();
					user.resetHealth();
					user.resetFood();
					user.resetExp();
					
					final Kings kings = scen.getScenario(Kings.class);

					if (kings.isEnabled() && kings.getKings().contains(online.getName())) {
		        		online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, NumberUtils.TICKS_IN_999_DAYS, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, NumberUtils.TICKS_IN_999_DAYS, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, NumberUtils.TICKS_IN_999_DAYS, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, NumberUtils.TICKS_IN_999_DAYS, 0));
					} else {
						online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100));
					}
					
					if (spec.isSpectating(online)) {
						continue;
					}
					
					if (scen.getScenario(SlaveMarket.class).isEnabled()) {
						PlayerInventory inv = online.getInventory();

						for (ItemStack item : inv.getContents()) {
							if (item == null) {
								continue;
							}
							
			 				if (item.getType() == Material.DIAMOND) {
								continue;
							}
							
							inv.removeItem(item);	
						}
						
				        inv.setArmorContents(null);
				        online.setItemOnCursor(new ItemStack(Material.AIR));

				        InventoryView openInventory = online.getOpenInventory();
				        
				        if (openInventory.getType() == InventoryType.CRAFTING) {
				            openInventory.getTopInventory().clear();
				        }
					} else {
						user.resetInventory();
					}
				}
				
				Bukkit.getPluginManager().callEvent(new GameStartEvent());
			}
		}.runTaskLater(plugin, 600);
	}
	
	/**
	 * Start the timers.
	 */
	public void timer() {
		stopTimers();
		
		taskMinutes = new BukkitRunnable() {
			public void run() {
				time++;
				pvp--;
				meetup--;
				
				if (time == 1 && !game.isPrivateGame()) {
					PlayerUtils.broadcast(Main.PREFIX + "The chat has been enabled.");
					game.setMuted(false);
				}
				
				if (time == 2) {
					for (World world : game.getWorlds()) {
						world.setSpawnFlags(true, true);
					}
					
					PlayerUtils.broadcast(Main.PREFIX + "Monsters will now start spawning.");
				}

				if (pvp == 0) {
					PlayerUtils.broadcast(Main.PREFIX + "PvP/iPvP has been enabled.");
					Bukkit.getPluginManager().callEvent(new PvPEnableEvent());
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						PacketUtils.sendTitle(online, "", "§4PvP has been enabled!", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
					}
					
					for (World world : game.getWorlds()) {
						world.setPVP(true);
					}
				}
				
				if (meetup == 0) {
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					PlayerUtils.broadcast(" ");
					if (scen.getScenario(DragonRush.class).isEnabled()) {
						PlayerUtils.broadcast(ChatColor.RED + " The time has run out and the dragon has won!");
					} else {
						PlayerUtils.broadcast(ChatColor.RED + " Meetup is now, head to 0,0!");
					}
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						online.playSound(online.getLocation(), Sound.WITHER_DEATH, 1, 1);
					}

					for (World world : game.getWorlds()) {
						world.setThundering(false);
						world.setStorm(false);
					}

					if (!scen.getScenario(Astrophobia.class).isEnabled()) {
						for (World world : game.getWorlds()) {
							world.setGameRuleValue("doDaylightCycle", "false");
							world.setTime(6000);
						}
						
						PlayerUtils.broadcast(Main.PREFIX + "Permaday has been activated.");
					}
					
					Bukkit.getPluginManager().callEvent(new MeetupEvent());
				}
				
				if (meetup > 0) {
					String meetupToString = String.valueOf(meetup);
					
					if (meetupToString.equals("1") || meetupToString.endsWith("5") || meetupToString.endsWith("0")) {
						if (scen.getScenario(DragonRush.class).isEnabled()) {
							PlayerUtils.broadcast(Main.PREFIX + "The dragon wins in §a" + DateUtils.advancedTicksToString(meetup * 60) + "§7.");
						} else {
							PlayerUtils.broadcast(Main.PREFIX + "Meetup is in §a" + DateUtils.advancedTicksToString(meetup * 60) + "§7.");
						}
						
						if (pvp != 0) {
							for (Player online : Bukkit.getOnlinePlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
							}
						}
						
						if (meetupToString.equals("1") && !scen.getScenario(DragonRush.class).isEnabled()) {
							PlayerUtils.broadcast(Main.PREFIX + "Start preparing to head to 0,0.");
							return;
						}
						
						if (game.isMovedMiddle() && meetupToString.equals("10")) {
							PlayerUtils.broadcast(Main.PREFIX + "You can now use §a/mucoords§7 to get the meetup coords.");
							return;
						}
					}
				}
				
				if (pvp > 0) {
					String pvpToString = String.valueOf(pvp);
					
					if (pvpToString.equals("1") || pvpToString.endsWith("5") || pvpToString.endsWith("0")) {
						PlayerUtils.broadcast(Main.PREFIX + "PvP will be enabled in §a" + DateUtils.advancedTicksToString(pvp * 60) + "§7.");
						PlayerUtils.broadcast(Main.PREFIX + "Reminder: Stalking is " + feat.getFeature(StalkingFeature.class).getStalkingRule().getMessage().toLowerCase() + "§7.");

						if (pvp != 0) {
							for (Player online : Bukkit.getOnlinePlayers()) {
								online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
							}
						}
					}
				}
			}
		};
		
		taskMinutes.runTaskTimer(plugin, 1200, 1200);
		
		taskSeconds = new BukkitRunnable() {
			public void run() {
				timeInSeconds++;
				pvpInSeconds--;
				meetupInSeconds--;
				
				int finalHeal = 20 - timeInSeconds;
				
				if (timeInSeconds == 20) {
					PlayerUtils.broadcast(Main.PREFIX + "Final heal has been given.");
					PlayerUtils.broadcast(Main.PREFIX + "Do not ask for another one.");
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						PacketUtils.sendTitle(online, "§6Final heal!", "§7Do not ask for another one", 5, 10, 5);
						
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
						
						User user = plugin.getUser(online);
						user.resetHealth();
						user.resetFood();
						
						online.setFireTicks(0);
					}
					
					Bukkit.getPluginManager().callEvent(new FinalHealEvent());
				}
				
				if (timeInSeconds < 20) {
					if (TimerCommand.isRunning()) {
						return;
					}
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(finalHeal));
					}
				} else if (pvpInSeconds > 0) {
					if (TimerCommand.isRunning()) {
						return;
					}
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						PacketUtils.sendAction(online, "§7PvP is enabled in §8» §a" + DateUtils.ticksToString(pvpInSeconds));
					}
				} else if (meetupInSeconds > 0) {
					if (TimerCommand.isRunning()) {
						return;
					}
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						if (scen.getScenario(DragonRush.class).isEnabled()) {
							PacketUtils.sendAction(online, "§7Dragon wins in §8» §a" + DateUtils.ticksToString(meetupInSeconds));
						} else {
							PacketUtils.sendAction(online, "§7Meetup is in §8» §a" + DateUtils.ticksToString(meetupInSeconds));
						}
					}
				} else {
					if (TimerCommand.isRunning()) {
						return;
					}
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						if (scen.getScenario(DragonRush.class).isEnabled()) {
							PacketUtils.sendAction(online, "§8» §6The dragon has won! §8«");
						} else {
							PacketUtils.sendAction(online, "§8» §6Meetup is now! §8«");
						}
					}
				}
			}
		};
		
		taskSeconds.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 * Start the countdown for the recorded round.
	 */
	public void startRR() {
		for (int i = 0; i < 150; i++) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.sendMessage("§0");
			}
		}
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§45", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §45.");
			}
		}.runTaskLater(plugin, 20);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§c4", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §c4.");
			}
		}.runTaskLater(plugin, 40);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§63", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §63.");
			}
		}.runTaskLater(plugin, 60);

		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §e2.");
			}
		}.runTaskLater(plugin, 80);

		new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §a1.");
			}
		}.runTaskLater(plugin, 100);
		
		new BukkitRunnable() {
			public void run() {
				PluginManager manager = Bukkit.getPluginManager();
				manager.registerEvents(spec.getSpecInfo(), plugin);
				
				State.setState(State.INGAME);
				game.setArenaBoard(false);
				
				time = 20;
				pvp = 0;
				meetup = 1;
				
				timeInSeconds = 30;
				
				timerRR();

				Bukkit.getServer().setIdleTimeout(10);
				
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				PlayerUtils.broadcast(Main.PREFIX + "The game has started!");
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				
				game.setMuted(false);
				
				for (World world : game.getWorlds()) {
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					world.setTime(0);
					
					world.setGameRuleValue("doDaylightCycle", "true");
					world.setThundering(false);
					world.setStorm(false);
					
					world.setSpawnFlags(false, true);
					
					for (Entity mob : world.getEntities()) {
						if (EntityUtils.isButcherable(mob.getType())) {
							mob.remove();
						}
					}
				}
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					for (Achievement a : Achievement.values()) {
						if (online.hasAchievement(a)) {
							online.removeAchievement(a);
						}
					}
					
					spec.getSpecInfo().getTotal(online).clear();
					
					if (spec.isSpectating(online)) {
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
					} else {
						PacketUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
						
						if (online.getGameMode() != GameMode.SURVIVAL) {
							online.setGameMode(GameMode.SURVIVAL);
						}
					}
					
					final User user = plugin.getUser(online);

					user.resetEffects();
					user.resetHealth();
					user.resetFood();
					user.resetExp();
					
					final Kings kings = scen.getScenario(Kings.class);

					if (kings.isEnabled() && kings.getKings().contains(online.getName())) {
		        		online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
					} else {
						online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100));
					}
					
					if (spec.isSpectating(online)) {
						continue;
					}
					
					if (scen.getScenario(SlaveMarket.class).isEnabled()) {
						final PlayerInventory inv = online.getInventory();

						for (ItemStack item : inv.getContents()) {
							if (item == null) {
								continue;
							}
							
							if (item.getType() == Material.DIAMOND) {
								continue;
							}
							
							inv.removeItem(item);	
						}
						
				        inv.setArmorContents(null);
				        online.setItemOnCursor(new ItemStack(Material.AIR));

				        final InventoryView openInventory = online.getOpenInventory();
				        
				        if (openInventory.getType() == InventoryType.CRAFTING) {
				            openInventory.getTopInventory().clear();
				        }
					} else {
						user.resetInventory();
					}
				}
				
				Bukkit.getPluginManager().callEvent(new GameStartEvent());
			}
		}.runTaskLater(plugin, 120);
		
		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "Final heal has been given.");
				Bukkit.getPluginManager().callEvent(new FinalHealEvent());
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendTitle(online, "§6Final heal!", "", 5, 10, 5);
					online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					
					final User user = plugin.getUser(online);
					user.resetHealth();
					user.resetFood();
					
					for (World world : game.getWorlds()) {
						world.setSpawnFlags(true, true);
					}
				}
			}
		}.runTaskLater(plugin, 380);
	}
	
	/**
	 * Start the recorded round timers.
	 */
	public void timerRR() {
		stopTimers();
		
		taskMinutes = new BukkitRunnable() {
			public void run() {
				// pvp is used as time passed.
				pvp++;
				time--;
				
				if (time == 0) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a" + meetup + " §8| §7Start of episode §a" + (meetup + 1));		
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					
					// time is used for time until next ep.
					time = 20;
					// meetup is used current episode.
					meetup++;
				}
				
				if (pvp == 20) {
					PlayerUtils.broadcast(Main.PREFIX + "PvP has been enabled!");
					Bukkit.getPluginManager().callEvent(new PvPEnableEvent());
					
					for (World world : game.getWorlds()) {
						world.setPVP(true);
					}
				}
				
				if (pvp == 100) {
					PlayerUtils.broadcast(Main.PREFIX + "Permaday has been activated!");
					
					for (World world : game.getWorlds()) {
						world.setGameRuleValue("doDaylightCycle", "false");
						world.setTime(6000);
					}
					
					if (game.isMovedMiddle()) {
						PlayerUtils.broadcast(Main.PREFIX + "You can now use §a/mucoords§7 to get the meetup coords.");
						return;
					}
				}
				
				if (pvp == 120) {
					PlayerUtils.broadcast(Main.PREFIX + "Meetup is now!");
					Bukkit.getPluginManager().callEvent(new MeetupEvent());
					
					if (game.isMovedMiddle()) {
						PlayerUtils.broadcast(Main.PREFIX + "Use §a/mucoords§7 to get the meetup coords.");
						return;
					}
				}
			}
		};
		
		taskMinutes.runTaskTimer(plugin, 1200, 1200);
	}
 
	/**
	 * Stop all the running timers.
	 */
	public void stopTimers() {
		final BukkitScheduler sch = Bukkit.getScheduler();

		if (taskMinutes != null && (sch.isQueued(taskMinutes.getTaskId()) || sch.isCurrentlyRunning(taskMinutes.getTaskId()))) {
			taskMinutes.cancel();
		}
		
		if (taskSeconds != null && (sch.isQueued(taskSeconds.getTaskId()) || sch.isCurrentlyRunning(taskSeconds.getTaskId()))) {
			taskSeconds.cancel();
		}
	}
}