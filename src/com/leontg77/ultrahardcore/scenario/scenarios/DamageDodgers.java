package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DamageDodgers scenario class
 * 
 * @author LeonTG77
 */
public class DamageDodgers extends Scenario implements Listener, CommandExecutor {
    private final Timer timer;
    private final Game game;

    private static final String PREFIX = "§4DamageDodgers §8» §7";
    private static final int DAMAGE_AMOUNT = 10000;

    public DamageDodgers(Main plugin, Game game, Timer timer) {
        super("DamageDodgers", "An amount of player deaths needed to deactivate Damage Dodgers is set by the host. If a player takes damage while Damage Dodgers is active, they will be instantly killed, and one less player will be required to turn it off. This continues until enough players have died and it deactivates, allowing players to take damage safely.");

        plugin.getCommand("dodge").setExecutor(this);

        this.timer = timer;
        this.game = game;
    }

    private int needed = 0;
    private int deaths = 0;

    @Override
    public void onDisable() {
        deaths = 0;
        needed = 0;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(event.getEntity() instanceof Player)) {
            return; // should only work for players.
        }

        Player player = (Player) entity;

        if (!game.getPlayers().contains(player)) {
            return;
        }

        deaths++;

        if (deaths > needed) {
            return;
        }

        PlayerUtils.broadcast(PREFIX + "§b" + player.getName() + " §7failed to avoid damage!");
        PlayerUtils.broadcast(PREFIX + "§b" + deaths + " §7" + (deaths == 1 ? "player" : "players") + " have perished!");

        event.setDamage(DAMAGE_AMOUNT);

        if (deaths == needed) {
            PlayerUtils.broadcast(PREFIX + "Amount of needed deaths reached, disabling damage dodgers.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Damage Dodgers is not enabled.");
            return true;
        }

        if (args.length > 0 && sender.hasPermission("uhc.damagedodgers")) {
            int amount;

            try {
                amount = parseInt(args[0]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }

            PlayerUtils.broadcast(PREFIX + "§b" + amount + "§7 amount of players has to die before Damage Dodgers disables.");
            needed = amount;
            return true;
        }

        if (deaths == needed) {
            sender.sendMessage(PREFIX + "Amount of needed deaths reached, disabling damage dodgers.");
            return true;
        }

        sender.sendMessage(PREFIX + "§b" + deaths + "§7 " + (deaths == 1 ? "player" : "players") + " have died.");
        sender.sendMessage(PREFIX + "§b" + needed + "§7 " + (needed == 1 ? "player" : "players") + " players must die in total.");
        return true;
    }
}