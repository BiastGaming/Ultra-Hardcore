package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.border.BorderShrinkFeature;
import com.leontg77.ultrahardcore.feature.border.BorderShrinkFeature.BorderShrink;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.feature.pearl.PearlDamageFeature;
import com.leontg77.ultrahardcore.feature.pvp.StalkingFeature;
import com.leontg77.ultrahardcore.feature.pvp.StalkingFeature.StalkingRule;
import com.leontg77.ultrahardcore.feature.rates.AppleRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.FlintRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.ShearsFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.ConfigGUI;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Config command class.
 * 
 * @author LeonTG77
 */
public class ConfigCommand extends UHCCommand {
    private final Main plugin;

    private final GUIManager gui;
    private final Game game;

    private final FeatureManager feat;
    private final ScenarioManager scen;

    /**
     * Config command class constructor.
     *
     * @param feat The feature manager class.
     * @param gui The inventory gui class.
     */
    public ConfigCommand(Main plugin, Game game, GUIManager gui, FeatureManager feat, ScenarioManager scen) {
        super("config", "<option> <value>");

        this.plugin = plugin;

        this.game = game;
        this.gui = gui;

        this.feat = feat;
        this.scen = scen;
    }

    /**
     * Config value class.
     *
     * @author LeonTG77
     */
    public enum ConfigValue {
        APPLERATES, BORDERSHRINK, FLINTRATES, HEADSHEAL, HOST, MATCHPOST, MAXPLAYERS, MEETUP, PEARLDAMAGE, PVP, SCENARIOS, STALKING, SHEARRATES, STATE, TEAMSIZE, WORLD;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return false;
            }

            ConfigGUI config = gui.getGUI(ConfigGUI.class);
            Player player = (Player) sender;

            player.openInventory(config.get());
            return true;
        }

        ConfigValue type;

        try {
            type = ConfigValue.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            StringBuilder types = new StringBuilder();
            int i = 1;

            for (ConfigValue value : ConfigValue.values()) {
                if (types.length() > 0) {
                    if (i == ConfigValue.values().length) {
                        types.append(" §7and§a ");
                    } else {
                        types.append("§7, §a");
                    }
                }

                types.append(value.name().toLowerCase());
                i++;
            }

            sender.sendMessage(Main.PREFIX + "Available config types: §a" + types.toString().trim() + "§7.");
            return true;
        }

        switch (type) {
        case APPLERATES:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config applerates <rate>");
                return true;
            }

            double appleRate = parseDouble(args[1], "apple rate");

            if (appleRate < 0.5) {
                throw new CommandException("Apple rates cannot be lower than vanilla (0.5%)");
            }

            if (appleRate > 10) {
                throw new CommandException("Apple rates cannot be higher than 10%");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Apple rates has been changed to §a" + NumberUtils.formatDouble(appleRate) + "%");

            feat.getFeature(AppleRatesFeature.class).setAppleRates(appleRate);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case BORDERSHRINK:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config bordershrink <shrinktime>");
                return true;
            }

            BorderShrink border;

            try {
                border = BorderShrink.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                throw new CommandException("'" + args[1] + "' is not a valid border shink.");
            }

            if (border == BorderShrink.NEVER) {
                PlayerUtils.broadcast(Main.PREFIX + "Border will no longer shrink.");
            } else {
                PlayerUtils.broadcast(Main.PREFIX + "Border will now shrink " + border.getPreText() + border.name().toLowerCase());
            }

            feat.getFeature(BorderShrinkFeature.class).setBorderShrink(border);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case STALKING:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config stalking <rule>");
                return true;
            }

            StalkingRule rule;

            try {
                rule = StalkingRule.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                throw new CommandException("'" + args[1] + "' is not a valid stalking rule.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Stalking is now " + rule.getMessage());

            feat.getFeature(StalkingFeature.class).setStalkingRule(rule);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case STATE:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config state <state>");
                return true;
            }

            State state;

            try {
                state = State.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                throw new CommandException("'" + args[1] + "' is not a vaild state.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "The server is now in §a" + state.name().toLowerCase() + "§7 mode.");
            State.setState(state);
            break;
        case FLINTRATES:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config flintrates <rate>");
                return true;
            }

            double flintRate = parseDouble(args[1], "flint rate");

            if (flintRate < 10) {
                throw new CommandException("Flint rates cannot be lower than vanilla (10%)");
            }

            if (flintRate > 100) {
                throw new CommandException("Flint rates cannot be higher than 100%");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Flint rates has been changed to §a" + NumberUtils.formatDouble(flintRate) + "%");

            feat.getFeature(FlintRatesFeature.class).setFlintRates(flintRate);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case HEADSHEAL:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config headsheal <amount>");
                return true;
            }

            double healamount = parseDouble(args[1], "heal amount");

            if (healamount < 0) {
                throw new CommandException("Heal amount cannot be negative.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Golden heads now heal §a" + NumberUtils.formatDouble(healamount) + "§7 hearts.");

            feat.getFeature(GoldenHeadsFeature.class).setHealAmount(healamount);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case HOST:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config host <name>");
                return true;
            }

            PlayerUtils.broadcast(Main.PREFIX + "The host has been changed to §a" + args[1] + "§7.");
            game.setHost(args[1]);
            break;
        case MATCHPOST:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config matchpost <shortlink>");
                return true;
            }

            String matchpost = args[1];

            if (!matchpost.toLowerCase().contains("redd.it")) {
                throw new CommandException("'" + args[1] + "' is not a valid match post.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "The matchpost has been changed to §a" + matchpost + "§7.");
            game.setMatchPost(matchpost);
            break;
        case MAXPLAYERS:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config maxplayers <amount>");
                return true;
            }

            int maxplayers = parseInt(args[1], "amount");

            if (maxplayers > Bukkit.getMaxPlayers()) {
                sender.sendMessage(ChatColor.RED + "You cannot set the slots higher than the servers max.");
                return true;
            }

            if (maxplayers < 10) {
                sender.sendMessage(ChatColor.RED + "You cannot set the slots lower than 10.");
                return true;
            }

            PlayerUtils.broadcast(Main.PREFIX + "The max player limit is now §a" + maxplayers + "§7.");
            game.setMaxPlayers(maxplayers);
            break;
        case MEETUP:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config meetup <timeInMinutes>");
                return true;
            }

            int meetup = parseInt(args[1], "meetup time");

            if (meetup < 0) {
                throw new CommandException("Meetup cannot be before the start.");
            }

            if (game.getPvP() > meetup) {
                throw new CommandException("Meetup cannot be after pvp.");
            }

            if (game.getPvP() == meetup) {
                throw new CommandException("Meetup and PvP cannot be at the same time.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Meetup is now §a" + meetup + " §7minutes in.");
            game.setMeetup(meetup);
            break;
        case PVP:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config pvp <timeInMinutes>");
                return true;
            }

            int pvp = parseInt(args[1], "pvp time");

            if (pvp < 0) {
                throw new CommandException("PvP cannot be enabled before the start.");
            }

            if (game.getMeetup() < pvp) {
                throw new CommandException("PvP cannot be enabled after meetup.");
            }

            if (game.getMeetup() == pvp) {
                throw new CommandException("PvP and Meetup cannot be at the same time.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "PvP will now be enabled §a" + pvp + " §7minutes in.");
            game.setPvP(pvp);
            break;
        case PEARLDAMAGE:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config pearldamage <amount>");
                return true;
            }

            double damage = parseDouble(args[1], "damage amount");

            if (damage < 0) {
                throw new CommandException("Pearl damage cannot be negative.");
            }

            if (damage == 0.0) {
                PlayerUtils.broadcast(Main.PREFIX + "Ender pearls will no longer deal damage.");
            } else {
                PlayerUtils.broadcast(Main.PREFIX + "Ender pearls will now deal §a" + NumberUtils.formatDouble(damage) + "§7 hearts.");
            }

            feat.getFeature(PearlDamageFeature.class).setPearlDamage(damage);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case SCENARIOS:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config scenarios <scenario1>, <scenario2>...");
                return true;
            }

            String scenarios = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

            PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + game.getAdvancedTeamSize(true, true) + scenarios + "§7.");
            game.setScenarios(scenarios);
            break;
        case SHEARRATES:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config shearrates <rate>");
                return true;
            }

            double shearRate = parseDouble(args[1], "shear rate");

            if (shearRate < 5) {
                throw new CommandException("Shear rates cannot be lower than 5%");
            }

            if (shearRate > 100) {
                throw new CommandException("Shear rates cannot be higher than 100%");
            }

            PlayerUtils.broadcast(Main.PREFIX + "Shear rates has been changed to §a" + NumberUtils.formatDouble(shearRate) + "%");

            feat.getFeature(ShearsFeature.class).setShearRates(shearRate);
            gui.getGUI(GameInfoGUI.class).update();
            break;
        case TEAMSIZE:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config teamsize <#ToX>");
                return true;
            }

            String teamsize = game.getAdvancedTeamSize(args[1], true, true);

            if (teamsize == null) {
                throw new CommandException("'" + args[1] + "' is not a valid teamsize.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + teamsize + game.getScenarios() + "§7.");
            game.setTeamSize(args[1]);
            break;
        case WORLD:
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /config world <world>");
                return true;
            }

            List<String> worlds = new ArrayList<String>();

            for (int i = 1; i < args.length; i++) {
                World world = Bukkit.getWorld(args[i]);

                if (world == null) {
                    throw new CommandException("'" + args[i] + "' is not an existing world.");
                }

                worlds.add(world.getName());
            }

            String worldList = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

            PlayerUtils.broadcast(Main.PREFIX + "The game will now be played in '§a" + worldList.replaceAll(" ", "§7, §a") + "§7'.");
            game.setWorld(worlds.toArray(new String[worlds.size()]));
            break;
        default:
            return true;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (ConfigValue type : ConfigValue.values()) {
                toReturn.add(type.name().toLowerCase());
            }
        }

        if (args.length == 2) {
            ConfigValue configType;

            try {
                configType = ConfigValue.valueOf(args[0].toUpperCase());
            } catch (Exception e) {
                return toReturn;
            }

            switch (configType) {
            case BORDERSHRINK:
                for (BorderShrink type : BorderShrink.values()) {
                    toReturn.add(type.name().toLowerCase());
                }
                break;
            case STALKING:
                for (StalkingRule type : StalkingRule.values()) {
                    toReturn.add(type.name().toLowerCase());
                }
                break;
            case HOST:
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Rank rank = plugin.getUser(online).getRank();

                    if (rank.getLevel() > 4) {
                        toReturn.add(online.getName());
                    }
                }
                break;
            case MATCHPOST:
                toReturn.add("redd.it/######");
                break;
            case SCENARIOS:
                for (Scenario type : scen.getScenarios()) {
                    toReturn.add(type.getName());
                }
                break;
            case STATE:
                for (State type : State.values()) {
                    toReturn.add(type.name().toLowerCase());
                }
                break;
            case TEAMSIZE:
                toReturn.add("FFA");
                toReturn.add("cTo");
                toReturn.add("rTo");
                toReturn.add("pTo");
                toReturn.add("mTo");
                toReturn.add("CapTo");
                toReturn.add("Auction");
                toReturn.add("No");
                toReturn.add("Open");
                break;
            case WORLD:
                for (World world : Bukkit.getWorlds()) {
                    toReturn.add(world.getName());
                }
                break;
            default:
                break;
            }
        }

        return toReturn;
    }
}