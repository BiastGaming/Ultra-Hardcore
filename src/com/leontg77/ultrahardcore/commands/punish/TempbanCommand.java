package com.leontg77.ultrahardcore.commands.punish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils.PunishmentType;

/**
 * Tempban command class.
 * 
 * @author LeonTG77
 */
public class TempbanCommand extends UHCCommand {
    private final BoardManager board;
    private final Main plugin;

    /**
     * Tempban command class constructor.
     *
     * @param board The board manager class.
     */
    public TempbanCommand(Main plugin, BoardManager board) {
        super("tempban", "<player> <time> <reason>");

        this.plugin = plugin;
        this.board = board;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        BanList list = Bukkit.getBanList(Type.NAME);

        if (list.isBanned(args[0])) {
            throw new CommandException("'" + args[0] + "' is already banned.");
        }

        long time = DateUtils.parseDateDiff(args[1], true);
        Date date = new Date(time);

        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length));
        User user = plugin.getUser(PlayerUtils.getOfflinePlayer(args[0]));

        if (user.getRank().getLevel() >= Rank.STAFF.getLevel()) {
            throw new CommandException("'" + args[0] + "' is a staff member and can't be temp-banned.");
        }

        if (target == null) {
            PunishUtils.savePunishment(user, PunishmentType.TEMPBAN, message, date);

            PlayerUtils.broadcast(Main.PREFIX + "§6" + args[0] + " §7has been temp-banned for §a" + message + "§7. §8(§a" + DateUtils.formatDateDiff(time) + "§8)");

            list.addBan(args[0], message, date, sender.getName());
            board.resetScore(args[0]);
            return true;
        }

        if (target.hasPermission("uhc.staff")) {
            throw new CommandException("You can't temp-ban this player.");
        }

        PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been temp-banned for §a" + message + "§7. §8(§a" + DateUtils.formatDateDiff(time) + "§8)");

        BanEntry ban = list.addBan(target.getName(), message, date, sender.getName());
        target.setWhitelisted(false);

        board.resetScore(args[0]);
        board.resetScore(target.getName());

        PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
        Bukkit.getPluginManager().callEvent(event);

        new BukkitRunnable() {
            public void run() {
                Block block = target.getLocation().getBlock();

                block.setType(Material.AIR);
                block = block.getRelative(BlockFace.UP);
                block.setType(Material.AIR);
            }
        }.runTaskLater(plugin, 2);

        target.kickPlayer(String.format(PunishUtils.getTempbanMessageFormat(), ban.getReason(), ban.getSource(), DateUtils.formatDateDiff(time)));
        PunishUtils.savePunishment(plugin.getUser(target), PunishmentType.TEMPBAN, message, date);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return allPlayers();
    }
}