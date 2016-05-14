package com.leontg77.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Data;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.commands.arena.ArenaCommand;
import com.leontg77.ultrahardcore.commands.arena.HotbarCommand;
import com.leontg77.ultrahardcore.commands.basic.BroadcastCommand;
import com.leontg77.ultrahardcore.commands.basic.ButcherCommand;
import com.leontg77.ultrahardcore.commands.basic.CombatLogCommand;
import com.leontg77.ultrahardcore.commands.basic.EditCommand;
import com.leontg77.ultrahardcore.commands.basic.FireCommand;
import com.leontg77.ultrahardcore.commands.basic.IgnoreCommand;
import com.leontg77.ultrahardcore.commands.basic.ListCommand;
import com.leontg77.ultrahardcore.commands.basic.ParkourCommand;
import com.leontg77.ultrahardcore.commands.basic.SetspawnCommand;
import com.leontg77.ultrahardcore.commands.basic.SkullCommand;
import com.leontg77.ultrahardcore.commands.basic.StaffChatCommand;
import com.leontg77.ultrahardcore.commands.basic.TextCommand;
import com.leontg77.ultrahardcore.commands.game.BoardCommand;
import com.leontg77.ultrahardcore.commands.game.ChatCommand;
import com.leontg77.ultrahardcore.commands.game.ConfigCommand;
import com.leontg77.ultrahardcore.commands.game.EndCommand;
import com.leontg77.ultrahardcore.commands.game.HelpopCommand;
import com.leontg77.ultrahardcore.commands.game.MUCoordsCommand;
import com.leontg77.ultrahardcore.commands.game.MatchpostCommand;
import com.leontg77.ultrahardcore.commands.game.RespawnCommand;
import com.leontg77.ultrahardcore.commands.game.ScatterCommand;
import com.leontg77.ultrahardcore.commands.game.ScenarioCommand;
import com.leontg77.ultrahardcore.commands.game.StartCommand;
import com.leontg77.ultrahardcore.commands.game.TimeLeftCommand;
import com.leontg77.ultrahardcore.commands.game.TimerCommand;
import com.leontg77.ultrahardcore.commands.game.VoteCommand;
import com.leontg77.ultrahardcore.commands.game.WhitelistCommand;
import com.leontg77.ultrahardcore.commands.give.GiveCommand;
import com.leontg77.ultrahardcore.commands.give.GiveallCommand;
import com.leontg77.ultrahardcore.commands.inventory.GameInfoCommand;
import com.leontg77.ultrahardcore.commands.inventory.HOFCommand;
import com.leontg77.ultrahardcore.commands.lag.MsCommand;
import com.leontg77.ultrahardcore.commands.lag.TpsCommand;
import com.leontg77.ultrahardcore.commands.msg.MsgCommand;
import com.leontg77.ultrahardcore.commands.msg.ReplyCommand;
import com.leontg77.ultrahardcore.commands.player.ClearInvCommand;
import com.leontg77.ultrahardcore.commands.player.ClearXPCommand;
import com.leontg77.ultrahardcore.commands.player.FeedCommand;
import com.leontg77.ultrahardcore.commands.player.FlyCommand;
import com.leontg77.ultrahardcore.commands.player.GamemodeCommand;
import com.leontg77.ultrahardcore.commands.player.HealCommand;
import com.leontg77.ultrahardcore.commands.player.HealthCommand;
import com.leontg77.ultrahardcore.commands.player.SethealthCommand;
import com.leontg77.ultrahardcore.commands.player.SetmaxhealthCommand;
import com.leontg77.ultrahardcore.commands.punish.BanCommand;
import com.leontg77.ultrahardcore.commands.punish.BanIPCommand;
import com.leontg77.ultrahardcore.commands.punish.DQCommand;
import com.leontg77.ultrahardcore.commands.punish.KickCommand;
import com.leontg77.ultrahardcore.commands.punish.MuteCommand;
import com.leontg77.ultrahardcore.commands.punish.TempbanCommand;
import com.leontg77.ultrahardcore.commands.punish.UnbanCommand;
import com.leontg77.ultrahardcore.commands.punish.UnbanIPCommand;
import com.leontg77.ultrahardcore.commands.spectate.BackCommand;
import com.leontg77.ultrahardcore.commands.spectate.GlobalChatCommand;
import com.leontg77.ultrahardcore.commands.spectate.GoodGameCommand;
import com.leontg77.ultrahardcore.commands.spectate.InvseeCommand;
import com.leontg77.ultrahardcore.commands.spectate.NearCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpecAndStaffChatCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpecChatCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpectateCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpectatorChatProtectionCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpeedCommand;
import com.leontg77.ultrahardcore.commands.spectate.TpCommand;
import com.leontg77.ultrahardcore.commands.team.PMCommand;
import com.leontg77.ultrahardcore.commands.team.PMMinedOresCommand;
import com.leontg77.ultrahardcore.commands.team.PMOresCommand;
import com.leontg77.ultrahardcore.commands.team.RandomCommand;
import com.leontg77.ultrahardcore.commands.team.TLCommand;
import com.leontg77.ultrahardcore.commands.team.TeamCommand;
import com.leontg77.ultrahardcore.commands.user.InfoCommand;
import com.leontg77.ultrahardcore.commands.user.InfoIPCommand;
import com.leontg77.ultrahardcore.commands.user.RankCommand;
import com.leontg77.ultrahardcore.commands.user.SetFixedScatterLocationCommand;
import com.leontg77.ultrahardcore.commands.user.StatsCommand;
import com.leontg77.ultrahardcore.commands.user.TopCommand;
import com.leontg77.ultrahardcore.commands.world.BorderCommand;
import com.leontg77.ultrahardcore.commands.world.PregenCommand;
import com.leontg77.ultrahardcore.commands.world.PvPCommand;
import com.leontg77.ultrahardcore.commands.world.WorldCommand;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.pvp.CombatLogFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.FireworkManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.ubl.UBL;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.WorldManager;
import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine;

/**
 * Command handler class.
 * 
 * @author LeonTG77
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	private final Main plugin;
	
	public CommandHandler(Main plugin) {
		this.plugin = plugin;
	}
	
	private List<UHCCommand> cmds = new ArrayList<UHCCommand>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommand(cmd.getName());
		
		if (command == null) { // this shouldn't happen, it only uses registered commands but incase.
			return true;
		}
		
		if (!sender.hasPermission(command.getPermission())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		try {
			if (!command.execute(sender, args)) {
				sender.sendMessage(Main.PREFIX + "Usage: " + command.getUsage());
			}
		} catch (CommandException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage()); // send them the exception message
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + ex.getClass().getName() + ": " + ex.getMessage());
			ex.printStackTrace(); // send them the exception and tell the console the error if its not a command exception
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommand(cmd.getName());

		if (command == null) { // this shouldn't happen, it only uses registered commands but incase.
			return null;
		}
		
		if (!sender.hasPermission(command.getPermission())) {
			return null;
		}
		
		try {
			List<String> list = command.tabComplete(sender, args);
			
			// if the list is null, replace it with everyone online.
			if (list == null) {
				return null;
			}
			
			// I don't want anything done if the list is empty.
			if (list.isEmpty()) {
				return list;
			}
			
			List<String> toReturn = new ArrayList<String>();
			
			if (args[args.length - 1].isEmpty()) {
				for (String type : list) {
					toReturn.add(type);
				}
			} else {
				for (String type : list) {
					if (type.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
						toReturn.add(type);
					}
				}
			}
			
			return toReturn;
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + ex.getClass().getName() + ": " + ex.getMessage());
			ex.printStackTrace(); 
		}
		return null;
	}

	/**
     * Get a uhc command.
     *
     * @param name The name of the uhc command
     * @return The UHCCommand if found, null otherwise.
     */
    protected UHCCommand getCommand(String name) {
        for (UHCCommand cmd : cmds) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        
        return null;
    }
	
	/**
	 * Register all the commands.
	 */
	public void registerCommands(Game game, Data data, Arena arena, Parkour parkour, Settings settings, GUIManager gui, BoardManager board, SpecManager spec, FeatureManager feat, ScenarioManager scen, WorldManager manager, Timer timer, TeamManager teams, FireworkManager firework, ScatterManager scatter, UBL ubl, AntiStripmine antiSM) {
		// arena
		cmds.add(new ArenaCommand(arena, game, parkour, spec, board));
		cmds.add(new HotbarCommand(plugin, arena));
		
		// basic
		cmds.add(new BroadcastCommand());
		cmds.add(new ButcherCommand(game));
		cmds.add(new CombatLogCommand(feat.getFeature(CombatLogFeature.class)));
		cmds.add(new EditCommand());
		cmds.add(new FireCommand(firework));
		cmds.add(new IgnoreCommand(plugin));
		cmds.add(new ListCommand(plugin, game));
		cmds.add(new ParkourCommand(plugin, settings, parkour));
		cmds.add(new SetspawnCommand(settings));
		cmds.add(new SkullCommand());
		cmds.add(new StaffChatCommand());
		cmds.add(new TextCommand());
		
		// game
		cmds.add(new BoardCommand(arena, game, board));
		cmds.add(new ChatCommand(game));
		cmds.add(new ConfigCommand(plugin, game, gui, feat, scen));
		cmds.add(new EndCommand(plugin, data, timer, settings, game, feat, scen, board, teams, spec, gui, firework, manager));
		cmds.add(new HelpopCommand(plugin, spec));
		cmds.add(new MatchpostCommand(game));
		cmds.add(new MUCoordsCommand(game, timer));
		cmds.add(new RespawnCommand(plugin));
		cmds.add(new ScenarioCommand(plugin, scen));
		cmds.add(new ScatterCommand(game, settings, spec, scatter, teams, parkour, arena));
		cmds.add(new StartCommand(game, timer));
		cmds.add(new TimeLeftCommand(game, timer));
		cmds.add(new TimerCommand(plugin));
		cmds.add(new VoteCommand(plugin));
		cmds.add(new WhitelistCommand(game));
		
		// give
		cmds.add(new GiveallCommand());
		cmds.add(new GiveCommand());
		
		// inventory
		cmds.add(new HOFCommand(plugin, game, settings, gui));
		cmds.add(new GameInfoCommand(gui));
		
		// lag
		cmds.add(new MsCommand(plugin));
		cmds.add(new TpsCommand(plugin));
		
		// msg
		cmds.add(new MsgCommand(plugin, scen));
		cmds.add(new ReplyCommand(plugin, scen));
		
		// player
		cmds.add(new ClearInvCommand(plugin));
		cmds.add(new ClearXPCommand(plugin));
		cmds.add(new FeedCommand(plugin));
		cmds.add(new FlyCommand());
		cmds.add(new GamemodeCommand(spec));
		cmds.add(new HealCommand(plugin));
		cmds.add(new HealthCommand(scen));
		cmds.add(new SethealthCommand());
		cmds.add(new SetmaxhealthCommand());
		
		// punish
		cmds.add(new BanCommand(plugin, board));
		cmds.add(new BanIPCommand(board));
		cmds.add(new DQCommand(plugin, board));
		cmds.add(new KickCommand(plugin));
		cmds.add(new MuteCommand(plugin));
		cmds.add(new TempbanCommand(plugin, board));
		cmds.add(new UnbanCommand(plugin));
		cmds.add(new UnbanIPCommand());

		// spectate
		cmds.add(new BackCommand(plugin, spec));
		GlobalChatCommand globalChatCommand = new GlobalChatCommand(plugin, game, spec);
		cmds.add(globalChatCommand);
		cmds.add(new GoodGameCommand(globalChatCommand));
		cmds.add(new InvseeCommand(gui, spec));
		cmds.add(new NearCommand(spec, teams));
		cmds.add(new SpectateCommand(game, spec));
		cmds.add(new SpectatorChatProtectionCommand(spec));
		cmds.add(new SpecAndStaffChatCommand(spec));
		cmds.add(new SpecChatCommand(spec));
		cmds.add(new SpeedCommand(spec));
		cmds.add(new TpCommand(spec));
		
		// team
		cmds.add(new PMCommand(spec, teams));
		cmds.add(new PMMinedOresCommand(game, spec, teams));
		cmds.add(new PMOresCommand(game, spec, teams));
		cmds.add(new RandomCommand(teams));
		cmds.add(new TeamCommand(game, board, teams, scen));
		cmds.add(new TLCommand(game, spec, teams));

		// user
		cmds.add(new InfoCommand(plugin, ubl));
		cmds.add(new InfoIPCommand());
		cmds.add(new RankCommand(plugin));
		cmds.add(new SetFixedScatterLocationCommand(plugin));
		cmds.add(new StatsCommand(plugin, game, gui));
		cmds.add(new TopCommand(game, gui));
		
		// world
		cmds.add(new BorderCommand());
		cmds.add(new PregenCommand());
		cmds.add(new PvPCommand());
		cmds.add(new WorldCommand(game, settings, antiSM, gui, manager));
		
		for (UHCCommand cmd : cmds) {
			PluginCommand pCmd = plugin.getCommand(cmd.getName());
			
			// if its null, broadcast the command name so I know which one it is (so I can fix it).
			if (pCmd == null) {
				PlayerUtils.broadcast(cmd.getName());
				continue;
			}
			
			pCmd.setExecutor(this);
			pCmd.setTabCompleter(this);
		}
	}
}