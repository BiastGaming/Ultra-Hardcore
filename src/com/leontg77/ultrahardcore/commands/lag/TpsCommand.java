package com.leontg77.ultrahardcore.commands.lag;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Tps command class.
 * 
 * @author LeonTG77
 */
public class TpsCommand extends UHCCommand {
	private final Main plugin;
	
	public TpsCommand(Main plugin) {
		super("tps", "");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		
		double tps = plugin.getTps();
		long maxRAM = 4096;
		
		long ramUsage = (Runtime.getRuntime().totalMemory() / 1024 / 1024);
		long startTime = runtime.getStartTime();
		
		int entities = 0;
		int chunks = 0;
		
		ChatColor color;
		String status;
		
		if (tps == 20.0) {
            status = "Perfect";
            color = ChatColor.GREEN;
        } else if (tps >= 17 && tps <= 23) {
            status = "All Good";
            color = ChatColor.GREEN;
        } else if (tps >= 14 && tps <= 26) {
            status = "Small Hiccup";
            color = ChatColor.GOLD;
        } else {
            status = "Struggling";
            color = ChatColor.RED;
        }
		
		for (World world : Bukkit.getWorlds()) {
			chunks += world.getLoadedChunks().length;
			entities += world.getEntities().size();
		}
		
		entities -= Bukkit.getOnlinePlayers().size();

		sender.sendMessage(Main.PREFIX + "Server performance:");
		sender.sendMessage("�8�l� �7Current TPS: " + color + tps + " �8(�6" + status + "�8)");
		sender.sendMessage("�8�l� �7Uptime: �a" + DateUtils.formatDateDiff(startTime));
		sender.sendMessage("�8�l� �7RAM Usage: �a" + ramUsage + "/" + maxRAM + " MB");
		sender.sendMessage("�8�l� �7Entities: �a" + entities);
		sender.sendMessage("�8�l� �7Loaded chunks: �a" + chunks);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}