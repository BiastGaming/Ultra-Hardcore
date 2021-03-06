package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.creeper;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.WorldServer;

public class ImpendingExplosionParticleTask extends BukkitRunnable {

    protected final Location location;
    protected final int count;

    public ImpendingExplosionParticleTask(Location location, int count) {
        this.location = location;
        this.count = count;
    }

    @Override
    public void run() {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

        world.sendParticles(
                null,
                EnumParticle.SMOKE_LARGE,
                false,
                location.getX(),
                location.getY(),
                location.getZ(),
                count,
                1D,
                1D,
                1D,
                0
        );
    }
}
