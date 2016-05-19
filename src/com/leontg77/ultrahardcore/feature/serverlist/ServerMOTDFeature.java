package com.leontg77.ultrahardcore.feature.serverlist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Server MOTD feature class.
 * 
 * @author LeonTG77
 */
public class ServerMOTDFeature extends Feature implements Listener {
    private final Game game;

    public ServerMOTDFeature(Game game) {
        super("Server MOTD", "Simple changes to the server list motd that says the current game state.");

        this.game = game;
    }

    @EventHandler
    public void on(ServerListPingEvent event) {
        final String info = getInformationMessage();
        final String motd = getMOTDMessage();

        event.setMotd(
            "§4§lArctic UHC §8» §6" + motd + " §8« [§71.8§8] [§7EU§8]" +
            "\n" +
            "§8» " + info
        );

        event.setMaxPlayers(game.getMaxPlayers());
    }

    /**
     * Gets the state message that should be displayed on the server list MOTD.
     *
     * @return The server list message.
     */
    private String getMOTDMessage() {
        final State current = State.getState();

        if (game.getTeamSize().startsWith("No") || game.isRecordedRound() || game.isPrivateGame()) {
            return "No games running";
        }

        switch (current) {
        case SCATTER:
        case INGAME:
            return "Match in progress";
        case CLOSED:
            return "Whitelist is on";
        case OPEN:
            if (game.getTeamSize().startsWith("Open")) {
                return "Open " + game.getScenarios();
            }

            return "Whitelist is off";
        default:
            return "No games running";
        }
    }

    /**
     * Gets the state message that should be displayed on the server list MOTD.
     *
     * @return The server list message.
     */
    private String getInformationMessage() {
        final State current = State.getState();

        final String scen = game.getScenarios().replaceAll(", ", "§8§o, §7§o");
        final String ingameAfter = "§8§o. §4§oHost: §a§o" + game.getHost();

        final String ingameVersion = "§7§o" + game.getAdvancedTeamSize(false, true).replaceAll("-", "§8§o-§7§o") + "§7§o" + scen.substring(0, Math.min(59 - ingameAfter.length(), scen.length())) + ingameAfter;
        final String twitterVersion = "§7§oFollow us on twitter, §a§o@ArcticUHC§7§o!";

        if (game.getTeamSize().startsWith("No") || game.isRecordedRound() || game.isPrivateGame()) {
            return twitterVersion;
        }

        switch (current) {
        case SCATTER:
        case INGAME:
        case CLOSED:
            return ingameVersion;
        case OPEN:
            if (game.getTeamSize().startsWith("Open")) {
                return twitterVersion;
            }

            return ingameVersion;
        default:
            return twitterVersion;
        }
    }
}