package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.chicken;

import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.AIUtil;

import net.minecraft.server.v1_8_R3.EntityAgeable;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IRangedEntity;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PathfinderGoalArrowAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalFollowParent;
import net.minecraft.server.v1_8_R3.PathfinderGoalMakeLove;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalPanic;
import net.minecraft.server.v1_8_R3.PathfinderGoalTempt;
import net.minecraft.server.v1_8_R3.World;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CustomChicken extends EntityChicken implements IRangedEntity {

    public CustomChicken(World world) {
        super(world);

        AIUtil ai = AIUtil.getInstance();

        ai.removeAIGoals(this.goalSelector, PathfinderGoalPanic.class, PathfinderGoalMakeLove.class, PathfinderGoalTempt.class, PathfinderGoalFollowParent.class);

        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.25D, 20, 10.0F));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityPlayer.class, false));
    }

    // override creating a child to stop an EntityChicken being spawned
    // and spawn our mob instead
    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        return new CustomChicken(this.world);
    }

    // taken logic from EntitySnowman
    @Override
    public void a(EntityLiving entityliving, float v) {
        // change snowball to egg
        CustomChickenEgg egg = new CustomChickenEgg(this.world, this);
        double d0 = entityliving.locY + (double)entityliving.getHeadHeight() - 1.100000023841858D;
        double d1 = entityliving.locX - this.locX;
        double d2 = d0 - egg.locY;
        double d3 = entityliving.locZ - this.locZ;
        float f1 = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        egg.shoot(d1, d2 + (double) f1, d3, 1.6F, 12.0F);
        // change bow noise to plop noise
        this.makeSound("mob.chicken.plop", 1.0F, 1.0F / (this.bc().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(egg);
    }
}
