package com.leontg77.ultrahardcore.scenario.scenarios;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/**
 * ExplosiveArrows scenario class.
 * 
 * @author D4mnX
 */
public class ExplosiveArrows extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    public ExplosiveArrows(Main plugin, Game game) {
        super("ExplosiveArrows", "Arrows shot by players explode upon any contact (both entity and block). These explosions do not cause damage, but instead result in the would-be-damaged players (and other entities) being blown away very fast.");
       
        this.plugin = plugin;
        this.game = game;
    }

    private ExplosionData lastExplosionData;

    @EventHandler
    protected void on(ProjectileHitEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Projectile projectile = event.getEntity();
        
        if (!(projectile instanceof Arrow)) {
            return;
        }

        ProjectileSource shooter = projectile.getShooter();
        
        if (shooter == null || !(shooter instanceof Player)) {
            return;
        }

        World world = projectile.getWorld();
        
        if (world.getName().equals("lobby")) {
            /*
              Failsafe. This should never happen as interaction events are cancelled in spawn and state is ingame,
              but just in case...A force 10 explosion in the middle of spawn would be devastating.
             */
            return;
        }

        Location location = projectile.getLocation();
        // getLocation on arrows reports as ~1.5 blocks behind the tip of the arrow
        
        Vector direction = projectile.getVelocity().normalize();
        location.add(direction.multiply(1.7f));

        float force = (float) projectile.getVelocity().length() * 2;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            lastExplosionData = new ExplosionData();
            lastExplosionData.location = location;
            lastExplosionData.force = force;
            
            world.createExplosion(location, force, false);
            projectile.remove();
            
            lastExplosionData = null;
        }, 1L);
    }

    @EventHandler
    protected void on(EntityDamageEvent event) {
        if (lastExplosionData == null) {
            return;
        }

        event.setCancelled(true);

        Entity entity = event.getEntity();
        double distance = entity.getLocation().distance(lastExplosionData.location);
        double speed = (lastExplosionData.force - (distance/2)) / 3;
        if (speed < 0) {
            return;
        }

        Vector explosionVector = lastExplosionData.location.toVector();
        Vector entityVector = entity.getLocation().toVector();

        Vector shootDirection = entityVector.clone()
                .subtract(explosionVector)
                .normalize()
                .multiply(speed);

        Vector oldVelocity = entity.getVelocity();
        Vector newVelocity = oldVelocity.clone().add(shootDirection);
        entity.setVelocity(newVelocity);
    }

    private static class ExplosionData {
        Location location;
        float force;
    }
}