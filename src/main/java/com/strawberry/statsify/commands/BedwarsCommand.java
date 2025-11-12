package com.strawberry.statsify.commands;

import com.mojang.authlib.GameProfile;
import com.strawberry.statsify.api.NadeshikoApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import java.util.List;

public class BedwarsCommand extends CommandBase {

    private final NadeshikoApi nadeshikoApi;

    public BedwarsCommand() {
        this.nadeshikoApi = new NadeshikoApi();
    }

    @Override
    public String getCommandName() {
        return "bw";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bw <username>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7cInvalid usage!\u00a7r Use /bw \u00a75<username>\u00a7r"));
            return;
        }

        String username = args[0];
        new Thread(() -> {
            try {
                String stats = nadeshikoApi.fetchPlayerStats(username);
                Minecraft.getMinecraft().addScheduledTask(() ->
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats))
                );
            } catch (IOException e) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cFailed to fetch stats for: \u00a7r" + username))
                );
            }
        }).start();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream()
                    .map(NetworkPlayerInfo::getGameProfile)
                    .map(GameProfile::getName)
                    .toArray(String[]::new));
        }
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
