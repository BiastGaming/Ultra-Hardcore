package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.feature.pvp.CombatLogFeature;

/**
 * Setspawn command class.
 * 
 * @author LeonTG77
 */
public class CombatLogCommand extends UHCCommand {
    private final CombatLogFeature ct;

    public CombatLogCommand(CombatLogFeature ct) {
        super("combatlog", "");

        this.ct = ct;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (game.isRecordedRound()) {
            throw new CommandException("Combat Tag is disabled in Recorded Rounds.");
        }
        
        if (game.isPrivateGame()) {
            throw new CommandException("Combat Tag is disabled in Private Games.");
        }
        
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can be in combat.");
        }

        Player player = (Player) sender;
        long combatTicksLeft = ct.getCombatTicksLeft(player);
        
        if (combatTicksLeft > 0) {
            player.sendMessage(Main.PREFIX + "You are still in combat for §a" + (((double) combatTicksLeft) / 20) + " §7seconds, do not log out.");
        } else {
            player.sendMessage(Main.PREFIX + "You are not in combat, you may log out if you want.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}