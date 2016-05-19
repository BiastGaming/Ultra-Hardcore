package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * GoodGame+ scenario class.
 * 
 * @author LeonTG77
 */
public class GoodGame extends Scenario implements Listener {
    private static final String PREFIX = "§aGood Game §8» §7";

    private final Game game;

    public GoodGame(Game game) {
        super("GoodGame", "The first person to say GG after someone dies will get a random reward, whether it be a diamond, gold, cake, iron, or a firework.");

        this.game = game;
    }

    private final Random rand = new Random();
    private boolean checkForGG = false;

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        checkForGG = true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGG(AsyncPlayerChatEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (!checkForGG) {
            return;
        }

        String message = event.getMessage().toLowerCase();

        if (!message.contains("gg") && !message.contains("good game") && !message.contains("goodgame")) {
            return;
        }

        checkForGG = false;

        int random = rand.nextInt(5);

        switch (random) {
        case 0:
            PlayerUtils.giveItem(player, new ItemStack(Material.DIAMOND));
            break;
        case 1:
            PlayerUtils.giveItem(player, new ItemStack(Material.GOLD_INGOT));
            break;
        case 2:
            PlayerUtils.giveItem(player, new ItemStack(Material.IRON_INGOT));
            break;
        case 3:
            PlayerUtils.giveItem(player, new ItemStack(Material.CAKE));
            break;
        case 4:
            ItemStack item = new ItemStack(Material.FIREWORK);
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            Builder builder = FireworkEffect.builder();

            builder.flicker(rand.nextBoolean());
            builder.trail(rand.nextBoolean());
            builder.withColor(randomColor());
            builder.with(randomType());

            if (rand.nextBoolean()) {
                builder.withFade(randomColor());
            }

            meta.addEffect(builder.build());
            meta.setPower(rand.nextInt(2) + 1);
            item.setItemMeta(meta);

            PlayerUtils.giveItem(player, item);
            break;
        }

        player.sendMessage(PREFIX + "You were the first to say 'Good Game'.");
        player.sendMessage(PREFIX + "Enjoy your reward!");
    }

    /**
     * Gets an random color.
     *
     * @return A random color.
     */
    private Color randomColor() {
        return Color.fromBGR(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    /**
     * Gets an random firework type.
     *
     * @return A random firework type.
     */
    private Type randomType() {
        return Type.values()[rand.nextInt(Type.values().length)];
    }
}