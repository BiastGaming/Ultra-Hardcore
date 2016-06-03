package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * DoubleDates scenario class.
 * 
 * @author LeonTG77
 */
public class DoubleDates extends Scenario implements CommandExecutor {
    protected static final String PREFIX = "§dDouble Dates §8» §7";

    private final TeamManager teamManager;

    public DoubleDates(TeamManager teamManager) {
        super("DoubleDates", "2 and 2 teams are combined together to create a larger team.");
        this.teamManager = teamManager;

        plugin.getCommand("doubledates").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Double Dates is not enabled.");
            return true;
        }

        List<Team> teams = Lists.newArrayList(teamManager.getTeamsWithPlayers());
        int amountOfTeams = teams.size();
        
        if (amountOfTeams == 0 || amountOfTeams % 2 != 0) {
            sender.sendMessage(PREFIX + "The amount of teams (§c" + amountOfTeams + "§7) is not divisible by 2.");
            return true;
        }

        int largestTeamSize = Ordering.<Team>from((t1, t2) -> Integer.compare(t1.getSize(), t2.getSize()))
                .max(teams)
                .getSize();
        
        List<Team> teamsNotMatching = teams.stream()
                .filter(team -> team.getSize() != largestTeamSize)
                .collect(Collectors.toList());

        if (!teamsNotMatching.isEmpty()) {
            String notMatching = teamsNotMatching.stream()
                    .map(team -> team.getPrefix() + team.getName() + team.getSuffix() + "§7")
                    .collect(Collectors.joining(", "));
            
            sender.sendMessage(PREFIX + "The size of " + notMatching + " is not the largest team size (§c" + largestTeamSize + "§7).");
            return true;
        }

        Collections.shuffle(teams);
        LinkedList<Team> unmergedTeams = Lists.newLinkedList(teams);

        while (!unmergedTeams.isEmpty()) {
            List<OfflinePlayer> toMerge = Stream
                    .generate(unmergedTeams::poll)  // Poll from unmerged teams
                    .limit(2)                       // Get 2 teams per iteration
                    .map(teamManager::getPlayers)   // Map to team players
                    .flatMap(Collection::stream)    // Collapse from List<Set<P>> to List<P>
                    .collect(Collectors.toList());

            toMerge.forEach(player -> teamManager.leaveTeam(player, true, 0L));
            Team availableTeam = teamManager.findAvailableTeam();
            toMerge.forEach(player -> teamManager.joinTeam(availableTeam, player));
        }

        Bukkit.broadcastMessage(PREFIX + "Teams have been merged.");
        Bukkit.broadcastMessage(PREFIX + "Use §a/team info §7to view your teammates.");
        return true;
    }
}