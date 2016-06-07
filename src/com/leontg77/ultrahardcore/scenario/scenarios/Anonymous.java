package com.leontg77.ultrahardcore.scenario.scenarios;

import java.io.File;
import java.util.UUID;

import org.bukkit.scoreboard.DisplaySlot;

import com.comphenix.protocol.ProtocolLibrary;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.scenarios.anonymous.CachedProfileParser;
import com.leontg77.ultrahardcore.scenario.scenarios.anonymous.DisguiseController;
import com.leontg77.ultrahardcore.scenario.scenarios.anonymous.MojangAPIProfileParser;

/**
 * Anonymous scenario class.
 * 
 * @author LeonTG77
 */
public class Anonymous extends Scenario {
    private final BoardManager board;

    public Anonymous(BoardManager board) {
        super("Anonymous", "Everyone is disguised as the same player.");
        
        this.board = board;
    }

    private DisguiseController disguise;
    
    @Override
    public void onDisable() {
        if (disguise == null) {
            return;
        }
        
        disguise.disable();
        board.setup(game);
    }

    @Override
    public void onEnable() {
        try {
            disguise = new DisguiseController(
                UUID.fromString("640a5372-780b-4c2a-b7e7-8359d2f9a6a8"),
                getName(),
                new CachedProfileParser(new MojangAPIProfileParser(), new File(plugin.getDataFolder(), "skin-cache.yml"), 2),
                plugin,
                5,
                ProtocolLibrary.getProtocolManager()
            );
            
            board.getBoard().clearSlot(DisplaySlot.PLAYER_LIST);
            board.getBoard().clearSlot(DisplaySlot.BELOW_NAME);
        } catch (Exception ex) {
            plugin.getLogger().info("Failed to start anonymous code.");
        }
    }
}