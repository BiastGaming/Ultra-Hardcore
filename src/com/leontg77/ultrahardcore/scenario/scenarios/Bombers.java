package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Bombers scenario class.
 * 
 * @author LeonTG77
 */
public class Bombers extends Scenario implements Listener {
    private static final Material ITEM_TYPE = Material.TNT;

    public Bombers() {
        super("Bombers", "Everyone starts out with an unbreakable flint and steel, most animals and monsters drop TNT.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        final ItemStack toGive = new ItemStack(Material.FLINT_AND_STEEL, 1);
        final ItemMeta meta = toGive.getItemMeta();
        meta.spigot().setUnbreakable(true);
        toGive.setItemMeta(meta);

        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerUtils.giveItem(online, toGive.clone());
        }
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        final List<ItemStack> drops = event.getDrops();
        final LivingEntity entity = event.getEntity();

        switch (entity.getType()) {
        case BAT:
        case PIG:
        case SHEEP:
        case COW:
        case CHICKEN:
        case HORSE:
        case WOLF:
            drops.add(new ItemStack(ITEM_TYPE, 1));
            break;
        case ZOMBIE:
        case SPIDER:
        case SKELETON:
        case ENDERMAN:
        case SLIME:
        case GHAST:
        case SILVERFISH:
            drops.add(new ItemStack(ITEM_TYPE, 3));
            break;
        case CREEPER:
        case BLAZE:
            drops.add(new ItemStack(ITEM_TYPE, 4));
            break;
        default:
            break;
        }
    }
}