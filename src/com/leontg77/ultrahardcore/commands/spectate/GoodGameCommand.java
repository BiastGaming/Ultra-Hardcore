package com.leontg77.ultrahardcore.commands.spectate;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;

public class GoodGameCommand extends UHCCommand {

    protected final GlobalChatCommand globalChatCommand;

    public GoodGameCommand(GlobalChatCommand globalChatCommand) {
        super("gg", "");
        this.globalChatCommand = globalChatCommand;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        return globalChatCommand.execute(sender, new String[]{"gg"});
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
