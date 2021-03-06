package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.minigames.Parkour;
import com.leontg77.ultrahardcore.utils.LocationUtils;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

/**
 * PushToSpawn listener class.
 * 
 * @author D4mnX
 */
public class PushToSpawnListener implements Listener {
    public static final double SPAWN_RADIUS = 35.0;

    private final Parkour parkour;
    private final Main plugin;

    /**
     * PushToSpawn listener class constructor.
     * 
     * @param plugin The main class.
     * @param parkour The parkour class.
     */
    public PushToSpawnListener(Main plugin, Parkour parkour) {
        this.parkour = parkour;
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (LocationUtils.areEqual(from, to)) {
            return;
        }
        
        World world = to.getWorld();
        
        Vector vec = player.getVelocity();
        double velocityY = vec.getY();

        if (!world.getName().equals("lobby")) {
            return;
        }

        if (to.getY() >= 0) {
            return;
        }

        if (parkour.isParkouring(player)) {
            return;
        }

        if (velocityY >= 1.15) {
            return;
        }

        // No matter what, increase velocity by 15
        vec.setY(velocityY + 1.15);

        Location spawn = plugin.getSpawn();
        spawn.setY(to.getY());
        
        double distanceToCenter = to.distance(spawn);
        double horizontalPushingSpeed = Math.max(0, distanceToCenter / SPAWN_RADIUS - 1d);
        
        // Push in the opposite direction (If they're at X, they need to be pushed to -X)
        double inverseX = -to.getX();
        double inverseZ = -to.getZ();
        
        Vector horizontalPushingVector = new Vector(inverseX, 0, inverseZ).normalize().multiply(horizontalPushingSpeed);
        vec.add(horizontalPushingVector);

        try {
            player.setVelocity(vec);
        } catch (Throwable e) {
            return;
        }
        
        player.getWorld().playSound(to, Sound.GHAST_FIREBALL, 1, 1);
        playEffect(to);
    }

    /**
     * Player the fire effect at the given location.
     *
     * @param loc The location.
     */
    private void playEffect(Location loc) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0.00003f, 0.0000000003f, 0.00003f, 0.05f, 100, null);

        for (Player player : loc.getWorld().getPlayers()) {
            CraftPlayer craft = (CraftPlayer) player; // safe cast.

            craft.getHandle().playerConnection.sendPacket(packet);
        }
    }
}