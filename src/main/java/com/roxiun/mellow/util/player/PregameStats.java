package com.roxiun.mellow.util.player;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import com.roxiun.mellow.api.bedwars.BedwarsPlayer;
import com.roxiun.mellow.cache.PlayerCache;
import com.roxiun.mellow.config.MellowOneConfig;
import com.roxiun.mellow.data.PlayerProfile;
import com.roxiun.mellow.util.ChatUtils;
import com.roxiun.mellow.util.UUIDUtils;
import com.roxiun.mellow.util.blacklist.BlacklistManager;
import com.roxiun.mellow.util.formatting.FormattingUtils;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class PregameStats {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final PlayerCache playerCache;
    private final MellowOneConfig config;
    private final BlacklistManager blacklistManager;

    // runtime state
    private boolean inPregameLobby = false;
    private boolean inBedwars = false;
    private final Set<String> alreadyLookedUp = ConcurrentHashMap.newKeySet();

    // patterns
    private static final Pattern BEDWARS_JOIN_PATTERN = Pattern.compile(
        "^(\\w+) has joined \\((\\d+)/(\\d+)\\)!$"
    );
    private static final Pattern BEDWARS_CHAT_PATTERN = Pattern.compile(
        "^(?:\\[.*?\\]\\s*)*(\\w{3,16})(?::| ») (.*)$"
    );

    public PregameStats(
        PlayerCache playerCache,
        MellowOneConfig config,
        BlacklistManager blacklistManager
    ) {
        this.playerCache = playerCache;
        this.config = config;
        this.blacklistManager = blacklistManager;
    }

    public void onWorldChange() {
        inPregameLobby = false;
        inBedwars = false;
        alreadyLookedUp.clear();
    }

    public void onChat(ClientChatReceivedEvent event) {
        if (!config.pregameStats) return;
        if (!HypixelUtils.INSTANCE.isHypixel()) return;

        if (!inBedwars) {
            inBedwars = isBedwarsSidebar();
            if (!inBedwars) return;
        }

        String raw = event.message.getUnformattedText();
        String message = raw.replaceAll("§.", "").trim(); // idk why the hell it doesn't work w/o this

        Matcher joinMatch = BEDWARS_JOIN_PATTERN.matcher(message);
        if (joinMatch.find()) {
            inPregameLobby = true;
            return;
        }

        if (
            message.contains("Protect your bed and destroy the enemy beds.") &&
            !message.contains(":")
        ) {
            inPregameLobby = false;
            return;
        }

        if (!inPregameLobby) return;

        Matcher chatMatch = BEDWARS_CHAT_PATTERN.matcher(message);
        if (!chatMatch.find()) return;

        String username = chatMatch.group(1);

        //if (username.equalsIgnoreCase(mc.thePlayer.getName())) return;
        if (!alreadyLookedUp.add(username.toLowerCase())) return;

        new Thread(
            () -> handlePlayer(username),
            "Mellow-PregameThread"
        ).start();
    }

    private void handlePlayer(String username) {
        PlayerProfile profile = playerCache.getProfile(username);

        if (profile == null || profile.getBedwarsPlayer() == null) {
            if (config.pregameStats) {
                mc.addScheduledTask(() ->
                    ChatUtils.sendMessage(
                        "§cFailed to fetch stats for: §r" +
                            username +
                            " (possibly nicked)"
                    )
                );
            }
            return;
        }

        UUID uuid = UUIDUtils.fromString(profile.getUuid());
        if (blacklistManager.isBlacklisted(uuid)) {
            String reason = blacklistManager
                .getBlacklistedPlayer(uuid)
                .getReason();
            mc.addScheduledTask(() -> {
                ChatUtils.sendMessage(
                    "§c" + username + " is on your blacklist: " + reason
                );
                // Play pling sound when blacklisted player talks
                mc.thePlayer.playSound("note.pling", 1.0F, 1.0F);
            });
        }

        if (config.pregameStats) {
            BedwarsPlayer player = profile.getBedwarsPlayer();
            String stats =
                player.getName() +
                " §r" +
                player.getStars() +
                " §7|§r FKDR: " +
                player.getFkdrColor() +
                player.getFormattedFkdr();
            mc.addScheduledTask(() -> ChatUtils.sendMessage(stats));
        }

        if (config.urchin && profile.isUrchinTagged()) {
            String tags = FormattingUtils.formatUrchinTags(
                profile.getUrchinTags()
            );
            String urchinMessage =
                "§c" + username + " is tagged on §5Urchin§c for: " + tags;
            mc.addScheduledTask(() -> ChatUtils.sendMessage(urchinMessage));
        }

        if (config.seraph && profile.isSeraphTagged()) {
            String formattedTags = FormattingUtils.formatSeraphTags(
                profile.getSeraphTags()
            );
            // Split the formatted tags by the newline separator and send as separate messages
            String[] tagMessages = formattedTags.split("\n§c");
            if (tagMessages.length > 0 && !tagMessages[0].trim().isEmpty()) {
                // Send the first tag with the main message
                String firstMessage =
                    "§c" +
                    username +
                    " is tagged on §3Seraph§c for: " +
                    tagMessages[0];
                mc.addScheduledTask(() -> ChatUtils.sendMessage(firstMessage));
                // Send additional tags as separate messages
                for (int i = 1; i < tagMessages.length; i++) {
                    if (!tagMessages[i].trim().isEmpty()) {
                        String additionalMessage = "§c" + tagMessages[i];
                        mc.addScheduledTask(() ->
                            ChatUtils.sendMessage(additionalMessage)
                        );
                    }
                }
            }
        }
    }

    private boolean isBedwarsSidebar() {
        Scoreboard board = mc.theWorld.getScoreboard();
        if (board == null) return false;

        ScoreObjective obj = board.getObjectiveInDisplaySlot(1);
        if (obj == null) return false;

        String name = EnumChatFormatting.getTextWithoutFormattingCodes(
            obj.getDisplayName()
        );
        return name.contains("BED WARS");
    }
}
