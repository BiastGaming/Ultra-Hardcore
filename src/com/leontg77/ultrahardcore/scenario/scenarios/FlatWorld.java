package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

public class FlatWorld extends Scenario implements Listener {

    protected static final Predicate<Entity> IS_VILLAGER = entity -> entity.getType() == EntityType.VILLAGER;
    protected static final Predicate<World> IS_SUPERFLAT = world -> world.getWorldType() == WorldType.FLAT;

    @SuppressWarnings("deprecation")
    protected static final short VILLAGER_TYPE_ID = EntityType.VILLAGER.getTypeId();
    protected static final int VILLAGER_EGG_STARTER_AMOUNT = 20;

    protected final Game game;

    public FlatWorld(Game game) {
        super("FlatWorld", "The world is a superflat world with very high village rates. Villager spawning is disabled to prevent TPS issues, so you start with " + VILLAGER_EGG_STARTER_AMOUNT + " villager spawn eggs.");
        this.game = game;
    }

    @Override
    public void onEnable() {
        Bukkit.getWorlds().stream()
                .filter(IS_SUPERFLAT)
                .map(World::getEntities)
                .flatMap(Collection::stream)
                .filter(IS_VILLAGER)
                .forEach(Entity::remove);

        if (game.isState(State.INGAME)) {
            on(new GameStartEvent());
        }
    }

    @EventHandler
    protected void on(GameStartEvent event) {
        ItemStack villagerEggs = new ItemStack(Material.MONSTER_EGG, VILLAGER_EGG_STARTER_AMOUNT, VILLAGER_TYPE_ID);
        game.getPlayers().forEach(player -> PlayerUtils.giveItem(player, villagerEggs));
    }

    @EventHandler
    protected void on(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            return;
        }

        LivingEntity entity = event.getEntity();
        if (!IS_VILLAGER.test(entity)) {
            return;
        }

        if (!IS_SUPERFLAT.test(entity.getWorld())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    protected void on(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (!IS_SUPERFLAT.test(chunk.getWorld())) {
            return;
        }

        Arrays.stream(chunk.getEntities()).filter(IS_VILLAGER).forEach(Entity::remove);
    }
}