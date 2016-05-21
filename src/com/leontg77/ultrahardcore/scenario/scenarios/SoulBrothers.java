package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.scenarios.soulbrothers.PlayerTeleporter;
import com.leontg77.ultrahardcore.scenario.scenarios.soulbrothers.SoulBrothersTeamTeleportTask;

/**
 * Soul Brothers scenario class.
 * 
 * @author LeonTG77
 */
public class SoulBrothers extends Scenario implements CommandExecutor {
    public static final String PREFIX = "§2Soul Brothers §8» §7";

    private final PlayerTeleporter teleporter;

    private final Main plugin;
    private final Game game;

    private final ScatterManager scatter;
    private final TeamManager teams;

    /**
     * Soul Brothers scenario class constructor.
     *
     * @param plugin The main class.
     * @param game The game class.
     * @param teams The team manager class.
     * @param scatter The scatter manager class.
     */
    public SoulBrothers(Main plugin, Game game, TeamManager teams, ScatterManager scatter) {
        super("SoulBrothers", "All teammates are seperated into their own worlds, at some point they will be teleported to a final world together.");

        this.teleporter = new PlayerTeleporter();

        this.plugin = plugin;
        this.game = game;

        this.scatter = scatter;
        this.teams = teams;

        plugin.getCommand("soul").setExecutor(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(teleporter);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(teleporter, plugin);
    }

    /**
     * Get all the first worlds for teams to be in.
     *
     * @return The worlds.
     */
    public List<World> getWorlds() {
        List<World> worlds = new ArrayList<>();

        for (World world : game.getWorlds()) {
            if (world.getName().toLowerCase().contains("_sb_") && !world.getName().toLowerCase().endsWith("_sb_final")) {
                worlds.add(world);
            }
        }

        return worlds;
    }

    /**
     * Get the final world to be teleported into.
     *
     * @return The world.
     */
    public World getFinalWorld() {
        for (World world : game.getWorlds()) {
            if (world.getName().toLowerCase().endsWith("_sb_final")) {
                return world;
            }
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    protected List<OfflinePlayer> getTeamPlayers(Team team) {
        return team.getEntries().stream()
                .map(Bukkit::getOfflinePlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "Soul Brothers is not enabled.");
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "Usage: /soul <help/scatter/finalscatter/start>");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            // TODO Make this look nice ingame
            sender.sendMessage(PREFIX + "Help with hosting Soul Brothers:");
            sender.sendMessage(Main.ARROW + "§fFirst you decide how many worlds you need, then you create a world with a normal");
            sender.sendMessage(Main.ARROW + "§fworld name for a normal game, just that you end the world name with _sb_[worldnumber].");
            sender.sendMessage(Main.ARROW + "§fEx: 'leon_sb_1' and 'leon_sb_2' if you had 2 worlds (replace leon with your name).");
            sender.sendMessage(Main.ARROW + "§fNormally the worlds have the same seed, but for the final world aka the one all gets");
            sender.sendMessage(Main.ARROW + "§fscattered in at last needs to end with _sb_final! Ex: 'leon_sb_final'");
            sender.sendMessage(Main.ARROW + "§fAfter the first normal scatter finishes you do /soul scatter.");
            sender.sendMessage(Main.ARROW + "§fAnd for the final scatter you do /soul finalscatter, then /soul start to clear effects");
            sender.sendMessage(Main.ARROW + "§fYou use normal /start after the first scatter but /soul start is there for no countdown");
            sender.sendMessage(Main.ARROW + "§fand so it doesn't clear inventories. Have fun hosting, ask Leon if you got any questions!");
        }

        if (args[0].equalsIgnoreCase("scatter")) {
            List<World> worlds = getWorlds();

            if (worlds.size() < 2) {
                sender.sendMessage(PREFIX + "There are less than 2 soul brothers worlds set up. Use /soul help!");
                return true;
            }

            Collection<Team> teams;

            if (args.length == 1) {
                teams = this.teams.getTeamsWithPlayers();
            } else {
                teams = Lists.newArrayList();
                for (String player : args) {
                    Team team = this.teams.getTeam(player);

                    if (team == null) {
                        sender.sendMessage(PREFIX + player + " is not on a team.");
                    } else {
                        teams.add(team);
                    }
                }
            }

            new SoulBrothersTeamTeleportTask(teleporter, worlds, teams).runTaskTimer(plugin, 0L, 30L);
            return true;
        }

        if (args[0].equalsIgnoreCase("finalscatter")) {
            World finalWorld = getFinalWorld();

            if (finalWorld == null) {
                sender.sendMessage(PREFIX + "There is no world name that ends with '_sb_final'.");
                return true;
            }

            scatter.setTeamScatter(true);
            scatter.setWorld(finalWorld);

            List<String> toScatter = teams.getTeamsWithPlayers().stream()
                    .map(Team::getName)
                    .collect(Collectors.toList());

            game.setState(State.SCATTER);

            try {
                scatter.scatter(toScatter);
            } catch (CommandException e) {
                throw new AssertionError(e);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            game.setState(State.INGAME);

            Set<PotionEffectType> removedTypes = ScatterManager.FREEZE_EFFECTS.stream().map(PotionEffect::getType).collect(Collectors.toSet());
            game.getPlayers().stream().forEach(player -> removedTypes.forEach(player::removePotionEffect));
        }
        return true;
    }
}