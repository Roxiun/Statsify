package com.roxiun.mellow.commands;

import com.roxiun.mellow.api.mojang.MojangApi;
import com.roxiun.mellow.util.ChatUtils;
import com.roxiun.mellow.util.UUIDUtils;
import com.roxiun.mellow.util.blacklist.BlacklistManager;
import com.roxiun.mellow.util.blacklist.BlacklistedPlayer;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class BlacklistCommand extends CommandBase {

    private final BlacklistManager blacklistManager;
    private final MojangApi mojangApi;

    public BlacklistCommand(
        BlacklistManager blacklistManager,
        MojangApi mojangApi
    ) {
        this.blacklistManager = blacklistManager;
        this.mojangApi = mojangApi;
    }

    @Override
    public String getCommandName() {
        return "blacklist";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("bl");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/blacklist <add | remove | list | sync>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            ChatUtils.sendCommandMessage(
                sender,
                "§cInvalid usage! Use " + getCommandUsage(sender)
            );
            return;
        }

        String subCommand = args[0];

        if ("list".equalsIgnoreCase(subCommand)) {
            Map<UUID, BlacklistedPlayer> blacklist =
                blacklistManager.getBlacklist();
            if (blacklist.isEmpty()) {
                ChatUtils.sendCommandMessage(
                    sender,
                    "§aThe blacklist is empty."
                );
                return;
            }

            // Pagination: default to page 1 if no page number provided
            int page = 1;
            int pageSize = 10; // Show 10 entries per page

            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    ChatUtils.sendCommandMessage(
                        sender,
                        "§cInvalid page number. Using page 1."
                    );
                }
            }

            // Convert the map values to a list for pagination
            List<BlacklistedPlayer> players = new java.util.ArrayList<>(
                blacklist.values()
            );
            int totalPlayers = players.size();
            int totalPages = (int) Math.ceil((double) totalPlayers / pageSize);

            if (page > totalPages) {
                page = totalPages;
                if (totalPages == 0) {
                    ChatUtils.sendCommandMessage(
                        sender,
                        "§aThe blacklist is empty."
                    );
                    return;
                }
            }

            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalPlayers);

            ChatUtils.sendCommandMessage(
                sender,
                "§aBlacklisted players (Page " + page + "/" + totalPages + "):"
            );
            for (int i = startIndex; i < endIndex; i++) {
                BlacklistedPlayer player = players.get(i);
                sender.addChatMessage(
                    new ChatComponentText(
                        "§r- " + player.getName() + ": " + player.getReason()
                    )
                );
            }

            // Show navigation help if there are multiple pages
            if (totalPages > 1) {
                String navigationMessage =
                    "§7Use §f/blacklist list <page>§7 to navigate";
                if (page < totalPages) {
                    navigationMessage +=
                        " (Next: §f/blacklist list " + (page + 1) + "§7)";
                }
                ChatUtils.sendCommandMessage(sender, navigationMessage);
            }
            return;
        } else if ("sync".equalsIgnoreCase(subCommand)) {
            // Handle sync command: /blacklist sync <filename>
            if (args.length < 2) {
                ChatUtils.sendCommandMessage(
                    sender,
                    "§cUsage: /blacklist sync <filename>"
                );
                return;
            }

            String filename = args[1];

            // Construct file path - allow both relative to config/mellow and absolute paths
            File syncFile;
            if (filename.startsWith("/")) {
                // Absolute path
                syncFile = new File(filename);
            } else {
                // Relative to the config/mellow directory
                File configDir = new File(
                    Minecraft.getMinecraft().mcDataDir,
                    "config/mellow"
                );
                syncFile = new File(configDir, filename);
            }

            new Thread(() -> {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ChatUtils.sendCommandMessage(
                        sender,
                        "§eSyncing blacklist with file: " +
                            syncFile.getAbsolutePath()
                    );
                });

                try {
                    int newEntries = blacklistManager.syncWithExternalFile(
                        syncFile
                    );
                    String message;
                    if (newEntries > 0) {
                        message =
                            "§aSuccessfully synced! Added " +
                            newEntries +
                            " new entries from " +
                            filename;
                    } else if (syncFile.exists()) {
                        message =
                            "§eSync completed! No new entries found in " +
                            filename;
                    } else {
                        message =
                            "§cFile not found: " + syncFile.getAbsolutePath();
                        Minecraft.getMinecraft().addScheduledTask(() ->
                            ChatUtils.sendCommandMessage(sender, message)
                        );
                        return;
                    }

                    Minecraft.getMinecraft().addScheduledTask(() ->
                        ChatUtils.sendCommandMessage(sender, message)
                    );
                } catch (Exception e) {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                        ChatUtils.sendCommandMessage(
                            sender,
                            "§cError syncing with file: " + e.getMessage()
                        )
                    );
                }
            })
                .start();
            return;
        }

        if (args.length < 2) {
            ChatUtils.sendCommandMessage(
                sender,
                "§cInvalid usage! Use " + getCommandUsage(sender)
            );
            return;
        }

        String playerName = args[1];

        new Thread(() -> {
            String uuidString = mojangApi.getUUIDFromName(playerName);
            if (uuidString == null) {
                uuidString = mojangApi.fetchUUID(playerName);
            }

            if (uuidString == null || uuidString.equals("ERROR")) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                    ChatUtils.sendCommandMessage(
                        sender,
                        "§cCould not find player: " + playerName
                    )
                );
                return;
            }

            UUID uuid = UUIDUtils.fromString(uuidString);

            if ("add".equalsIgnoreCase(subCommand)) {
                String reason = "";
                if (args.length < 3) {
                    reason = "(none)";
                } else {
                    reason = String.join(
                        " ",
                        Arrays.copyOfRange(args, 2, args.length)
                    );
                }

                boolean playerAdded = blacklistManager.addPlayer(
                    uuid,
                    playerName,
                    reason
                );
                if (playerAdded) {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                        ChatUtils.sendCommandMessage(
                            sender,
                            "§aAdded " + playerName + " to the blacklist."
                        )
                    );
                } else {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                        ChatUtils.sendCommandMessage(
                            sender,
                            "§c" +
                                playerName +
                                " is already on the blacklist for reason: " +
                                blacklistManager
                                    .getBlacklistedPlayer(uuid)
                                    .getReason()
                        )
                    );
                }
            } else if ("remove".equalsIgnoreCase(subCommand)) {
                blacklistManager.removePlayer(uuid);
                Minecraft.getMinecraft().addScheduledTask(() ->
                    ChatUtils.sendCommandMessage(
                        sender,
                        "§aRemoved " + playerName + " from the blacklist."
                    )
                );
            } else {
                Minecraft.getMinecraft().addScheduledTask(() ->
                    ChatUtils.sendCommandMessage(
                        sender,
                        "§cInvalid subcommand! Use 'add', 'remove', or 'list'."
                    )
                );
            }
        })
            .start();
    }

    @Override
    public List<String> addTabCompletionOptions(
        ICommandSender sender,
        String[] args,
        BlockPos pos
    ) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(
                args,
                "add",
                "remove",
                "list",
                "sync"
            );
        }

        if (args.length == 2 && "list".equalsIgnoreCase(args[0])) {
            // Provide number suggestions for page parameter
            List<String> numbers = new java.util.ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                numbers.add(String.valueOf(i));
            }
            return getListOfStringsMatchingLastWord(
                args,
                numbers.toArray(new String[0])
            );
        }

        // For add/remove commands, provide player name completion
        if (
            args.length == 2 &&
            ("add".equalsIgnoreCase(args[0]) ||
                "remove".equalsIgnoreCase(args[0]))
        ) {
            return null; // Let the game handle player name completion
        }

        // For sync command, return null to allow file name completion (or default behavior)
        if (args.length == 2 && "sync".equalsIgnoreCase(args[0])) {
            return null; // Let the client handle file name completion
        }

        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
