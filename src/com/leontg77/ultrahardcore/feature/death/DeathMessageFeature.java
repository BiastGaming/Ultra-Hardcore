package com.leontg77.ultrahardcore.feature.death;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

/**
 * Death Messages feature class.
 * 
 * @author LeonTG77
 */
public class DeathMessageFeature extends Feature implements Listener {
    private final Arena arena;
    private final Game game;

    /**
     * Death Messages feature class contructor.
     *
     * @param arena The arena class.
     * @param game The game class.
     */
    public DeathMessageFeature(Arena arena, Game game) {
        super("Death Messages", "Modified death messages.");

        this.arena = arena;
        this.game = game;
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (game.isRecordedRound()) {
            return;
        }

        final Player player = event.getEntity();

        // the arena has it's own way of doing deaths.
        if (arena.isEnabled() && arena.hasPlayer(player)) {
            return;
        }

        final List<World> worlds = game.getWorlds();

        String deathMessage = event.getDeathMessage();
        event.setDeathMessage(null);

        if (!game.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
            return;
        }

        Player killer = player.getKiller();

        if (deathMessage == null || deathMessage.isEmpty()) {
            return;
        }

        if (killer == null) {
            PlayerUtils.broadcast("§8» §f" + deathMessage);
            return;
        }

        ItemStack item = killer.getItemInHand();

        String shotDistance = "";

        if (player.getWorld().equals(killer.getWorld())) {
            double distance = killer.getLocation().distance(player.getLocation());
            shotDistance = " §8(§6" + NumberUtils.formatDouble(distance) + " §7blocks§8)";
        }

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !deathMessage.contains(killer.getName()) || (!deathMessage.contains("slain") && !deathMessage.contains("shot"))) {
            if (deathMessage.contains("shot")) {
                deathMessage += shotDistance;
            }

            PlayerUtils.broadcast("§8» §f" + deathMessage);
            return;
        }

        String name = item.getItemMeta().getDisplayName();

        ComponentBuilder builder = new ComponentBuilder("§8» §f" + deathMessage.replace("[" + name + "]", ""));
        String color = killer.getItemInHand().getEnchantments().isEmpty() ? "§f" : "§b";

        StringBuilder colored = new StringBuilder();

        for (String entry : name.split(" ")) {
            colored.append(color + "§o" + entry).append(" ");
        }

        builder.append(color + "[" + colored.toString().trim() + color + "]");
        builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent( convertToJson(item)) }));

        if (deathMessage.contains("shot")) {
            builder.append(shotDistance, FormatRetention.NONE);
        }

        BaseComponent[] result = builder.create();

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.spigot().sendMessage(result);
        }

        Bukkit.getLogger().info("§8» §f" + deathMessage);
    }

    /**
    * Converts an {@link ItemStack} to a Json string
    * for sending with {@link BaseComponent}'s.
    *
    * @param itemStack the item to convert
    * @return the Json string representation of the item
    */
    public static String convertToJson(ItemStack stack) {
        net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(stack);
        final NBTTagCompound tag = item.save(new NBTTagCompound());

        return tag.toString();
    }
}