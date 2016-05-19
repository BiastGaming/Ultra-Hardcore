package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * TommySX scenario class.
 * 
 * @author LeonTG77
 */
public class TommySX extends Scenario implements Listener {
    private final Game game;

    /**
     * TommySX class constructor.
     *
     * @param game The game class.
     */
    public TommySX(Game game) {
        super("TommySX", "Creepers will do 2x more damage, every diamond shovel automatically has Efficiency V, every bow and sword is automatically named \"XDDDDDDDDDDDE\", if MrTeamRaven is in the game, he will drop 64 cobblestone, 64 ladders and 2 feathers (+ his inventory) on his death, 'gg' in the chat is changed to 'mfw' and mobs (both passive and hostile) have 10% chance of spawning with speed 2, they will also be named 'Gotta Go Fast!'");

        this.game = game;
    }

    private final Random rand = new Random();

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        for (World world : game.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof LivingEntity)) {
                    continue;
                }

                on(new CreatureSpawnEvent((LivingEntity) entity, SpawnReason.CUSTOM));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW) // so my chat listener can still make prefixes and such but have the new message.
    public void on(AsyncPlayerChatEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        String message = event.getMessage();

        message = message.replaceAll("gg", "mfw");
        message = message.replaceAll("Gg", "Mfw");
        message = message.replaceAll("gG", "mfW");
        message = message.replaceAll("GG", "MFW");

        event.setMessage(message);
    }

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (rand.nextInt(100) >= 10) {
            return;
        }

        LivingEntity entity = event.getEntity();

        if (!game.getWorlds().contains(entity.getWorld())) {
            return;
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, NumberUtils.TICKS_IN_999_DAYS, 1));
        entity.setCustomName("Gotta Go Fast!");
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Entity damager = event.getDamager();

        if (damager instanceof Creeper) {
            event.setDamage(event.getDamage() * 2);
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        ItemStack item = event.getRecipe().getResult();

        if (item == null) {
            return;
        }

        switch (item.getType()) {
        case DIAMOND_SPADE:
            item.addEnchantment(Enchantment.DIG_SPEED, 5);
            break;
        case DIAMOND_SWORD:
        case BOW:
        case GOLD_SWORD:
        case IRON_SWORD:
        case STONE_SWORD:
        case WOOD_SWORD:
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("XDDDDDDDDDDDE");
            item.setItemMeta(meta);
            break;
        default:
            break;
        }

        event.getInventory().setResult(item);
    }

    private static final UUID RAVEN_UUID = UUID.fromString("286e3f57-9cd0-477e-8f08-5d4953710449");

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();

        if (!player.getUniqueId().equals(RAVEN_UUID)) {
            return;
        }

        event.getDrops().add(new ItemStack(Material.COBBLESTONE, 64));
        event.getDrops().add(new ItemStack(Material.LADDER, 64));
        event.getDrops().add(new ItemStack(Material.FEATHER, 2));
    }
}