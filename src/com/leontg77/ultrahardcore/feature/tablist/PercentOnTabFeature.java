package com.leontg77.ultrahardcore.feature.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Percent on tab feature.
 * 
 * @author LeonTG77
 */
public class PercentOnTabFeature extends Feature {
    private BukkitRunnable task;

    public PercentOnTabFeature(final Main plugin, final BoardManager board) {
        super("Percent on tab", "Makes the tablist have your percent health.");

        task = new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    String percentString = NumberUtils.makePercent(online.getHealth());
                    int percent = Integer.parseInt(percentString.substring(2));

                    Scoreboard sb = board.getBoard();

                    Objective bellowName = sb.getObjective("nameHealth");
                    Objective tabList = sb.getObjective("tabHealth");

                    if (tabList != null) {
                        Score score = tabList.getScore(online.getName());
                        score.setScore(percent);
                    }

                    if (bellowName != null) {
                        Score score = bellowName.getScore(online.getName());
                        score.setScore(percent);
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
    }
}