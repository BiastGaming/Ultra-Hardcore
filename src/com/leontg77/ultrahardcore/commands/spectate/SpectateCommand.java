package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Spectate command class.
 * 
 * @author LeonTG77
 */
public class SpectateCommand extends UHCCommand {
    private final SpecManager spec;
    private final Game game;

    public SpectateCommand(Game game, SpecManager spec) {
        super("spectate", "<on|off|toggle|list|cmdspy|info> [player]");

        this.game = game;
        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("uhc.staff") && !spec.isSpectating(sender.getName())) {
                throw new CommandException("You are not spectating.");
            }

            if (spec.getSpectators().isEmpty()) {
                sender.sendMessage(Main.PREFIX + "There are no spectators.");
                return true;
            }

            Set<String> specs = spec.getSpectators();

            StringBuilder spectatorList = new StringBuilder();
            int i = 1;

            for (String spectator : specs) {
                if (spectatorList.length() > 0) {
                    if (i == specs.size()) {
                        spectatorList.append(" §8and §a");
                    } else {
                        spectatorList.append("§8, §a");
                    }
                }

                spectatorList.append((Bukkit.getPlayer(spectator) == null ? "§c" : "§a") + spectator);
                i++;
            }

            sender.sendMessage(Main.PREFIX + "There are §6" + (i - 1) + " §7spectators.");
            sender.sendMessage(Main.ARROW + "Spectators§8: §a" + spectatorList.toString() + "§8.");
            return true;
        }

        Player target;

        if (args.length == 1 || !sender.hasPermission(getPermission() + ".others")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can manage their spectator mode.");
            }

            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                throw new CommandException("'" + args[1] + "' is not online.");
            }
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (!sender.hasPermission("uhc.prelist")) {
                sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                return true;
            }

            if (game.getWorlds().contains(target.getWorld())) {
                throw new CommandException("You need to be in spawn to do this.");
            }

            if (target != sender) {
                sender.sendMessage(Main.PREFIX + "You toggled " + target.getName() + "'s spectator mode.");
            }
            target.sendMessage(Main.PREFIX + "Your spectator mode was toggled.");

            spec.toggle(target);
            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (!sender.hasPermission("uhc.prelist")) {
                sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                return true;
            }

            if (spec.isSpectating(target)) {
                throw new CommandException((target == sender ? "You are" : "'" + target.getName() + "' is") + " already spectating.");
            }

            if (game.isState(State.INGAME) && game.getWorlds().contains(target.getWorld())) {
                throw new CommandException("You need to be in spawn to do this.");
            }

            if (target != sender) {
                sender.sendMessage(Main.PREFIX + "You enabled " + target.getName() + "'s spectator mode.");
            }
            target.sendMessage(Main.PREFIX + "Your spectator mode was enabled.");

            spec.enableSpecmode(target);
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (!sender.hasPermission("uhc.prelist")) {
                sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                return true;
            }

            if (!spec.isSpectating(target)) {
                throw new CommandException((target == sender ? "You are" : "'" + target.getName() + "' is") + " not spectating.");
            }

            if (game.getWorlds().contains(target.getWorld())) {
                throw new CommandException("You need to be in spawn to do this.");
            }

            if (target != sender) {
                sender.sendMessage(Main.PREFIX + "You disabled " + target.getName() + "'s spectator mode.");
            }
            target.sendMessage(Main.PREFIX + "Your spectator mode was disabled.");

            spec.disableSpecmode(target);
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (!spec.isSpectating(target)) {
                throw new CommandException((target == sender ? "You are" : "'" + target.getName() + "' is") + " not spectating.");
            }

            if (spec.getSpecInfoers().contains(target.getName())) {
                spec.getSpecInfoers().remove(target.getName());

                if (target != sender) {
                    sender.sendMessage(Main.PREFIX + "You disabled " + target.getName() + "'s specinfo.");
                }
                target.sendMessage(Main.PREFIX + "Your specinfo has been disabled.");
            } else {
                spec.getSpecInfoers().add(target.getName());

                if (target != sender) {
                    sender.sendMessage(Main.PREFIX + "You enabled " + target.getName() + "'s specinfo.");
                }
                target.sendMessage(Main.PREFIX + "Your specinfo has been enabled.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("cmdspy")) {
            if (!target.hasPermission("uhc.cmdspy")) {
                sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                return true;
            }

            if (spec.getCommandSpies().contains(target.getName())) {
                spec.getCommandSpies().remove(target.getName());

                if (target != sender) {
                    sender.sendMessage(Main.PREFIX + "You enabled " + target.getName() + "'s commandspy.");
                }
                target.sendMessage(Main.PREFIX + "Your commandspy has been enabled.");
            } else {
                spec.getCommandSpies().add(target.getName());

                if (target != sender) {
                    sender.sendMessage(Main.PREFIX + "You disabled " + target.getName() + "'s commandspy.");
                }
                target.sendMessage(Main.PREFIX + "Your commandspy has been disabled.");
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("help");
            toReturn.add("on");
            toReturn.add("off");
            toReturn.add("toggle");
            toReturn.add("list");
            toReturn.add("cmdspy");
            toReturn.add("info");
        }

        if (args.length == 2) {
            if (!sender.hasPermission("uhc.spectate.other")) {
                return new ArrayList<String>();
            }

            if (args[0].equalsIgnoreCase("on")) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!spec.isSpectating(online)) {
                        toReturn.add(online.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("off")) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (spec.isSpectating(online)) {
                        toReturn.add(online.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("toggle")) {
                return allPlayers();
            } else if (args[0].equalsIgnoreCase("cmdspy")) {
                return allPlayers();
            } else if (args[0].equalsIgnoreCase("info")) {
                return allPlayers();
            }
        }

        return toReturn;
    }
}