package com.leontg77.ultrahardcore.commands.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Data;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.events.GameEndEvent;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.HardcoreHeartsFeature;
import com.leontg77.ultrahardcore.feature.scoreboard.SidebarResetFeature;
import com.leontg77.ultrahardcore.feature.tablist.HeartsOnTabFeature;
import com.leontg77.ultrahardcore.feature.tablist.TabHealthColorFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.ConfigGUI;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.gui.guis.HallOfFameGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.FireworkManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * End command class.
 * 
 * @author LeonTG77
 */
public class EndCommand extends UHCCommand {
    private final Main plugin;

    private final Timer timer;
    private final Data data;

    private final Settings settings;
    private final Game game;

    private final ScenarioManager scen;
    private final FeatureManager feat;

    private final BoardManager board;
    private final TeamManager teams;

    private final SpecManager spec;
    private final GUIManager gui;

    private final FireworkManager firework;
    private final WorldManager manager;

    public EndCommand(Main plugin, Data data, Timer timer, Settings settings, Game game, FeatureManager feat, ScenarioManager scen, BoardManager board, TeamManager teams, SpecManager spec, GUIManager gui, FireworkManager firework, WorldManager manager) {
        super("end", "<winners>");

        this.plugin = plugin;

        this.timer = timer;
        this.data = data;

        this.settings = settings;
        this.game = game;

        this.scen = scen;
        this.feat = feat;

        this.board = board;
        this.teams = teams;

        this.spec = spec;
        this.gui = gui;

        this.firework = firework;
        this.manager = manager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        List<String> winners = new ArrayList<String>();

        PlayerUtils.broadcast(Main.PREFIX + "The game has ended!");
        PlayerUtils.broadcast(" ");
        PlayerUtils.broadcast(Main.PREFIX + "The winners are:");

        int totalKills = 0;

        for (int i = 0; i < args.length; i++) {
            OfflinePlayer winner = PlayerUtils.getOfflinePlayer(args[i]);

            User user = plugin.getUser(winner);
            user.increaseStat(Stat.WINS);

            String color = teams.getTeam(winner) == null ? "§f" : teams.getTeam(winner).getPrefix();
            int kills = board.getActualScore(winner.getName());

            totalKills += kills;

            PlayerUtils.broadcast(Main.PREFIX + "§8- " + color + winner.getName() + "§8 (§a" + kills + " §7" + (kills == 1 ? "kill" : "kills") + "§8)");
            winners.add(winner.getName());
        }

        if (winners.size() > 1) {
            PlayerUtils.broadcast(Main.PREFIX + "With a total of §a" + totalKills + "§7 kills.");
        }

        PlayerUtils.broadcast(" ");
        PlayerUtils.broadcast(Main.PREFIX + "Thanks for playing and congrats to the winners!");
        PlayerUtils.broadcast(Main.PREFIX + "Remember to check out the hall of fame by using §6/hof§7.");

        Bukkit.getPluginManager().callEvent(new GameEndEvent());

        FileUtils.updateUserFiles(plugin);

        if (!game.isRecordedRound()) {
            String host = game.getHostHOFName();

            DateFormat dateFormat = new SimpleDateFormat("dd. MMM, yyyy", Locale.US);
            Date date = new Date();
            
            int matchcount = 1;

            if (settings.getHOF().contains(host) && settings.getHOF().contains(host + ".games")) {
                matchcount = settings.getHOF().getConfigurationSection(host + ".games").getKeys(false).size() + 1;
            }

            settings.getHOF().set(host + ".games." + matchcount + ".date", dateFormat.format(date));
            settings.getHOF().set(host + ".games." + matchcount + ".winners", winners);
            settings.getHOF().set(host + ".games." + matchcount + ".kills", totalKills);
            settings.getHOF().set(host + ".games." + matchcount + ".teamsize", game.getAdvancedTeamSize(false, false).trim());
            settings.getHOF().set(host + ".games." + matchcount + ".scenarios", game.getScenarios());
            settings.saveHOF();
            
            gui.getGUI(HallOfFameGUI.class).update(host);
        }

        for (Scenario scens : scen.getEnabledScenarios()) {
            scens.disable();
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (spec.isSpectating(online)) {
                spec.disableSpecmode(online);
            }

            for (Player onlineTwo : Bukkit.getOnlinePlayers()) {
                online.showPlayer(onlineTwo);
                onlineTwo.showPlayer(online);
            }

            online.setGameMode(GameMode.SURVIVAL);
            online.teleport(plugin.getSpawn());
            online.setMaxHealth(20.0);
            online.setFireTicks(0);

            User user = plugin.getUser(online);
            user.reset();
        }

        game.setState(State.NOT_RUNNING);
        firework.startFireworkShow();

        Bukkit.setIdleTimeout(60);

        game.setScenarios("games running");
        game.setMatchPost("none");
        game.setMaxPlayers(Bukkit.getMaxPlayers());
        game.setTeamSize("No");

        feat.getFeature(SidebarResetFeature.class).enable(settings);
        feat.getFeature(TabHealthColorFeature.class).disable(settings);
        feat.getFeature(HeartsOnTabFeature.class).disable(settings);
        feat.getFeature(HardcoreHeartsFeature.class).enable(settings);

        game.setRecordedRound(false);
        game.setRRName("N/A");

        gui.getGUI(GameInfoGUI.class).update();
        gui.getGUI(ConfigGUI.class).update();

        teams.getSavedTeams().clear();

        for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
            whitelisted.setWhitelisted(false);
        }

        new BukkitRunnable() {
            public void run() {
                data.clearData();

                for (String entry : board.getBoard().getEntries()) {
                    board.getBoard().resetScores(entry);
                }

                for (Team team : board.getBoard().getTeams()) {
                    for (String member : team.getEntries()) {
                        team.removeEntry(member);
                    }
                }

                PlayerUtils.broadcast(Main.PREFIX + "Reset scoreboards and teams.");

                for (World world : game.getWorlds()) {
                    manager.deleteWorld(world);
                }

                PlayerUtils.broadcast(Main.PREFIX + "Deleted used worlds.");
            }
        }.runTaskLater(plugin, 300);

        new BukkitRunnable() {
            public void run() {
                game.setHost("None");

                String kickMessage =
                "§8» §cThanks for playing! §8«" +
                "\n" +
                "\n§7If you'd like to know about updates and upcoming games," +
                "\n§7you can follow us on twitter §a@ArcticUHC§7!";

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.kickPlayer(kickMessage);
                }

                Bukkit.shutdown();
            }
        }.runTaskLater(plugin, 1200);

        timer.stopTimers();
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return allPlayers();
    }
}