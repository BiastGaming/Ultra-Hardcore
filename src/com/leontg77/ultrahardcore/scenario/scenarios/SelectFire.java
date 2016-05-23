package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;

/**
 * SelectFire scenario class.
 * 
 * @author LeonTG77
 */
public class SelectFire extends Scenario implements Listener {
    public static final String PREFIX = "§cSelect Fire §8» §7";

    public SelectFire() {
        super("SelectFire", "When you left click holding a bow, it cycles the mode to what the bow shoots. It can shoot creepers, TNT, flame arrows, or fireballs, Chickens do not drop feathers, Coal ore drops arrows with a 10% chance.");
    }

    private final Map<UUID, BowMode> modes = Maps.newHashMap();

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.BOW) {
            return;
        }

        BowMode oldMode = getBowMode(player);

        switch (oldMode) {
        case FLAME:
            modes.put(player.getUniqueId(), BowMode.TNT);
            break;
        case TNT:
            modes.put(player.getUniqueId(), BowMode.CREEPER);
            break;
        case CREEPER:
            modes.put(player.getUniqueId(), BowMode.FIREBALL);
            break;
        case FIREBALL:
            modes.put(player.getUniqueId(), BowMode.FLAME);
            break;
        default:
            break;
        }

        player.sendMessage(PREFIX + "New mode: §6" + NameUtils.capitalizeString(getBowMode(player).name(), false) + ".");
    }

    @EventHandler
    public void on(ProjectileLaunchEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Projectile proj = event.getEntity();

        if (!(proj instanceof Arrow)) {
            return;
        }

        if (!(proj.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) proj.getShooter();
        Arrow arrow = (Arrow) proj;
        
        if (!game.getPlayers().contains(player)) {
            return;
        }

        Location loc = player.getLocation().clone();
        BowMode mode = getBowMode(player);

        switch (mode) {
        case FLAME:
            arrow.setFireTicks(Integer.MAX_VALUE);
            break;
        case TNT:
            arrow.remove();
            
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setVelocity(loc.getDirection().multiply(3.0));
            tnt.setFuseTicks(80);
            break;
        case CREEPER:
            arrow.remove();
            
            Creeper creep = loc.getWorld().spawn(loc, Creeper.class);
            creep.setVelocity(loc.getDirection().multiply(3.0));
            break;
        case FIREBALL:
            arrow.remove();
            player.launchProjectile(Fireball.class);
            break;
        default:
            break;
        }
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Chicken)) {
            return;
        }
        
        for (ItemStack drop : event.getDrops()) {
            if (drop.getType() == Material.FEATHER) {
                drop.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (block.getType() != Material.COAL_ORE) {
            return;
        }

        BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.ARROW));
    }

    /**
     * Get the current bow mode for the given player.
     * 
     * @param player The player to get for.
     * @return
     */
    private BowMode getBowMode(Player player) {
        if (!modes.containsKey(player.getUniqueId())) {
            modes.put(player.getUniqueId(), BowMode.FLAME);
        }

        return modes.get(player.getUniqueId());
    }
    
    /**
     * Bow modes enum class.
     * 
     * @author LeonTG77
     */
    public enum BowMode {
        FLAME, CREEPER, FIREBALL, TNT;
    }
}