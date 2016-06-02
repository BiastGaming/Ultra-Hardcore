package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.SecretTeams;
import com.leontg77.ultrahardcore.scenario.scenarios.SelfDiagnosis;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Team command class.
 * 
 * @author LeonTG77
 */
public class TeamCommand extends UHCCommand {
    private static final String ADMIN_PERM = "uhc.team.admin";

    private final Game game;
    private final ScenarioManager scen;

    private final BoardManager board;
    private final TeamManager teams;

    public static final String PREFIX = "§4Team §8» §7";

    public TeamCommand(Game game, BoardManager board, TeamManager teams, ScenarioManager scen) {
        super("team", "");

        this.game = game;
        this.scen = scen;

        this.board = board;
        this.teams = teams;
    }

    private final Map<String, List<String>> invites = new HashMap<String, List<String>>();

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return helpMenu(sender); // the method returns true but hey, one less line :D
        }

        if (args[0].equalsIgnoreCase("info")) {
            OfflinePlayer target;

            if (args.length == 1 || scen.getScenario(SecretTeams.class).isEnabled()) {
                if (!(sender instanceof Player)) {
                    throw new CommandException("Only players can view their own team info.");
                }

                target = (Player) sender;
            } else {
                target = PlayerUtils.getOfflinePlayer(args[1]);
            }

            Team team = teams.getTeam(target);

            if (team != null && team.getName().equals("spec")) {
                team = null;
            }

            if (target.isOnline() && sender == target.getPlayer()) {
                sender.sendMessage(PREFIX + "Your team information:");
            } else {
                sender.sendMessage(PREFIX + ChatColor.GREEN + target.getName() + "'s §7team information:");
            }

            sender.sendMessage(Main.ARROW + "Team: §c" + (team == null ? "None" : team.getPrefix() + team.getName()));
            sender.sendMessage(Main.ARROW + "Kills: §a" + board.getActualScore(target.getName()));

            if (team == null) {
                return true;
            }

            if (!teams.getSavedTeams().containsKey(team.getName())) {
                teams.getSavedTeams().put(team.getName(), new HashSet<String>(team.getEntries()));
            }

            Set<String> savedTeam = teams.getSavedTeams().get(team.getName());

            StringBuilder teammates = new StringBuilder();
            int current = 1;

            int teamkills = 0;

            for (String entry : savedTeam) {
                if (teammates.length() > 0) {
                    if (current == savedTeam.size()) {
                        teammates.append(" §7and §f");
                    } else {
                        teammates.append("§7, §f");
                    }
                }

                teamkills += board.getActualScore(entry);
                current++;

                Player teammate = Bukkit.getPlayer(entry);

                if (teammate == null) {
                    teammates.append(ChatColor.RED + entry);
                    continue;
                }

                if (scen.getScenario(TeamHealth.class).isEnabled() || scen.getScenario(SelfDiagnosis.class).isEnabled() || scen.getScenario(Paranoia.class).isEnabled()) {
                    teammates.append(ChatColor.GREEN + teammate.getName());
                } else {
                    teammates.append(ChatColor.GREEN + teammate.getName() + " §8(" + NumberUtils.makePercent(teammate.getHealth()) + "%§8)");
                }
            }

            sender.sendMessage(Main.ARROW + "Team Kills: §a" + teamkills);
            sender.sendMessage(Main.ARROW + "§8------------------------------- §8§");
            sender.sendMessage(Main.ARROW + "Teammates: §8(§aGreen §7= Online, §cRed §7= Offline§8)");
            sender.sendMessage(Main.ARROW + teammates.toString().trim());
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (scen.getScenario(SecretTeams.class).isEnabled()) {
                throw new CommandException("You can't do this in secret teams.");
            }
            
            if (teams.getSavedTeams().isEmpty() || teams.getSavedTeams().keySet().toString().equalsIgnoreCase("[spec]")) {
                sender.sendMessage(PREFIX + "There are no teams.");
                return true;
            }

            sender.sendMessage(PREFIX + "List of all teams:");

            for (Team team : teams.getTeamsWithPlayers()) {
                if (team.getName().equalsIgnoreCase("spec")) {
                    continue;
                }

                Set<String> savedTeam = teams.getSavedTeams().get(team.getName());

                StringBuilder teammates = new StringBuilder();
                int current = 1;

                for (String entry : savedTeam) {
                    if (teammates.length() > 0) {
                        if (current == savedTeam.size()) {
                            teammates.append(" and ");
                        } else {
                            teammates.append(", ");
                        }
                    }

                    teammates.append(entry);
                    current++;
                }

                sender.sendMessage(team.getPrefix() + team.getName() + ": §f" + teammates.toString().trim() + ".");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team enable <teamsize>");
            }

            if (game.teamManagement()) {
                throw new CommandException(Main.PREFIX + "Team management is already enabled.");
            }

            int teamsize = parseInt(args[1], "teamsize");

            PlayerUtils.broadcast(Main.PREFIX + "Team Management has been enabled.");
            PlayerUtils.broadcast(PREFIX + "You can now create your own teams! §8(§7Max team size: §6" + teamsize + "§8)");
            PlayerUtils.broadcast(PREFIX + "You can use §a/team create §7to create the team.");

            if (game.pregameBoard()) {
                board.setScore("§e ", 14);
                board.setScore("§8» §cTeam:", 13);
                board.setScore("§8» §7/team", 12);
            }

            game.setTeamManagement(true, teamsize);
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (!game.teamManagement()) {
                throw new CommandException(Main.PREFIX + "Team management is not enabled.");
            }

            if (game.pregameBoard()) {
                board.resetScore("§e ");
                board.resetScore("§8» §cTeam:");
                board.resetScore("§8» §7/team");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Team Management has been disabled.");
            game.setTeamManagement(false, game.getTeamManagementTeamsize());
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can create teams.");
            }

            if (!game.teamManagement()) {
                throw new CommandException(PREFIX + "Team management is currently disabled.");
            }

            Player player = (Player) sender;
            Team currentTeam = teams.getTeam(player);

            if (currentTeam != null && !currentTeam.getName().equals("spec")) {
                throw new CommandException("You are already on a team.");
            }

            Team team = teams.findAvailableTeam();

            if (team == null) {
                throw new CommandException("There are no more available teams.");
            }

            invites.put(player.getName(), new ArrayList<String>());

            sender.sendMessage(PREFIX + "Team created! Use §a/team invite <player>§7 to invite a player.");
            teams.joinTeam(team, player);
            return true;
        }

        if (args[0].equalsIgnoreCase("invite")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can invite people to teams.");
            }

            if (!game.teamManagement()) {
                throw new CommandException(PREFIX + "Team management is currently disabled.");
            }

            Player player = (Player) sender;

            if (!invites.containsKey(player.getName())) {
                throw new CommandException(PREFIX + "Only the team leader can invite players to the team.");
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team invite <player>");
            }

            Team team = teams.getTeam(player);

            if (team == null || team.getName().equals("spec")) {
                throw new CommandException("You are not on a team, create one using /team create.");
            }

            if (team.getSize() >= game.getTeamManagementTeamsize()) {
                throw new CommandException("Your team is currently full.");
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                throw new CommandException("'" + args[1] + "' is not online.");
            }

            Team targetTeam = teams.getTeam(target);

            if (targetTeam != null) {
                throw new CommandException("'" + target.getName() + "' is already on a team.");
            }

            invites.get(player.getName()).add(target.getName());

            teams.sendMessage(team, PREFIX + ChatColor.GREEN + target.getName() + " §7has been invited to your team.");
            target.sendMessage(PREFIX + "You have been invited to §a" + sender.getName() + "'s §7team.");

            ComponentBuilder builder = new ComponentBuilder("");

            builder.append(PREFIX + "§b§oClick here to accept the request.");
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to join " + player.getName() + "'s team.") }));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + player.getName()));

            target.spigot().sendMessage(builder.create());
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can accept team requests.");
            }

            if (!game.teamManagement()) {
                throw new CommandException(PREFIX + "Team management is currently disabled.");
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team accept <player>");
            }

            Player player = (Player) sender;

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                throw new CommandException("'" + args[1] + "' is not online.");
            }

            if (!invites.containsKey(target.getName()) || !invites.get(target.getName()).contains(player.getName())) {
                throw new CommandException("'" + target.getName() + "' has not sent you any requests.");
            }

            Team currentTeam = teams.getTeam(player);

            if (currentTeam != null && !currentTeam.getName().equals("spec")) {
                throw new CommandException("You are already on a team.");
            }

            Team team = teams.getTeam(target);

            if (team == null) {
                throw new CommandException("'" + target.getName() + "' is not on a team");
            }

            if (team.getSize() >= game.getTeamManagementTeamsize()) {
                throw new CommandException("That team is currently full.");
            }

            teams.sendMessage(team, PREFIX + ChatColor.GREEN + player.getName() + " §7joined your team.");
            player.sendMessage(PREFIX + "Request accepted, you joined their team.");

            invites.get(target.getName()).remove(sender.getName());
            teams.joinTeam(team, player);
            return true;
        }

        if (args[0].equalsIgnoreCase("deny")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can deny team requests.");
            }

            if (!game.teamManagement()) {
                throw new CommandException(PREFIX + "Team management is currently disabled.");
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team deny <player>");
            }

            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                throw new CommandException("'" + args[1] + "' is not online.");
            }

            if (!invites.containsKey(target.getName()) || !invites.get(target.getName()).contains(player.getName())) {
                throw new CommandException("'" + target.getName() + "' has not sent you any requests.");
            }

            target.sendMessage(PREFIX + ChatColor.GREEN + player.getName() + " §7denied your request.");
            sender.sendMessage(PREFIX + "You denied the team request.");

            invites.get(target.getName()).remove(player.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can leave teams.");
            }

            Player player = (Player) sender;

            if (!game.teamManagement()) {
                throw new CommandException(PREFIX + "Team management is currently disabled.");
            }

            Team team = teams.getTeam(player);

            if (team == null || team.getName().equals("spec")) {
                throw new CommandException("You are not on a team.");
            }

            teams.sendMessage(team, Main.PREFIX + "§a" + player.getName() + "§7 left your team.");
            teams.leaveTeam(player, true);
            return true;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can kick someone from a team.");
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team kick <player>");
            }

            Player player = (Player) sender;

            if (!game.teamManagement()) {
                throw new CommandException(PREFIX + "Team management is currently disabled.");
            }

            Team team = teams.getTeam(player);

            if (team == null || team.getName().equals("spec")) {
                throw new CommandException("You are not on a team.");
            }

            if (!invites.containsKey(player.getName())) {
                throw new CommandException(PREFIX + "Only the team leader can kick players from the team.");
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                throw new CommandException("'" + args[1] + "' is not online.");
            }

            if (!team.getEntries().contains(target.getName())) {
                throw new CommandException("'" + target.getName() + "' is not on your team.");
            }

            teams.sendMessage(team, PREFIX + ChatColor.GREEN + target.getName() + " §7was kicked from your team.");
            teams.leaveTeam(target, true);
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length < 3) {
                throw new CommandException(Main.PREFIX + "Usage: /team add <team> <player>");
            }

            Team team = teams.getTeam(args[1]);

            if (team == null) {
                throw new CommandException("'" + args[1] + "' is not a vaild team.");
            }

            OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[2]);

            sender.sendMessage(PREFIX + ChatColor.GREEN + offline.getName() + "§7 was added to team §6" + team.getName() + "§7.");
            teams.joinTeam(team, offline);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team remove <player>");
            }

            OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
            Team team = teams.getTeam(offline);

            if (team == null) {
                throw new CommandException("'" + offline.getName() + "' is not on a team.");
            }

            sender.sendMessage(PREFIX + ChatColor.GREEN + offline.getName() + " §7was removed from his team.");
            teams.leaveTeam(offline, true);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team delete <team>");
            }

            Team team = teams.getTeam(args[1]);

            if (team == null) {
                throw new CommandException("'" + args[1] + "' is not a valid team.");
            }

            for (OfflinePlayer player : teams.getPlayers(team)) {
                teams.leaveTeam(player, true);
            }

            sender.sendMessage(PREFIX + "Team §a" + team.getName() + " §7has been deleted.");
            return true;
        }

        if (args[0].equalsIgnoreCase("friendlyfire") || args[0].equalsIgnoreCase("teamdamage")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team friendlyfire <true/false>");
            }

            boolean enable = parseBoolean(args[1]);

            for (Team team : teams.getTeams()) {
                team.setAllowFriendlyFire(enable);
            }

            PlayerUtils.broadcast(PREFIX + "FriendlyFire has been " + (enable ? "§aenabled" : "§cdisabled") + "§7.");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            for (Team team : board.getBoard().getTeams()) {
                for (OfflinePlayer player : teams.getPlayers(team)) {
                    teams.leaveTeam(player, true);
                }
            }

            teams.getSavedTeams().clear();
            PlayerUtils.broadcast(PREFIX + "All teams has been cleared.");
            return true;
        }

        if (args[0].equalsIgnoreCase("color")) {
            if (scen.getScenario(SecretTeams.class).isEnabled()) {
                throw new CommandException("You can't do this in secret teams.");
            }
            
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            PlayerUtils.broadcast(PREFIX + "All teams has been re-colored.");
            teams.setup(); // it doesn't do anything other than change the color if the team exist.

            for (Team team : teams.getTeamsWithPlayers()) {
                teams.sendMessage(team, PREFIX + "Your team color is now§8: " + team.getPrefix() + "this color§7!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("kickout") || args[0].equalsIgnoreCase("dq")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team " + args[0] + " <teamsize>");
            }

            if (!Bukkit.hasWhitelist()) {
                throw new CommandException("You cannot do this with the whitelist off.");
            }

            int size = parseInt(args[1], "Teamsize");

            for (Player online : Bukkit.getOnlinePlayers()) {
                Team team = teams.getTeam(online);

                if (size == 0) {
                    if (teams.getTeam(online) == null) {
                        online.setWhitelisted(false);

                        online.kickPlayer(String.format(PunishUtils.getDQMessageFormat(), "Solos are not allowed!", sender.getName()));
                    }

                    online.sendMessage(PREFIX + "All solos has been kicked!");
                } else {
                    if (team != null && team.getSize() == size) {
                        online.setWhitelisted(false);

                        online.kickPlayer(String.format(PunishUtils.getDQMessageFormat(), "To" + size + " are not allowed!", sender.getName()));
                    }

                    online.sendMessage(PREFIX + "All To" + size + " has been kicked!");
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("createteam") || args[0].equalsIgnoreCase("ct")) {
            if (!sender.hasPermission(ADMIN_PERM)) {
                return helpMenu(sender);
            }

            if (args.length == 1) {
                throw new CommandException(Main.PREFIX + "Usage: /team " + args[0] + " <players...>");
            }

            Team team = teams.findAvailableTeam();

            if (team == null) {
                throw new CommandException("There are no more available teams.");
            }

            invites.put(args[0], new ArrayList<String>());

            for (int i = 1; i < args.length; i++) {
                OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[i]);
                teams.joinTeam(team, target);
            }

            if (team.getSize() > 1) {
                teams.sendMessage(team, PREFIX + "You have been put on a team with the following people:" );


                for (OfflinePlayer entry : teams.getPlayers(team)) {
                    Player player = entry.getPlayer();

                    if (player == null) {
                        continue;
                    }

                    if (team.getSize() == 1) {
                        player.sendMessage(PREFIX + "§cNo one, you are a solo.");
                        break;
                    }

                    StringBuilder list = new StringBuilder();
                    int i = 2;

                    for (String entryTwo : team.getEntries()) {
                        if (entry.getName().equals(entryTwo)) {
                            continue;
                        }

                        if (list.length() > 0) {
                            if (i == team.getSize()) {
                                list.append(" §8and §7");
                            } else {
                                list.append("§8, §7");
                            }
                        }

                        OfflinePlayer teammate = PlayerUtils.getOfflinePlayer(entryTwo);

                        if (teammate.isOnline()) {
                            list.append(ChatColor.GREEN + entryTwo);
                        } else {
                            list.append(ChatColor.RED + entryTwo);
                            teammate.setWhitelisted(true);
                        }

                        i++;
                    }

                    player.sendMessage(PREFIX + list.toString());
                }

                teams.sendMessage(team, PREFIX + "Names in §cred §7are offline, but have been whitelisted.");
                teams.sendMessage(team, PREFIX + "Mistake? Immediately '§a/helpop WRONG TEAM!§7'.");
            }
            
            sender.sendMessage(PREFIX + "Team has been created.");
            return true;
        }

        return helpMenu(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("info");
            toReturn.add("list");

            if (game.teamManagement()) {
                toReturn.add("create");
                toReturn.add("leave");
                toReturn.add("invite");
                toReturn.add("kick");
                toReturn.add("accept");
                toReturn.add("deny");
            }

            if (sender.hasPermission("uhc.team.admin")) {
                toReturn.add("kickout");
                toReturn.add("dq");
                toReturn.add("enable");
                toReturn.add("disable");
                toReturn.add("ct");
                toReturn.add("createteam");
                toReturn.add("add");
                toReturn.add("remove");
                toReturn.add("delete");
                toReturn.add("friendlyfire");
                toReturn.add("teamdamage");
                toReturn.add("color");
                toReturn.add("clear");
            }
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
            case "invite":
            case "kick":
            case "remove":
            case "accept":
            case "deny":
            case "info":
                return allPlayers();
            case "add":
            case "delete":
                for (Team team : teams.getTeams()) {
                    toReturn.add(team.getName());
                }
                break;
            case "friendlyfire":
                toReturn.add("true");
                toReturn.add("false");
                break;
            }
        }

        if (args.length > 1) {
            if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
                return allPlayers();
            }

            if (args[0].equalsIgnoreCase("createteam") || args[0].equalsIgnoreCase("ct")) {
                return allPlayers();
            }
        }

        return toReturn;
    }

    /**
     * Sends the help list to a player.
     *
     * @param sender the player.
     */
    public boolean helpMenu(CommandSender sender) {
        sender.sendMessage(PREFIX + "Team management help:");
        sender.sendMessage("§8» §f/pm <message> §7- §f§oTalk in team chat.");
        sender.sendMessage("§8» §f/tl §7- §f§oTell your coords to your teammates.");
        sender.sendMessage("§8» §f/pmores §7- §f§oBroadcast ores in your inventory to your team.");
        sender.sendMessage("§8» §f/pmminedores §7- §f§oBroadcast your mined ores to your team.");
        sender.sendMessage("§8» §f/team info [player] §7- §f§oDisplay your or the targets team info.");
        sender.sendMessage("§8» §f/team list §7- §f§oList all teams.");

        if (game.teamManagement()) {
            sender.sendMessage("§8» §f/team create §7- §f§oCreate a team.");
            sender.sendMessage("§8» §f/team leave §7- §f§oLeave your team.");
            sender.sendMessage("§8» §f/team invite <player> §7- §f§oInvite a player to your team.");
            sender.sendMessage("§8» §f/team kick <player> §7- §f§oKick a player to your team.");
            sender.sendMessage("§8» §f/team accept <player> §7- §f§oAccept the players request.");
            sender.sendMessage("§8» §f/team deny <player> §7- §f§oDeny the players request.");
        }

        if (sender.hasPermission(ADMIN_PERM)) {
            sender.sendMessage(PREFIX + "Team management Admin help:");
            sender.sendMessage("§8» §f/team enable <teamsize> §7- §f§oEnable team management.");
            sender.sendMessage("§8» §f/team disable §7- §f§oDisable team management.");
            sender.sendMessage("§8» §f/team ct <players...> §7- §f§oCreate a team with the following players.");
            sender.sendMessage("§8» §f/team add <team> <player> §7- §f§oAdd a player to a team.");
            sender.sendMessage("§8» §f/team remove <player> §7- §f§oRemove a player from his team.");
            sender.sendMessage("§8» §f/team delete <team> §7- §f§oEmpty a specific team.");
            sender.sendMessage("§8» §f/team friendlyfire <true|false> §7- §f§oToggle FriendlyFire.");
            sender.sendMessage("§8» §f/team dq <size> §7- §f§oKicks all players on a team with the that teamsize.");
            sender.sendMessage("§8» §f/team color §7- §f§oRecolor all teams.");
            sender.sendMessage("§8» §f/team clear §7- §f§oClear all teams.");
        }
        return true;
    }
}