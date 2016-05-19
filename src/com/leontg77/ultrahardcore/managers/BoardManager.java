package com.leontg77.ultrahardcore.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;

/**
 * The class for managing scoreboards.
 * <p>
 * This class contains methods for setting scores, getting scores, setting up boards and resettings scores.
 * 
 * @author LeonTG77
 */
public class BoardManager {
    private final Main plugin;

    private final Scoreboard board;

    private Objective nameHealth;
    private Objective tabHealth;
    private Objective hearts;

    private Objective backup;
    private Objective kills;

    public BoardManager(Main plugin) {
        this.plugin = plugin;

        board = Bukkit.getScoreboardManager().getMainScoreboard();

        nameHealth = board.getObjective("nameHealth");
        tabHealth = board.getObjective("tabHealth");
        hearts = board.getObjective("hearts");

        backup = board.getObjective("backup");
        kills = board.getObjective("kills");
    }

    /**
     * Get the main scoreboard.
     *
     * @return The scoreboard.
     */
    public Scoreboard getBoard() {
        return board;
    }

    /**
     * Get the health below the name objective.
     *
     * @return The objective.
     */
    public Objective getNameHealObjective() {
        return nameHealth;
    }

    /**
     * Get the percent health in tab objective.
     *
     * @return The objective.
     */
    public Objective getTabHealthObjective() {
        return tabHealth;
    }

    /**
     * Get the tab hearts objective.
     *
     * @return The objective.
     */
    public Objective getTabHeartsObjective() {
        return hearts;
    }

    /**
     * Get the sidebar kills objective.
     *
     * @return The objective.
     */
    public Objective getKillsObjective() {
        return kills;
    }

    /**
     * Get the sidebar kill backup objective.
     *
     * @return The objective.
     */
    public Objective getBackupObjective() {
        return backup;
    }

    /**
     * Setup the scoreboard objectives.
     */
    public void setup(Game game) {
        if (board.getObjective("kills") == null) {
            kills = board.registerNewObjective("kills", "dummy");
        }

        if (board.getObjective("backup") == null) {
            backup = board.registerNewObjective("backup", "dummy");
        }

        if (board.getObjective("tabHealth") == null) {
            tabHealth = board.registerNewObjective("tabHealth", "dummy");
        }

        if (board.getObjective("nameHealth") == null) {
            nameHealth = board.registerNewObjective("nameHealth", "dummy");
        }

        if (board.getObjective("hearts") == null) {
            hearts = board.registerNewObjective("hearts", "health");
        }

        if (game.isRecordedRound()) {
            kills.setDisplayName("§8» §6" + game.getRRName() + "§8 «");
        } else {
            kills.setDisplayName("§4Arctic §8» §7§o" + game.getHost().substring(0, Math.min(game.getHost().length(), 13)) + "§r");
        }

        if (!game.arenaBoard()) {
            kills.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        nameHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
        nameHealth.setDisplayName("§4♥");

        plugin.getLogger().info("Scoreboards has been setup.");
    }

    /**
     * Sets the score of the given player.
     *
     * @param entry the player setting it for.
     * @param newScore the new score.
     */
    public void setScore(final String entry, final int newScore) {
        final Score backupScore = backup.getScore(entry);
        final Score killScore = kills.getScore(entry);

        backupScore.setScore(newScore);
        killScore.setScore(newScore);
    }

    /**
     * Gets a score for the given string.
     *
     * @param entry the wanted string.
     * @return The score of the string.
     */
    public int getScore(final String entry) {
        return kills.getScore(entry).getScore();
    }

    /**
     * Gets a actual score for the given string.
     *
     * @param entry the wanted string.
     * @return The actual score of the string.
     */
    public int getActualScore(final String entry) {
        return backup.getScore(entry).getScore();
    }

    /**
     * Reset the score of the given string.
     *
     * @param entry the string resetting.
     */
    public void resetScore(final String entry) {
        int score = getActualScore(entry);

        board.resetScores(entry);

        // this should never be reset, so i'm adding it back! :D
        for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
            if (offline.getName().equalsIgnoreCase(entry)) {
                backup.getScore(entry).setScore(score);
                break;
            }
        }
    }
}