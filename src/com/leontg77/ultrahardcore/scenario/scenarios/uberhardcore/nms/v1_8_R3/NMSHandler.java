package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3;

import java.util.List;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.MobOverride;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.chicken.CustomChicken;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.chicken.ThrownEggHandler;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.creeper.CreeperDeathHandler;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.rabbit.RabbitSpawnHandler;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.sheep.CustomSheep;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.skeleton.CustomSkeleton;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.spider.CustomSpider;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.spider.SpiderDeathHandler;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.zombie.CustomZombie;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.zombie.ZombieSeigeHandler;

import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntityZombie;

public class NMSHandler extends com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.NMSHandler {

    protected final EntityChecker entityChecker;
    protected final EntityClassReplacer entityClassReplacer;
    protected final NewSpawnsModifier newSpawnsModifier;
    protected final List<MobOverride> mobOverrides;

    public NMSHandler(Plugin plugin) {
        super(plugin);

        entityChecker = new EntityChecker();
        newSpawnsModifier = new NewSpawnsModifier();
        entityClassReplacer = new EntityClassReplacer(plugin.getLogger());

        mobOverrides = ImmutableList.of(
                new MobOverride()
                    .withOverridingClasses(EntityChicken.class, CustomChicken.class)
                    .withListeners(new ThrownEggHandler())
                    .withSupressedInvalidSpawnReasons(CreatureSpawnEvent.SpawnReason.MOUNT),
                new MobOverride()
                    .withOverridingClasses(EntitySkeleton.class, CustomSkeleton.class)
                    .withSupressedInvalidSpawnReasons(CreatureSpawnEvent.SpawnReason.JOCKEY),
                new MobOverride()
                    .withListeners(new CreeperDeathHandler()),
                new MobOverride()
                    .withListeners(new RabbitSpawnHandler()),
                new MobOverride()
                    .withOverridingClasses(EntitySheep.class, CustomSheep.class),
                new MobOverride()
                    .withOverridingClasses(EntitySpider.class, CustomSpider.class)
                    .withListeners(new SpiderDeathHandler()),
                new MobOverride()
                    .withOverridingClasses(EntityZombie.class, CustomZombie.class)
                    .withListeners(new ZombieSeigeHandler())
                    .withSupressedInvalidSpawnReasons(CreatureSpawnEvent.SpawnReason.REINFORCEMENTS)
        );
    }

    @Override
    public com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityChecker getEntityChecker() {
        return entityChecker;
    }

    @Override
    public com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityClassReplacer getEntityClassReplacer() {
        return entityClassReplacer;
    }

    @Override
    public List<MobOverride> getMobOverrides() {
        return mobOverrides;
    }

    @Override
    public NewSpawnsModifier getNewSpawnsModifier() {
        return newSpawnsModifier;
    }
}
