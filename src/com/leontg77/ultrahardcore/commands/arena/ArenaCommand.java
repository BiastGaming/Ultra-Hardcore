package com.leontg77.ultrahardcore.commands.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.minigames.Parkour;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Arena command class.
 * 
 * @author LeonTG77
 */
public class ArenaCommand extends UHCCommand {
    private final Game game;

    private final Parkour parkour;
    private final Arena arena;

    private final BoardManager board;
    private final SpecManager spec;

    public ArenaCommand(Arena arena, Game game, Parkour parkour, SpecManager spec, BoardManager board) {
        super("arena", "[enable|disable|reset|board|leave]");

        this.game = game;

        this.parkour = parkour;
        this.arena = arena;

        this.board = board;
        this.spec = spec;
    }

    private static final String ADMIN_PERMISSION = "uhc.arena.admin";

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (sender.hasPermission(ADMIN_PERMISSION)) {
                if (args[0].equalsIgnoreCase("enable")) {
                    if (arena.isEnabled()) {
                        throw new CommandException("The arena is already enabled.");
                    }

                    if (!sender.isOp() && Bukkit.hasWhitelist()) {
                        throw new CommandException("You cannot enable the arena with the whitelist on.");
                    }

                    PlayerUtils.broadcast(Arena.PREFIX + "The arena has been enabled.");
                    PlayerUtils.broadcast(Arena.PREFIX + "You can use §a/a §7to join it.");

                    arena.enable();
                    return true;
                }

                if (args[0].equalsIgnoreCase("disable")) {
                    if (!arena.isEnabled()) {
                        throw new CommandException("The arena is not enabled.");
                    }

                    PlayerUtils.broadcast(Arena.PREFIX + "The arena has been disabled.");
                    arena.disable();
                    return true;
                }

                if (args[0].equalsIgnoreCase("reset")) {
                    arena.reset();
                    return true;
                }

                if (args[0].equalsIgnoreCase("board")) {
                    if (!arena.isEnabled()) {
                        throw new CommandException("The arena is not enabled.");
                    }

                    if (game.arenaBoard()) {
                        for (String entry : arena.getBoard().getEntries()) {
                            arena.resetScore(entry);
                        }

                        PlayerUtils.broadcast(Arena.PREFIX + "The arena board has been disabled.");
                        game.setArenaBoard(false);

                        board.getKillsObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
                    } else {
                        PlayerUtils.broadcast(Arena.PREFIX + "The arena board has been enabled.");
                        arena.getKillBoard().setDisplaySlot(DisplaySlot.SIDEBAR);

                        game.setPregameBoard(false);
                        game.setArenaBoard(true);
                    }
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("leave")) {
                if (!(sender instanceof Player)) {
                    throw new CommandException("Only players can use the arena.");
                }

                Player player = (Player) sender;

                if (!arena.isEnabled()) {
                    throw new CommandException("The arena is currently disabled.");
                }

                if (!arena.hasPlayer(player)) {
                    throw new CommandException("You are not in the arena.");
                }

                arena.removePlayer(player, false);;
                return true;
            }
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can use the arena.");
        }

        Player player = (Player) sender;

        if (!arena.isEnabled()) {
            throw new CommandException("The arena is currently disabled.");
        }

        if (arena.hasPlayer(player)) {
            throw new CommandException("You are already in the arena.");
        }

        if (parkour.isParkouring(player)) {
            throw new CommandException("You cannot join the arena while parkouring\n§cTo leave the parkour use /parkour leave!");
        }

        if (spec.isSpectating(player)) {
            throw new CommandException("You cannot join the arena as a spectator.");
        }

        arena.addPlayer(player);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("leave");

            if (sender.hasPermission(ADMIN_PERMISSION)) {
                toReturn.add("enable");
                toReturn.add("disable");
                toReturn.add("reset");
                toReturn.add("board");
            }
        }

        return toReturn;
    }
}