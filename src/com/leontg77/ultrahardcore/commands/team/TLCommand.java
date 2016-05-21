package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * TeamLoc command class.
 * 
 * @author LeonTG77
 */
public class TLCommand extends UHCCommand {
    private final Game game;

    private final TeamManager teams;
    private final SpecManager spec;

    public TLCommand(Game game, SpecManager spec, TeamManager teams) {
        super("tl", "");

        this.game = game;

        this.teams = teams;
        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can display their location to their team.");
        }

        Player player = (Player) sender;

        if (!game.getPlayers().contains(player)) {
            throw new CommandException("You are not playing a match.");
        }

        Team team = teams.getTeam(player);

        if (team == null || spec.isSpectating(player)) {
            throw new CommandException("You are not on a team.");
        }

        final Location loc = player.getLocation();

        teams.sendMessage(team, TeamCommand.PREFIX + "§6§o" + player.getName() + "§8§o: §7X: §a" + loc.getBlockX() + " §7Y: §a" + loc.getBlockY() + " §7Z: §a" + loc.getBlockZ() + " §8(§c" + environment(loc.getWorld()) + "§8)");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    /**
     * Get the String version of the given world's environment type.
     *
     * @param world The world checking.
     * @return The string version of the type.
     */
    private String environment(World world) {
        switch (world.getEnvironment()) {
        case NORMAL:
            return "Overworld";
        case NETHER:
            return "Nether";
        case THE_END:
            return "The End";
        default:
            return "Unknown";
        }
    }
}