package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.EntityUtils;

/**
 * Butcher command class.
 * 
 * @author LeonTG77
 */
public class ButcherCommand extends UHCCommand {
    private final Game game;

    public ButcherCommand(Game game) {
        super("butcher", "[mob]");

        this.game = game;
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        int amount = 0;

        if (args.length == 0) {
            for (World world : game.getWorlds()) {
                for (Entity mob : world.getEntities()) {
                    EntityType type = mob.getType();

                    if (type == EntityType.DROPPED_ITEM || type == EntityType.EXPERIENCE_ORB) {
                        continue;
                    }

                    if (!EntityUtils.isButcherable(type)) {
                        continue;
                    }

                    mob.remove();
                    amount++;
                }
               }

            sender.sendMessage(Main.PREFIX + "Killed §6" + amount + " §7entities.");
            return true;
        }

        EntityType type;

        try {
            type = EntityType.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            throw new CommandException("'" + args[0] + "' is not a valid entity type.");
        }

        Entity entity = null;

        for (World world : Bukkit.getWorlds()) {
            for (Entity mob : world.getEntities()) {
                if (!mob.getType().equals(type)) {
                    continue;
                }

                entity = mob;
                mob.remove();
                amount++;
            }
           }

        if (amount < 1 || entity == null) {
            throw new CommandException("There were no mobs to kill.");
        }

        sender.sendMessage(Main.PREFIX + "Killed §6" + amount + " §7" + EntityUtils.getMobName(entity).toLowerCase() + "s.");
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (EntityType type : EntityType.values()) {
                toReturn.add(type.name().toLowerCase());
            }
        }

        return toReturn;
    }
}