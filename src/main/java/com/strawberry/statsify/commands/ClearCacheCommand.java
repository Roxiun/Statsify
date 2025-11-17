package com.strawberry.statsify.commands;

import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ClearCacheCommand extends CommandBase {

    private final Map<String, List<String>> playerSuffixes;

    public ClearCacheCommand(Map<String, List<String>> playerSuffixes) {
        this.playerSuffixes = playerSuffixes;
    }

    @Override
    public String getCommandName() {
        return "cleartabcache";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cleartabcache";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
            new ChatComponentText("§r[§bF§r] §aTab cache has been wiped")
        );
        playerSuffixes.clear();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
