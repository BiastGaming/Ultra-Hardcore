package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * MUCoords command class.
 * 
 * @author LeonTG77
 */
public class MUCoordsCommand extends UHCCommand {
    private final Timer timer;
    private final Game game;

    /**
     * MUCoords command class constructor.
     *
     * @param game The game class.
     * @param timer The timer class.
     */
    public MUCoordsCommand(Game game, Timer timer) {
        super("mucoords", "");

        this.timer = timer;
        this.game = game;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (game.getWorld() == null) {
            throw new CommandException(Main.PREFIX + "No game world has been set yet.");
        }

        if (!game.isMovedMiddle()) {
            throw new CommandException(Main.PREFIX + "This game is not a moved 0,0 game.");
        }

        if (game.isRecordedRound()) {
            if (!State.isState(State.INGAME) || timer.getPvP() < 100) {
                throw new CommandException(Main.PREFIX + "Meetup coords has not been announced yet.");
            }
        } else {
            if (!State.isState(State.INGAME) || timer.getMeetup() > 10) {
                throw new CommandException(Main.PREFIX + "Meetup coords has not been announced yet.");
            }
        }

        WorldBorder border = game.getWorld().getWorldBorder();

        sender.sendMessage(Main.PREFIX + "Meetup coords§8: §7X: §a" + border.getCenter().getBlockX() + " §7Z: §a" + border.getCenter().getBlockZ());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}