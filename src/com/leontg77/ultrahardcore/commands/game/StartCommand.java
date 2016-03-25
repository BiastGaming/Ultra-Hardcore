package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Start command class.
 * 
 * @author LeonTG77
 */
public class StartCommand extends UHCCommand {
	private final Timer timer;
	private final Game game;

	public StartCommand(Game game, Timer timer) {
		super("start", "<timefromstart> <timeuntilpvp> <timeuntilmeetup>");
		
		this.timer = timer;
		this.game = game;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		switch (State.getState()) {
		case NOT_RUNNING:
		case OPEN:
		case CLOSED:
			throw new CommandException("You can't start the game without scattering first.");
		case ENDING:
			throw new CommandException("You can't start the game when it just ended.");
		case SCATTER:
			if (game.isRecordedRound()) {
				timer.startRR();
			} else {
				timer.start();
			}
			break;
		case INGAME:
			if (args.length < 3) {
				return false;
			}
			
			int timePassed = parseInt(args[0], "time from start");
			int pvp = parseInt(args[1], "time until pvp");
			int meetup = parseInt(args[2], "time until meetup");
			
			timer.setTimeSinceStart(timePassed);
			timer.setPvP(pvp);
			timer.setMeetup(meetup);

			if (game.isRecordedRound()) {
				timer.timerRR();
			} else {
				timer.timer();
			}
			break;
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}