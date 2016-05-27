package com.leontg77.ultrahardcore.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.minigames.Arena;

/**
 * Stats listener class.
 * 
 * @author LeonTG77
 */
public class StatsListener implements Listener {
    private final Main plugin;

    private final Arena arena;
    private final Game game;

    private final BoardManager board;
    private final TeamManager teams;

    private final GoldenHeadsFeature ghead;

    public StatsListener(Main plugin, Arena arena, Game game, BoardManager board, TeamManager teams, GoldenHeadsFeature ghead) {
        this.plugin = plugin;

        this.arena = arena;
        this.game = game;

        this.board = board;
        this.teams = teams;

        this.ghead = ghead;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // the arena has it's own way of doing deaths.
        if (arena.isEnabled() && arena.hasPlayer(player)) {
            return;
        }

        List<World> worlds = game.getWorlds();

        // I don't care about the rest they're not in a game world.
        if (!worlds.contains(player.getWorld())) {
            return;
        }

        User user = plugin.getUser(player);

        Player killer = player.getKiller();

        if (killer == null) {
            user.increaseStat(Stat.DEATHS);
            return;
        }

        Team pteam = teams.getTeam(player);
        Team team = teams.getTeam(killer);

        if (pteam != null && pteam.equals(team)) {
            return;
        }

        user.increaseStat(Stat.DEATHS);

        User killUser = plugin.getUser(killer);
        killUser.increaseStat(Stat.KILLS);

        if (killUser.getStat(Stat.KILLSTREAK) < board.getScore(killer.getName())) {
            killUser.setStat(Stat.KILLSTREAK, board.getScore(killer.getName()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) {
            return;
        }

        User user = plugin.getUser(killer);

        if (entity instanceof Monster) {
            user.increaseStat(Stat.HOSTILEMOBKILLS);
        }

        if (entity instanceof Animals) {
            user.increaseStat(Stat.ANIMALKILLS);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        double oldHealth = player.getHealth();

        new BukkitRunnable() {
            public void run() {
                double damage = oldHealth - player.getHealth();
                User user = plugin.getUser(player);

                double current = user.getStatDouble(Stat.DAMAGETAKEN);
                user.setStat(Stat.DAMAGETAKEN, current + damage);
                
                if (event.getCause() == DamageCause.FALL) {
                    double currentF = user.getStatDouble(Stat.FALLDAMAGE);
                    user.setStat(Stat.FALLDAMAGE, currentF + damage);
                }
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityShootBowEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        User user = plugin.getUser(player);
        
        user.increaseStat(Stat.ARROWSHOTS);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        
        user.increaseStat(Stat.BLOCKS);
        
        if (block.getType() == Material.GLOWSTONE) {
            user.increaseStat(Stat.GLOWSTONE);
            return;
        }
        
        if (block.getType() == Material.DIAMOND_ORE) {
            user.increaseStat(Stat.DIAMONDS);
            return;
        }

        if (block.getType() == Material.GOLD_ORE) {
            user.increaseStat(Stat.GOLD);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        
        user.increaseStat(Stat.PLACED);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        Entity attacked = event.getEntity();
        Entity attacker = event.getDamager();

        if (!(attacked instanceof Player) || !(attacker instanceof Arrow)) {
            return;
        }

        Player player = (Player) attacked;
        Arrow arrow = (Arrow) attacker;

        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player killer = (Player) arrow.getShooter();
        double distance = killer.getLocation().distance(player.getLocation());

        User user = plugin.getUser(killer);

        if (user.getStatDouble(Stat.LONGESTSHOT) < distance) {
            user.setStat(Stat.LONGESTSHOT, distance);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDmg(EntityDamageByEntityEvent event) {
        Entity attacked = event.getEntity();
        Entity attacker = event.getDamager();

        if (!(attacked instanceof LivingEntity)) {
            return;
        }

        LivingEntity living = (LivingEntity) attacked;
        
        if (attacker instanceof Player) {
            Player killer = (Player) attacker;
            
            User user = plugin.getUser(killer);
            user.increaseStat(Stat.MELEEHITS);
            
            double oldHealth = living.getHealth();

            new BukkitRunnable() {
                public void run() {
                    double damage = oldHealth - living.getHealth();

                    Stat stat = living instanceof Player ? Stat.PVPDAMAGEDEALT : Stat.PVEDAMAGEDEALT;
                    
                    double current = user.getStatDouble(stat);
                    user.setStat(stat, current + damage);
                }
            }.runTaskLater(plugin, 1);
        }
        
        if (attacker instanceof Arrow) {
            Arrow arrow = (Arrow) attacker;
            
            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }
            
            Player killer = (Player) arrow.getShooter();
            
            User user = plugin.getUser(killer);
            user.increaseStat(Stat.BOWHITS);
            
            double oldHealth = living.getHealth();

            new BukkitRunnable() {
                public void run() {
                    double damage = oldHealth - living.getHealth();

                    Stat stat = living instanceof Player ? Stat.PVPDAMAGEDEALT : Stat.PVEDAMAGEDEALT;
                    
                    double current = user.getStatDouble(stat);
                    user.setStat(stat, current + damage);
                }
            }.runTaskLater(plugin, 1);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        User user = plugin.getUser(player);

        LivingEntity entity = event.getEntity();
        
        if (entity instanceof Wolf) {
            user.increaseStat(Stat.WOLVESTAMED);
            return;
        }

        if (entity instanceof Horse) {
            user.increaseStat(Stat.HORSESTAMED);
        }

        if (entity instanceof Ocelot) {
            user.increaseStat(Stat.CATSTAMED);
        }
    }
    
    @EventHandler
    public void on(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        
        if (!(proj.getShooter() instanceof Player)) {
            return;
        }
        
        Player player = (Player) proj.getShooter();
        User user = plugin.getUser(player);
        
        if (proj instanceof Snowball) {
            user.increaseStat(Stat.SNOWBALLSHOTS);
            return;
        }

        if (proj instanceof FishHook) {
            user.increaseStat(Stat.RODUSES);
        }

        if (proj instanceof Egg) {
            user.increaseStat(Stat.EGGSTHROWN);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        User user = plugin.getUser(player);

        user.setStat(Stat.HEARTSHEALED, user.getStat(Stat.HEARTSHEALED) + event.getAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        int oldL = event.getOldLevel();
        int newL = event.getNewLevel();

        if (oldL >= newL) {
            return;
        }

        user.setStat(Stat.LEVELS, user.getStat(Stat.LEVELS) + (newL - oldL));
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        ItemStack item = event.getItem();

        if (item.getType() == Material.GOLDEN_APPLE) {
            if (ghead.isGoldenHead(item)) {
                user.increaseStat(Stat.GOLDENHEADSEATEN);
            } else {
                user.increaseStat(Stat.GOLDENAPPLESEATEN);
            }
            return;
        }

        if (item.getType() == Material.POTION && item.getDurability() != 0) {
            user.increaseStat(Stat.POTIONS);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(PlayerPortalEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        User user = plugin.getUser(player);
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        switch (to.getWorld().getEnvironment()) {
        case NETHER:
            user.increaseStat(Stat.NETHER);
            break;
        default:
            break;
        }
    }
}