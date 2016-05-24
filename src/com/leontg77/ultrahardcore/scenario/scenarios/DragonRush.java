package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DragonRush scenario class
 * 
 * @author LeonTG77
 */
public class DragonRush extends Scenario implements Listener, CommandExecutor {
    public static final String PREFIX = "§cDragon Rush §8» §7";

    private final List<Location> portalBlocks = new ArrayList<Location>();

    private final Location start;
    private final Location end;

    private final SpecManager spec;

    public DragonRush(SpecManager spec) {
        super("DragonRush", "The first team to kill the dragon wins the game.");

        World world = Bukkit.getWorld("lobby");

        start = new Location(world, 337, 25, 39);
        end = new Location(world, 347, 38, 68);

        for (int x = start.getBlockX(); x <= end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y <= end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z <= end.getBlockZ(); z++) {
                    portalBlocks.add(new Location(world, x, y, z));
                }
            }
        }

        this.spec = spec;

        plugin.getCommand("genportal").setExecutor(this);
    }

    private final Random rand = new Random();
    private int placed = 0;

    @Override
    public void onDisable() {
        placed = 0;
    }

    @Override
    public void onEnable() {
        placed = 0;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();

        if (block == null) {
            return;
        }
        
        if (item == null) {
            return;
        }

        Action action = event.getAction();
        Player player = event.getPlayer();

        Location loc = new Location(block.getWorld(), 0, block.getLocation().getY(), 0);
        
        if (spec.isSpectating(player)) {
            return;
        }
        
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        if (block.getType() != Material.ENDER_PORTAL_FRAME) {
            return;
        }
        
        if (block.getLocation().distance(loc) > 15) {
            return;
        }
        
        if (BlockUtils.getDurability(block) > 3) {
            return;
        }
        
        if (item.getType() != Material.EYE_OF_ENDER) {
            return;
        }
        
        placed++;

        if (placed == 3) {
            PlayerUtils.broadcast(Main.PREFIX + "§d§l§oThe portal has been activated.");

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.PORTAL_TRAVEL, 0.5f, 1);
            }

            for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
                for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
                    block.getWorld().getBlockAt(x, loc.getBlockY(), z).setType(Material.ENDER_PORTAL);
                }
            }
        } else if (placed < 3) {
            PlayerUtils.broadcast(Main.PREFIX + "An eye has been placed (§a" + placed + "§7/§a3§7)");

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
        }
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof EnderDragon)) {
            return;
        }
        
        Player killer = entity.getKiller();

        if (killer == null) {
            return;
        }

        PlayerUtils.broadcast(Main.PREFIX + "The dragon was defeated by §a" + killer.getName() + "§7.");

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld() == killer.getWorld()) {
                continue;
            }

            online.playSound(online.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
        }
    }

    @EventHandler
    public void on(MeetupEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            PacketUtils.sendTitle(online, "§c§lTIMES UP!", "§7The time ran out and the dragon won.", 5, 30, 5);
        }
    }

    @SuppressWarnings("deprecation")
    private void generatePortal(Location loc) {
        int diffX = end.getBlockX() - start.getBlockX();
        int diffY = end.getBlockY() - start.getBlockY();
        int diffZ = end.getBlockZ() - start.getBlockZ();

        int i = 0;

        for (int x = 0; x <= diffX; x++) {
            for (int y = 0; y <= diffY; y++) {
                for (int z = 0; z <= diffZ; z++) {
                    Location current = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    Location tLoc = portalBlocks.get(i);
                    i++;

                    if (tLoc == null) {
                        continue;
                    }

                    if (!tLoc.getChunk().isLoaded()) {
                        tLoc.getChunk().load(true);
                    }

                    Block cBlock = current.getBlock();
                    Block block = tLoc.getBlock();

                    if (block.getType() == Material.BARRIER) {
                        continue;
                    }

                    if (block.getType() == Material.SMOOTH_BRICK) {
                        if (rand.nextInt(8) == 0) {
                            cBlock.setTypeId(97);
                            cBlock.setData((byte) (rand.nextInt(3) + 2));
                        } else {
                            cBlock.setType(block.getType());
                            cBlock.setData((byte) rand.nextInt(3));
                        }
                    } else if (block.getType() == Material.MOB_SPAWNER) {
                        cBlock.setType(block.getType());
                        cBlock.setData(block.getData());

                        if (cBlock.getState() instanceof CreatureSpawner) {
                            CreatureSpawner spawner = (CreatureSpawner) cBlock.getState();

                            spawner.setSpawnedType(EntityType.SILVERFISH);
                        }
                    } else {
                        cBlock.setType(block.getType());
                        cBlock.setData(block.getData());
                    }

                    cBlock.getState().update(true);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "DragonRush is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "Usage: /genportal <world>");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "The world '" + args[0] + "' does not exist.");
            return true;
        }

        Location high = new Location(world, 0, 0, 24);
        high.setY(world.getHighestBlockYAt(high));

        Location center = new Location(world, -5, high.getY() - (end.getY() - start.getY()) + 1, -5);

        generatePortal(center);
        PlayerUtils.broadcast(PREFIX + "Generated end portal in world '§a" + world.getName() + "§7'.");
        return true;
    }
}