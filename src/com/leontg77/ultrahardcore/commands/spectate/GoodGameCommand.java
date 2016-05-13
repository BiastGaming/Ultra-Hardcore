package com.leontg77.ultrahardcore.commands.spectate;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

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
