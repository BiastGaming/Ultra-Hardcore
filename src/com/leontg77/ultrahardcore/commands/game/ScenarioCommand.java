package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Scenario command class.
 * 
 * @author LeonTG77
 */
public class ScenarioCommand extends UHCCommand {
    private final ScenarioManager manager;
    private final Main plugin;

    public ScenarioCommand(Main plugin, ScenarioManager manager) {
        super("scenario", "<enable|disable|list|info> [scenario]");

        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("enable")) {
                if (sender.hasPermission(getPermission() + ".manage")) {
                    if (args.length == 1) {
                        return false;
                    }

                    Scenario scen = manager.getScenario(args[1]);

                    if (scen == null) {
                        throw new CommandException("'" + args[1] + "' is not a scenario.");
                    }

                    if (scen.isEnabled()) {
                        sender.sendMessage(Main.PREFIX + "§a" + scen.getName() + " §7is already enabled.");
                        return true;
                    }

                    PlayerUtils.broadcast(Main.PREFIX + "§a" + scen.getName() + " §7has been enabled.");
                    scen.enable(plugin);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("disable")) {
                if (sender.hasPermission(getPermission() + ".manage")) {
                    if (args.length == 1) {
                        return false;
                    }

                    Scenario scen = manager.getScenario(args[1]);

                    if (scen == null) {
                        throw new CommandException("'" + args[1] + "' is not a scenario.");
                    }

                    if (!scen.isEnabled()) {
                        sender.sendMessage(Main.PREFIX + "§a" + scen.getName() + " §7is not enabled.");
                        return true;
                    }

                    PlayerUtils.broadcast(Main.PREFIX + "§a" + scen.getName() + " §7has been disabled.");
                    scen.disable();
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder list = new StringBuilder("");
                int i = 1;

                for (Scenario scen : manager.getScenarios()) {
                    if (list.length() > 0) {
                        if (i == manager.getScenarios().size()) {
                            list.append("§8 and §7");
                        } else {
                            list.append("§8, §7");
                        }
                    }

                    list.append((scen.isEnabled() ? "§a" : "§c") + scen.getName());
                    i++;
                }

                sender.sendMessage(Main.PREFIX + "List of all scenarios: §8(§6" + manager.getScenarios().size() + "§8)");
                sender.sendMessage(Main.ARROW + list.toString().trim());
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                if (args.length == 1) {
                    return false;
                }

                Scenario scen = manager.getScenario(args[1]);

                if (scen == null) {
                    throw new CommandException("'" + args[1] + "' is not a scenario.");
                }

                sender.sendMessage(Main.PREFIX + "Information about §6" + scen.getName() + "§8:");
                sender.sendMessage(Main.ARROW + "§f§o" + scen.getDescription());
                return true;
            }
        }

        int size = manager.getEnabledScenarios().size();

           sender.sendMessage(Main.PREFIX + "Currently enabled scenarios: §8(§6" + (size == 0 ? 1 : size) + "§8)");

        if (size == 0) {
               sender.sendMessage(Main.ARROW + "Vanilla+ §8- §f§oA normal UHC with a few tweaks.");
            return true;
        }

           for (Scenario scen : manager.getEnabledScenarios()) {
               sender.sendMessage(Main.ARROW + scen.getName() + " §8- §f§o" + scen.getDescription());
           }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("list");
            toReturn.add("info");

            if (sender.hasPermission(getPermission() + ".manage")) {
                toReturn.add("enable");
                toReturn.add("disable");
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                for (Scenario scen : manager.getScenarios()) {
                    toReturn.add(scen.getName());
                }
            }

            if (sender.hasPermission(getPermission() + ".manage")) {
                if (args[0].equalsIgnoreCase("enable")) {
                    for (Scenario scen : manager.getDisabledScenarios()) {
                        toReturn.add(scen.getName());
                    }
                } else if (args[0].equalsIgnoreCase("disable")) {
                    for (Scenario scen : manager.getEnabledScenarios()) {
                        toReturn.add(scen.getName());
                    }
                }
            }

        }

        return toReturn;
    }
}