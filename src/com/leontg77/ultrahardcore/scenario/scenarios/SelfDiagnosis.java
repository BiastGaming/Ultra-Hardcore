package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.protocol.HealthUpdatePacketAdapter;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Self Diagnosis scenario class.
 * 
 * @author LeonTG77, D4mnX & ghowden
 */
public class SelfDiagnosis extends Scenario {
    private final HealthUpdatePacketAdapter fakeHP;
    private final BoardManager board;

    /**
     * Self Diagnosis class constructor.
     *
     * @param board The board manager class.
     */
    public SelfDiagnosis(BoardManager board) {
        super("SelfDiagnosis", "It's unable to see your own and opponent's health.");

        this.fakeHP = new HealthUpdatePacketAdapter(plugin);
        this.board = board;
    }

    @Override
    public void onDisable() {
        board.setup(game);
        fakeHP.disable();
    }

    @Override
    public void onEnable() {
        Scoreboard board = this.board.getBoard();

        board.clearSlot(DisplaySlot.PLAYER_LIST);
        board.clearSlot(DisplaySlot.BELOW_NAME);

        fakeHP.enable();
    }
}