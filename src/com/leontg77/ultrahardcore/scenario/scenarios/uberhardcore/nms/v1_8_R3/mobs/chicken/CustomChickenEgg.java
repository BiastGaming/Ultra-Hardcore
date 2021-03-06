package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.chicken;


import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityEgg;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.World;

/**
 * Used to replace an EntityEgg with a version that spawns an CustomChicken instead.
 *
 * Also reduces rates of babies spawning from eggs too to avoid overpopulation
 */
public class CustomChickenEgg extends EntityEgg {

    public CustomChickenEgg(World worldIn, EntityLiving shooter) {
        super(worldIn, shooter);
    }

    // replace 1/8 chance to spawn baby to 1/32 + no chance for 4 to spawn
    @Override
    protected void a(MovingObjectPosition movingobjectposition)
    {
        if(movingobjectposition.entity != null) {
            // added this.getThrower() instanceof CustomChicken ? 1.0F : 0.0F]
            // causes non-chicken thrown eggs to do no damage
            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), this.getShooter() instanceof CustomChicken ? 1.0F : 0.0F);
        }

        // 1/32 chance to spawn
        if (this.random.nextInt(32) == 0) {
            CustomChicken chicken = new CustomChicken(this.getWorld());
            chicken.setAge(-24000);
            chicken.setLocation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
            this.world.addEntity(chicken, CreatureSpawnEvent.SpawnReason.EGG);
        }

        // standard egg particles
        for(int i = 0; i < 8; ++i) {
            this.world.addParticle(EnumParticle.ITEM_CRACK, this.locX, this.locY, this.locZ, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, new int[]{Item.getId(Items.EGG)});
        }

        // RIP
        this.die();
    }
}
