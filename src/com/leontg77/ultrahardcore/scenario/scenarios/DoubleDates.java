package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * DoubleDates scenario class.
 * 
 * @author LeonTG77
 */
public class DoubleDates extends Scenario implements CommandExecutor {

    public DoubleDates(TeamManager teams) {
        super("DoubleDates", "2 and 2 teams are combined together to create a larger team.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }
}