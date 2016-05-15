package com.leontg77.ultrahardcore.scenario.scenarios.soulbrothers;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.leontg77.ultrahardcore.scenario.scenarios.SoulBrothers.PREFIX;

public class SoulBrothersTeamTeleportTask extends BukkitRunnable {

    public static final Function<Team, String> TEAM_AS_TEXT =
            team -> team.getName() + "[" + Joiner.on(", ").join(team.getEntries()) + "]";

    protected static final String START = PREFIX + "Soul-brotherifying %s teams over %s worlds";
    protected static final String TEAM_TOO_BIG = PREFIX + "Failed team %s/%s (%s): Team size is larger than amount of worlds";
    protected static final String NOBODY_ONLINE = PREFIX + "Failed team %s/%s (%s): Everybody offline";
    protected static final String TELEPORTING = PREFIX + "Teleporting team %s/%s: %s";
    protected static final String SCHEDULED = PREFIX + "%s is offline, scheduled teleport";

    protected final PlayerTeleporter teleporter;
    protected final List<World> worlds;
    protected final LinkedList<Team> teams;
    protected final int teamsSize;

    protected int currentTeamCount = 0;

    public SoulBrothersTeamTeleportTask(
            PlayerTeleporter teleporter,
            List<World> worlds,
            Collection<Team> teams
    ) {
        this.teleporter = teleporter;
        this.worlds = worlds;
        this.teams = new LinkedList<>(teams);
        teamsSize = teams.size();
    }

    @Override
    public void run() {
        if (currentTeamCount == 0) {
            Bukkit.broadcastMessage(String.format(START, teamsSize, worlds.size()));
        }

        if (teams.isEmpty()) {
            cancel();
            return;
        }

        Team team = teams.poll();
        currentTeamCount++;

        String teamText = TEAM_AS_TEXT.apply(team);

        if (team.getSize() > worlds.size()) {
            Bukkit.broadcastMessage(String.format(TEAM_TOO_BIG, currentTeamCount, teamsSize, teamText));
            return;
        }

        List<OfflinePlayer> teamPlayers = team.getEntries().stream()
                .map(PlayerUtils::getOfflinePlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Optional<Player> firstOnlinePlayer = teamPlayers.stream()
                .filter(OfflinePlayer::isOnline)
                .map(OfflinePlayer::getPlayer)
                .findAny();

        if (!firstOnlinePlayer.isPresent()) {
            Bukkit.broadcastMessage(String.format(NOBODY_ONLINE, currentTeamCount, teamsSize, teamText));
            return;
        }

        Location location = firstOnlinePlayer.get().getLocation();
        Bukkit.broadcastMessage(String.format(TELEPORTING, currentTeamCount, teamsSize, teamText));

        int currentWorldCount = 0;
        for (OfflinePlayer teamPlayer : teamPlayers) {
            location = location.clone();
            location.setWorld(worlds.get(currentWorldCount++));

            PlayerTeleporter.Result teleportResult = teleporter.teleport(teamPlayer, location);
            if (teleportResult == PlayerTeleporter.Result.ON_NEXT_LOGIN) {
                Bukkit.broadcastMessage(String.format(SCHEDULED, teamPlayer.getName()));
            }
        }
    }
}
