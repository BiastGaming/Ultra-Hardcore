package com.leontg77.ultrahardcore.feature.tablist;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;

/**
 * Hearts on tab feature.
 * 
 * @author LeonTG77
 */
public class HeartsOnTabFeature extends ToggleableFeature {
    private final BoardManager board;

    public HeartsOnTabFeature(BoardManager board) {
        super("Hearts on tab", "Makes your health on the tab list be hearts instead of percent.");

        icon.setType(Material.INK_SACK);
        icon.setDurability((short) 1);

        slot = 7;

        this.board = board;
    }

    @Override
    public void onDisable() {
        board.getTabHealthObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);

        for (Player online : Bukkit.getOnlinePlayers()) {
            double health = online.getHealth();

            online.setHealth(online.getHealth() == 19.0 ? 18.0 : 19.0);
            online.setHealth(health);
        }
    }

    @Override
    public void onEnable() {
        board.getTabHeartsObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);

        for (Player online : Bukkit.getOnlinePlayers()) {
            double health = online.getHealth();

            online.setHealth(online.getHealth() == 19.0 ? 18.0 : 19.0);
            online.setHealth(health);
        }
    }
}