package com.leontg77.ultrahardcore.commands.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.events.ScatterEvent;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Hotbar command class.
 * 
 * @author LeonTG77
 */
public class HotbarCommand extends UHCCommand implements Listener {
    private final Arena arena;
    private final Main plugin;

    public HotbarCommand(Main plugin, Arena arena) {
        super("hotbar", "");

        this.plugin = plugin;
        this.arena = arena;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final List<String> list = new ArrayList<String>();

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can change their hotbar.");
        }

        if (!game.isState(State.OPEN) && !game.isState(State.CLOSED) && !game.isState(State.NOT_RUNNING)) {
            throw new CommandException(Arena.PREFIX + "You cannot edit your hotbar setup while a game is running.");
        }

        Player player = (Player) sender;

        if (arena.isEnabled() && arena.hasPlayer(player)) {
            throw new CommandException(Arena.PREFIX + "You cannot edit your inventory in the arena.");
        }

        User user = plugin.getUser(player);
        user.resetInventory();

        arena.giveDefaultKit(player);
        list.add(player.getName());

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.hidePlayer(player);
        }

        player.sendMessage(Arena.PREFIX + "Open your inventory to edit your hotbar.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!list.contains(player.getName())) {
            return;
        }

        PlayerUtils.giveItem(player, player.getItemOnCursor());
        player.setItemOnCursor(new ItemStack(Material.AIR));

        User user = plugin.getUser(player);

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item == null) {
                item = new ItemStack(Material.AIR);
            }

            user.getConfig().set("hotbar." + i, item.clone().serialize());
        }

        user.saveConfig();

        player.sendMessage(Arena.PREFIX + "Arena hotbar saved.");

        list.remove(player.getName());
        user.resetInventory();

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!list.contains(player.getName())) {
            return;
        }

        if (event.getSlotType().equals(SlotType.ARMOR)) {
            player.sendMessage(Arena.PREFIX + "You cannot edit your armor while setting up your arena hotbar.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!list.contains(player.getName())) {
            return;
        }

        player.sendMessage(Arena.PREFIX + "You cannot drop your items while setting up your arena hotbar.");
        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!list.contains(player.getName())) {
            return;
        }

        player.sendMessage(Arena.PREFIX + "You cannot interact with the world while setting up your arena hotbar.");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = (Player) event.getPlayer();

        if (!list.contains(player.getName())) {
            return;
        }

        player.sendMessage(Arena.PREFIX + "You cannot do commands while setting up your arena hotbar.");
        player.sendMessage(Arena.PREFIX + "Open your inventory to edit your hotbar.");

        event.setCancelled(true);
    }

    @EventHandler
    public void on(ScatterEvent event) {
        for (String entry : list) {
            Player player = Bukkit.getPlayer(entry);

            if (player != null) {
                User user = plugin.getUser(player);

                player.sendMessage(Arena.PREFIX + "Hotbar setup cancelled as a game is starting.");
                user.resetInventory();

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.showPlayer(player);
                }
            }
        }

        list.clear();
    }
}