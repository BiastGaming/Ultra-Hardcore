package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.events.ScatterEvent;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.minigames.Parkour;
import com.leontg77.ultrahardcore.utils.EntityUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Spread command class.
 * 
 * @author LeonTG77
 */
public class ScatterCommand extends UHCCommand {
    private final SpecManager spec;

    private final Settings settings;
    private final Game game;

    private final ScatterManager scatter;
    private final TeamManager teamMan;

    private final Parkour parkour;
    private final Arena arena;

    public ScatterCommand(Game game, Settings settings, SpecManager spec, ScatterManager scatter, TeamManager teams, Parkour parkour, Arena arena) {
        super("scatter", "<teamspread> <player|*>");

        this.spec = spec;

        this.settings = settings;
        this.game = game;

        this.scatter = scatter;
        this.teamMan = teams;

        this.parkour = parkour;
        this.arena = arena;
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            return false;
        }

        World world = game.getWorld();

        if (world == null) {
            throw new CommandException("There are no worlds called '" + settings.getConfig().getString("world", "girhgqeruiogh") + "'.");
        }

        boolean teamSpread = parseBoolean(args[0]);

        if (args[1].equalsIgnoreCase("*")) {
            switch (game.getState()) {
            case NOT_RUNNING:
                if (game.isRecordedRound() || game.isPrivateGame()) {
                    game.setState(State.CLOSED);
                    break;
                }

                throw new CommandException("You can't scatter when no games are running.");
            case OPEN:
                if (game.isRecordedRound() || game.isPrivateGame()) {
                    game.setState(State.CLOSED);
                    break;
                }

                throw new CommandException("You can't scatter when the server isn't whitelisted.");
            case SCATTER:
                throw new CommandException("You can't scatter while a scatter is currently running.");
            case INGAME:
                throw new CommandException("You can't scatter when the game has started.");
            default:
                break;
            }

            if (!Bukkit.hasWhitelist()) {
                throw new CommandException("You can't scatter when the server isn't whitelisted.");
            }

            if (game.teamManagement()) {
                throw new CommandException("You can't scatter without disabling team management.");
            }

            if (!game.isPrivateGame() && !game.isRecordedRound() && !game.isMuted()) {
                throw new CommandException("You can't scatter without muting the chat.");
            }

            if (arena.isEnabled()) {
                throw new CommandException("You can't scatter without disabling the arena.");
            }

            parkour.reset();
            game.setState(State.SCATTER);

            int teams = 0;
            int solo = 0;

            List<String> toScatter = new ArrayList<String>();
            game.setPreWhitelists(true);

            if (game.getTeamSize().toLowerCase().startsWith("cto") && teamSpread) {
                for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
                    if (teamMan.getTeam(whitelisted) != null) {
                        continue;
                    }

                    Team team = teamMan.findAvailableTeam();

                    if (team != null) {
                        teamMan.joinTeam(team, whitelisted);
                    }
                }
            }

            for (Team loopTeam : teamMan.getTeams()) {
                if (loopTeam.getSize() == 0) {
                    continue;
                }

                toScatter.add(loopTeam.getName());

                if (loopTeam.getSize() > 1) {
                    teams++;
                } else {
                    solo++;
                }

                for (OfflinePlayer teammate : teamMan.getPlayers(loopTeam)) {
                    teammate.setWhitelisted(true);
                }
            }

            if (!teamSpread) {
                for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
                    if (spec.isSpectating(whitelisted.getName())) {
                        continue;
                    }

                    toScatter.add(whitelisted.getName());
                    solo++;
                }
            }

            scatter.setTeamScatter(teamSpread);
            scatter.setWorld(world);

            scatter.scatter(ImmutableList.copyOf(toScatter));

            Bukkit.getPluginManager().callEvent(new ScatterEvent());

            if (teamSpread) {
                PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + teams + " §7teams and §a" + solo + " §7solos...");
            } else {
                PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + solo + " §7players...");
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
            }

            for (World worlds : game.getWorlds()) {
                worlds.setDifficulty(Difficulty.HARD);
                worlds.setPVP(false);
                worlds.setTime(0);

                worlds.setGameRuleValue("doDaylightCycle", "false");
                worlds.setThundering(false);
                worlds.setStorm(false);

                world.setSpawnFlags(false, true);

                for (Entity mob : worlds.getEntities()) {
                    if (EntityUtils.isButcherable(mob.getType())) {
                        mob.remove();
                    }
                }
            }
        } else {
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                throw new CommandException("'" + args[1] + "' is not online.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + target.getName() + "§7...");
            target.setWhitelisted(true);

            if (teamSpread) {
                Team team = teamMan.getTeam(target);

                if (team != null) {
                    for (OfflinePlayer teammate : teamMan.getPlayers(team)) {
                        if (teammate.getPlayer() == null) {
                            continue;
                        }

                        if (teammate == target) {
                            continue;
                        }

                        if (game.isState(State.SCATTER)) {
                            for (PotionEffect effect : ScatterManager.FREEZE_EFFECTS) {
                                if (target.hasPotionEffect(effect.getType())) {
                                    target.removePotionEffect(effect.getType());
                                }

                                target.addPotionEffect(effect);
                            }
                        }

                        target.teleport(teammate.getPlayer());
                        return true;
                    }
                }
            }

            scatter.setTeamScatter(false);
            scatter.setWorld(world);

            scatter.scatter(ImmutableList.of(target.getName()));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("true");
            toReturn.add("false");
        }

        if (args.length == 2) {
            toReturn.add("*");

            for (String name : allPlayers()) {
                toReturn.add(name);
            }
        }

        return toReturn;
    }
}