package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Timeleft command class.
 * 
 * @author LeonTG77
 */
public class TimeLeftCommand extends UHCCommand {
    private final Timer timer;
    private final Game game;

    public TimeLeftCommand(Game game, Timer timer) {
        super("timeleft", "");

        this.timer = timer;
        this.game = game;
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (game.isRecordedRound()) {
            sender.sendMessage(Main.PREFIX + "Current Episode: §a" + timer.getMeetup() + ".");
            sender.sendMessage(Main.PREFIX + "Time to next episode: §a" + timer.getTimeSinceStart() + " minute(s).");
            return true;
        }

        if (game.getTeamSize().startsWith("No") || game.getTeamSize().startsWith("Open")) {
            throw new CommandException("There are no matches running.");
        }

        if (!State.isState(State.INGAME)) {
            throw new CommandException("The game has not started yet.");
        }

        int timePassed = timer.getTimeSinceStartInSeconds();
        int meetup = timer.getMeetupInSeconds();
        int pvp = timer.getPvPInSeconds();

        int finalheal = 20 - timePassed;

        sender.sendMessage(Main.PREFIX + "UHC Game Timers:");
        sender.sendMessage(Main.ARROW + "Time since start: §a" + DateUtils.ticksToString(timePassed));
        sender.sendMessage(Main.ARROW + (finalheal <= 0 ? "§eFinal heal has passed!" : "§7Final heal is given in: §a" + DateUtils.ticksToString(finalheal)));
        sender.sendMessage(Main.ARROW + (pvp <= 0 ? "§aPvP is enabled!" : "§7PvP enables in: §a" + DateUtils.ticksToString(pvp)));
        sender.sendMessage(Main.ARROW + (meetup <= 0 ? "§cMeetup is NOW!" : "§7Meetup in: §a" + DateUtils.ticksToString(meetup)));
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<String>();
    }
}