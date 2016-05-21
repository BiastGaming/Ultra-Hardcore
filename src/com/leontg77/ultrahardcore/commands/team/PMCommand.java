package com.leontg77.ultrahardcore.commands.team;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * Pm command class.
 * 
 * @author LeonTG77
 */
public class PMCommand extends UHCCommand {
    private final TeamManager teams;
    private final SpecManager spec;

    public PMCommand(SpecManager spec, TeamManager teams) {
        super("pm", "<message>");

        this.teams = teams;
        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can talk in team chat.");
        }

        Player player = (Player) sender;
        Team team = teams.getTeam(player);

        if (team == null || spec.isSpectating(player)) {
            throw new CommandException("You are not on a team.");
        }

        if (args.length == 0) {
            return false;
        }

        String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        teams.sendMessage(team, TeamCommand.PREFIX + "§6§o" + player.getName() + "§8§o: §f" + msg);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return allVisiblePlayers(sender);
    }
}