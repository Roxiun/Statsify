package com.strawberry.statsify.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class StatsifyCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "st";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/st";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
            new ChatComponentText("§r§b§lstat§9§tsi§3§lfy§r")
        );
        sender.addChatMessage(
            new ChatComponentText(
                "§r§boriginal made by§e melissalmao§r updated by§e roxiun§r"
            )
        );
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(
            new ChatComponentText(
                "§r§3/bw <username>:§b Manually check bedwars stats of a player.§r"
            )
        );
        sender.addChatMessage(
            new ChatComponentText(
                "§r§3/denick <finals | beds> <number>:§b Manually denick a player based on finals or beds.§r"
            )
        );
        sender.addChatMessage(
            new ChatComponentText(
                "§r§3/cleartabcache:§b Clear stats cache of players if you're having issues.§r"
            )
        );
        sender.addChatMessage(
            new ChatComponentText(
                "§r§3/who:§b Check and print the stats of the players in your lobby.§r"
            )
        );
        sender.addChatMessage(new ChatComponentText(""));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
