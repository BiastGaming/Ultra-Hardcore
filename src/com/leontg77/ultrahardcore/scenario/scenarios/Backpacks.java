package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Backpacks scenario class.
 * 
 * @author LeonTG77
 */
public class Backpacks extends Scenario implements Listener, CommandExecutor {
    private final Game game;

    /**
     * Backpacks scenario class constructor.
     *
     * @param plugin The main class.
     * @param game The game class.
     */
    public Backpacks(Main plugin, Game game) {
        super("Backpacks", "Players can type /bp to open up a backpack inventory.");

        this.game = game;

        plugin.getCommand("bp").setExecutor(this);
    }

    private static final String PREFIX = "§6Backpacks §8» §7";

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();
        Block block = player.getLocation().subtract(0, 1, 0).getBlock();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        block.setType(Material.CHEST);

        Inventory ender = player.getEnderChest();
        Chest chest = (Chest) block.getState();

        chest.getInventory().setContents(ender.getContents());
        ender.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can have backpacks.");
            return true;
        }

        Player player = (Player) sender;

        if (!isEnabled()) {
            player.sendMessage(PREFIX + "Backbacks are not enabled.");
            return true;
        }

        if (!State.isState(State.INGAME)) {
            player.sendMessage(ChatColor.RED + "The game hasn't started yet.");
            return true;
        }

        if (!game.getPlayers().contains(player)) {
            player.sendMessage(ChatColor.RED + "You can only use your backpack while playing.");
            return true;
        }

        player.openInventory(player.getEnderChest());
        return true;
    }
}