package com.leontg77.ultrahardcore.world.antistripmine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;

/**
 * Anti stripmine world data class.
 * 
 * @author XHawk87, modified by LeonTG77.
 */
public class WorldData {
	private final Main plugin;
	
	private final AntiStripmine antiSM;
	private final String world;
	
	private final Map<Material, Integer> remaining = new HashMap<Material, Integer>();
	private final Set<ChunkOreRemover> noOresFound = new HashSet<ChunkOreRemover>();

	private final File completedFile;
	private final File queuedFile;
	
	private long total;
	private int chunks;

	/**
	 * World data class constructor.
	 * 
	 * @param plugin The main class.
	 * @param antiSM The anti stripmine class.
	 * @param world The world to use for the world data.
	 */
	public WorldData(Main plugin, AntiStripmine antiSM, World world) {
		this.plugin = plugin;
		
		this.antiSM = antiSM;
		this.world = world.getName();
		
		File folder = new File(plugin.getDataFolder(), "Anti Stripmine" + File.separator + world.getName() + File.separator);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.completedFile = new File(folder, "completed.log");
		this.queuedFile = new File(folder, "queued.log");
		
		if (!completedFile.exists()) {
			try {
				completedFile.createNewFile();
			} catch (Exception e) {}
		}
		
		if (!queuedFile.exists()) {
			try {
				queuedFile.createNewFile();
			} catch (Exception e) {}
		}
	}

	/**
	 * Get the world being used by this world data.
	 * 
	 * @return The world.
	 */
	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	/**
	 * Display the world stats for this world to the given sender.
	 * 
	 * @param sender The sender displaying to.
	 */
	public void displayStats(CommandSender sender) {
		if (chunks == 0) {
			sender.sendMessage(ChatColor.RED + "Anti-Stripmine has not run yet on the world: '" + world + "'.");
			return;
		}
		
		sender.sendMessage(Main.PREFIX + world + ": Average §a" + (total / chunks) + " §7ns per chunk §8(§7" + (total / chunks / 1.0E9D) + "s)");
		
		for (Material type : remaining.keySet()) {
			int amount = remaining.get(type);
			
			sender.sendMessage("§8§ §7" + type.name() + "§8: §a" + amount + " §7remaining in §6" + chunks + " §7chunks §8(§7average §6" + (amount / chunks) + " §7per chunk§8)");
		}
	}

	/**
	 * Load up the complated log and the queued log.
	 */
	public void load() {
		final WorldData worldData = this;
		
		new BukkitRunnable() {
			public void run() {
				Set<String> toQueue = new TreeSet<String>();
				Throwable th;
				
				try {
					BufferedReader in = new BufferedReader(new FileReader(queuedFile));
					th = null;
					
					try {
						String line;
						
						while ((line = in.readLine()) != null) {
							toQueue.add(line);
						}
					} catch (Throwable ex) {
						th = ex;
						throw ex;
					} finally {
						if (in == null) {
							return;
						}
						
						if (th == null) {
							in.close();
							return;
						} 
						
						try {
							in.close();
						} catch (Throwable ex) {
							th.addSuppressed(ex);
						}
					}
				} catch (FileNotFoundException ex) {
					plugin.getLogger().log(Level.WARNING, "Could not find file: " + queuedFile.getPath(), ex);
				} catch (Exception ex) {
					plugin.getLogger().log(Level.SEVERE, "Cannot read file: " + queuedFile.getPath(), ex);
				}
				
				try {
					BufferedReader in = new BufferedReader(new FileReader(completedFile));
					th = null;
					
					try {
						String line;
						
						while ((line = in.readLine()) != null) {
							String[] parts = line.split(" ");
							toQueue.remove(parts[0]);
							
							for (int i = 2; i < parts.length; i++) {
								String[] split = parts[i].split(":");
								
								Material type = Material.valueOf(split[0]);
								int amount = Integer.parseInt(split[1]);
								
								if (remaining.containsKey(type)) {
									remaining.put(type, remaining.get(type) + amount);
								} else {
									remaining.put(type, amount);
								}
							}
						}
					} catch (Throwable ex) {
						th = ex;
						throw ex;
					} finally {
						if (in == null) {
							return;
						}
						
						if (th == null) {
							in.close();
							return;
						} 
						
						try {
							in.close();
						} catch (Throwable ex) {
							th.addSuppressed(ex);
						}
					}
				} catch (FileNotFoundException ex) {
					plugin.getLogger().log(Level.WARNING, "Could not find file: " + queuedFile.getPath(), ex);
				} catch (Exception ex) {
					plugin.getLogger().log(Level.SEVERE, "Cannot read file: " + queuedFile.getPath(), ex);
				}
				
				int i = 1;
				
				for (String record : toQueue) {
					String[] parts = record.split(",");
					
					final int x = Integer.parseInt(parts[0]);
					final int z = Integer.parseInt(parts[1]);
					
					new BukkitRunnable() {
						public void run() {
							antiSM.queue(new ChunkOreRemover(antiSM, worldData, getWorld().getChunkAt(x, z)));
						}
					}.runTaskLater(plugin, i++);
				}
				
				final int queued = toQueue.size();
				
				new BukkitRunnable() {
					public void run() {
						plugin.getLogger().info("Loaded data for " + world + ", checking " + queued + " chunks!");
					}
				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	/**
	 * Add the status if the given chunk ore remover to the queued log.
	 * 
	 * @param remover The chunk ore remover to use.
	 */
	public void logQueued(ChunkOreRemover remover) {
		final String record = remover.toString() + "\n";
		
		new BukkitRunnable() {
			public void run() {
				synchronized (queuedFile) {
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(queuedFile, true));
						Throwable th = null;
						
						try {
							out.write(record);
						} catch (Throwable ex) {
							th = ex;
							throw ex;
						} finally {
							if (out == null) {
								return;
							}
							
							if (th == null) {
								out.close();
								return;
							}
							
							try {
								out.close();
							} catch (Throwable x2) {
								th.addSuppressed(x2);
							}
						}
					} catch (Exception ex) {
						plugin.getLogger().log(Level.SEVERE, "Failed to save to " + queuedFile.getPath() + ": " + record, ex);
					}
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	/**
	 * Add the status if the given chunk ore remover to the completed log.
	 * 
	 * @param remover The chunk ore remover to use.
	 */
	public void logCompleted(ChunkOreRemover remover, long duration, Map<Material, Integer> remaining) {
		this.chunks += 1;
		this.total += duration;
		
		StringBuilder builder = new StringBuilder();
		builder.append(remover.toString());
		builder.append(" ").append(duration);
		
		for (Entry<Material, Integer> entry : remaining.entrySet()) {
			Material material = entry.getKey();
			int amount = entry.getValue();
			
			builder.append(" ").append(material.name()).append(":").append(amount);
			
			if (remaining.containsKey(material)) {
				remaining.put(material, remaining.get(material) + amount);
			} else {
				remaining.put(material, amount);
			}
		}
		
		builder.append("\n");
		
		final String record = builder.toString();
		
		new BukkitRunnable() {
			public void run() {
				synchronized (WorldData.this.completedFile) {
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(WorldData.this.completedFile, true));
						Throwable localThrowable2 = null;
						
						try {
							out.write(record);
						} catch (Throwable localThrowable1) {
							localThrowable2 = localThrowable1;
							throw localThrowable1;
						} finally {
							if (out != null) {
								if (localThrowable2 != null) {
									try {
										out.close();
									} catch (Throwable x2) {
										localThrowable2.addSuppressed(x2);
									}
								} else {
									out.close();
								}
							}
						}
					} catch (IOException ex) {
						plugin.getLogger().log(Level.SEVERE, "Failed to save to " + WorldData.this.completedFile.getPath() + ": " + record, ex);
					}
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	/**
	 * Check if the given chunk ore remover has no ore in it's chunk.
	 * 
	 * @param remover The chunk ore remover to check with.
	 * @return True if it has none, false otherwise.
	 */
	public boolean hasNoOres(ChunkOreRemover remover) {
		return noOresFound.contains(remover);
	}

	/**
	 * Do a 2nd check if no ores was found on the first check in the given chunk ore remover.
	 * 
	 * @param remover The chunk ore remover to check with.
	 */
	public void doASecondCheck(final ChunkOreRemover remover) {
		noOresFound.add(remover);
		
		new BukkitRunnable() {
			public void run() {
				antiSM.queue(remover);
			}
		}.runTaskLater(plugin, 100L);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WorldData)) {
			return false;
		}
		
		WorldData other = (WorldData) obj;
		return other.getWorld().getUID().equals(getWorld().getUID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getWorld().getUID());
	}
}