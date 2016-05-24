package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * End meetup scenario class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class EndMeetup extends Scenario implements Listener {
    private final ScatterManager scatter;

    public EndMeetup(ScatterManager scatter) {
        super("EndMeetup", "Meetup will be in the end, a portal will be lit at 0,0 and pvp will be disabled in other worlds as soon as meetup occurs, going into the end will put you in a random location and give you 20 seconds of resistance(to prevent spawn killing), The dragon is already dead and endermens are disabled.");

        this.scatter = scatter;
    }

    @EventHandler
    public void on(MeetupEvent event) {
        for (World gWorld : game.getWorlds()) {
            if (!gWorld.getEnvironment().equals(Environment.THE_END)) {
                gWorld.setPVP(false);
                continue;
            }

            for (Entity entity : gWorld.getEntities()) {
                if (entity instanceof Enderman || entity instanceof EnderCrystal || entity instanceof EnderDragon) {
                    entity.remove();
                }
            }
        }
        
        World world = game.getWorld();

        if (world == null) {
            return;
        }

        Location highest = LocationUtils.getHighestBlock(new Location(world, 0.5, 0, 0.5));

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Block block = new Location(world, x, highest.getBlockY(), z).getBlock();
                block.setType(Material.ENDER_STONE);

                if (x == -2 && z == -2) {
                    continue;
                }

                if (x == 2 && z == 2) {
                    continue;
                }

                if (x == 2 && z == -2) {
                    continue;
                }

                if (x == -2 && z == 2) {
                    continue;
                }

                Block blockAbove = block.getRelative(BlockFace.UP);
                blockAbove.setType(Material.ENDER_PORTAL_FRAME);
                blockAbove.setData((byte) 4);
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = new Location(world, x, highest.getBlockY() + 1, z).getBlock();
                block.setType(Material.ENDER_PORTAL);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();

        if (to.getWorld().getEnvironment() != Environment.THE_END) {
            return;
        }

        List<Location> locs = scatter.findScatterLocations(to.getWorld(), 100, 1);

        if (locs.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Could not teleport you to the end.");
            event.setCancelled(true);
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 10));

        event.setTo(locs.get(0));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(CreatureSpawnEvent event) {
        Location loc = event.getLocation();
        Entity entity = event.getEntity();

        if (entity instanceof Enderman && loc.getWorld().getEnvironment() != Environment.THE_END) {
            event.setCancelled(true);
        }
    }
}