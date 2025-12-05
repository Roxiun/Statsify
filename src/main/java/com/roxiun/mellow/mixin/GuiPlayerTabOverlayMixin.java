package com.roxiun.mellow.mixin;

import com.roxiun.mellow.Mellow;
import com.roxiun.mellow.api.seraph.SeraphTag;
import com.roxiun.mellow.api.urchin.UrchinTag;
import com.roxiun.mellow.data.TabStats;
import com.roxiun.mellow.util.formatting.FormattingUtils;
import com.roxiun.mellow.util.player.PlayerUtils;
import java.util.UUID;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin {

    private static final String MIDDLE_DOT = "\u30fb";

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(
        NetworkPlayerInfo networkPlayerInfoIn,
        CallbackInfoReturnable<String> cir
    ) {
        if (Mellow.config == null || !Mellow.config.tabStats) {
            return;
        }

        String playerName = networkPlayerInfoIn.getGameProfile().getName();
        if (playerName == null) {
            return;
        }

        TabStats stats = Mellow.tabStats.get(playerName);
        boolean isNicked = Mellow.nickUtils.isNicked(playerName);
        String originalDisplayName = getOriginalDisplayName(
            networkPlayerInfoIn
        );
        UUID playerUUID = networkPlayerInfoIn.getGameProfile().getId();

        String newDisplayName;

        if (stats != null) {
            newDisplayName = handlePlayerWithStats(
                playerName,
                stats,
                playerUUID
            );
        } else if (isNicked && !originalDisplayName.contains("§8[§5NICK§8]")) {
            // For nicks without stats, still handle them within the dynamic system
            String[] tabData = PlayerUtils.getTabDisplayName2(playerName);
            if (tabData != null && tabData.length >= 2) {
                String team = tabData[0];
                String name = tabData[1];
                String teamColor = team.length() >= 2
                    ? team.substring(0, 2)
                    : "";

                // Create a minimal TabStats object for the nick case
                TabStats emptyStats = new TabStats(
                    null, // urchinTags
                    null, // seraphTags
                    null, // stars
                    null, // fkdr
                    null, // winstreak
                    null, // wlr
                    null, // bblr
                    null, // wins
                    null, // beds
                    null // finals
                );

                newDisplayName = formatDisplayNameWithStats(
                    team,
                    name,
                    teamColor,
                    emptyStats
                );
            } else {
                // Fallback: create a basic tab structure from network info
                String team = ScorePlayerTeam.formatPlayerName(
                    networkPlayerInfoIn.getPlayerTeam(),
                    playerName
                );
                String name = playerName;
                String teamColor = team.length() >= 2
                    ? team.substring(0, 2)
                    : "";

                // Create a minimal TabStats object for the nick case
                TabStats emptyStats = new TabStats(
                    null, // urchinTags
                    null, // seraphTags
                    null, // stars
                    null, // fkdr
                    null, // winstreak
                    null, // wlr
                    null, // bblr
                    null, // wins
                    null, // beds
                    null // finals
                );

                newDisplayName = formatDisplayNameWithStats(
                    team,
                    name,
                    teamColor,
                    emptyStats
                );
            }
        } else {
            newDisplayName = originalDisplayName;
        }

        newDisplayName = appendBlacklistTag(newDisplayName, playerUUID);

        if (!originalDisplayName.equals(newDisplayName)) {
            cir.setReturnValue(newDisplayName);
        }
    }

    private String handlePlayerWithStats(
        String playerName,
        TabStats stats,
        UUID playerUUID
    ) {
        String[] tabData = PlayerUtils.getTabDisplayName2(playerName);
        if (tabData == null || tabData.length < 2) {
            return "";
        }
        String team = tabData[0];
        String name = tabData[1];

        String teamColor = team.length() >= 2 ? team.substring(0, 2) : "";
        return formatDisplayNameWithStats(team, name, teamColor, stats);
    }

    private String formatDisplayNameWithStats(
        String team,
        String name,
        String teamColor,
        TabStats stats
    ) {
        String newDisplayName = buildOrderedStatsString(
            team,
            name,
            teamColor,
            stats
        );

        if (Mellow.config.showUrchinTagsInTab && stats.isUrchinTagged()) {
            for (UrchinTag tag : stats.getUrchinTags()) {
                newDisplayName +=
                    " " + FormattingUtils.formatUrchinTagIcon(tag);
            }
        }

        if (Mellow.config.showSeraphTagsInTab && stats.isSeraphTagged()) {
            for (SeraphTag tag : stats.getSeraphTags()) {
                newDisplayName +=
                    " " + FormattingUtils.formatSeraphTagIcon(tag);
            }
        }

        return newDisplayName;
    }

    private String buildOrderedStatsString(
        String team,
        String name,
        String teamColor,
        TabStats stats
    ) {
        StringBuilder result = new StringBuilder();
        String stars = stats.getStars();
        String fkdr = stats.getFkdr();

        // Dynamic ordering based on user configuration
        result.append(buildDynamicOrderedString(team, name, teamColor, stats));

        return result.toString();
    }

    private String buildDynamicOrderedString(
        String team,
        String name,
        String teamColor,
        TabStats stats
    ) {
        // Collect all valid stat parts with their type information
        java.util.List<
            java.util.Map.Entry<String, Integer>
        > validPartsWithType = new java.util.ArrayList<>();

        // Process each stat in the configured order with type tracking
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat1,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat2,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat3,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat4,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat5,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat6,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat7,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat8,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat9,
            team,
            name,
            teamColor,
            stats
        );
        addValidPartWithConfigStat(
            validPartsWithType,
            Mellow.config.customStat10,
            team,
            name,
            teamColor,
            stats
        );

        // Build the string with configurable dot separators between positions
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < validPartsWithType.size(); i++) {
            if (i > 0) {
                // Determine which separator to use based on the position and previous element type
                boolean previousIsTeam =
                    validPartsWithType.get(i - 1).getValue() == 0; // Team type is 0

                if (i == 1) {
                    // Between 1st and 2nd (index 0 and 1)
                    if (Mellow.config.showDot12) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 2) {
                    // Between 2nd and 3rd (index 1 and 2)
                    if (Mellow.config.showDot23) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 3) {
                    // Between 3rd and 4th (index 2 and 3)
                    if (Mellow.config.showDot34) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 4) {
                    // Between 4th and 5th (index 3 and 4)
                    if (Mellow.config.showDot45) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 5) {
                    // Between 5th and 6th (index 4 and 5)
                    if (Mellow.config.showDot56) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 6) {
                    // Between 6th and 7th (index 5 and 6)
                    if (Mellow.config.showDot67) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 7) {
                    // Between 7th and 8th (index 6 and 7)
                    if (Mellow.config.showDot78) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 8) {
                    // Between 8th and 9th (index 7 and 8)
                    if (Mellow.config.showDot89) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                } else if (i == 9) {
                    // Between 9th and 10th (index 8 and 9)
                    if (Mellow.config.showDot910) {
                        result.append(MIDDLE_DOT).append("§r");
                    } else if (!previousIsTeam) {
                        result.append(" ");
                    }
                    // If previous is team, don't add any separator since team already has internal spacing
                }
            }
            result.append(validPartsWithType.get(i).getKey());
        }

        return result.toString();
    }

    private void addValidPartWithConfigStat(
        java.util.List<java.util.Map.Entry<String, Integer>> validPartsWithType,
        int statIndex,
        String team,
        String name,
        String teamColor,
        TabStats stats
    ) {
        String[] statParts = processDynamicStat(
            statIndex,
            team,
            name,
            teamColor,
            stats
        );
        if (statParts != null && !statParts[0].trim().isEmpty()) {
            // Create an entry with the stat value and its type (statIndex)
            validPartsWithType.add(
                new java.util.AbstractMap.SimpleEntry<>(statParts[0], statIndex)
            );
        }
    }

    private String[] processDynamicStat(
        int statIndex,
        String team,
        String name,
        String teamColor,
        TabStats stats
    ) {
        String stars = stats.getStars();
        String fkdr = stats.getFkdr();

        switch (statIndex) {
            case 0: // Team
                return new String[] { team, "false" };
            case 1: // Stars (shows Nick instead if player is nicks)
                boolean isNicked = Mellow.nickUtils.isNicked(name);
                if (isNicked) {
                    if (Mellow.config.showNickWithBrackets) {
                        return new String[] { "§5[§lNICK§r§5]", "false" };
                    } else {
                        return new String[] { "§5§lNICK", "false" };
                    }
                } else if (stars != null && !stars.isEmpty()) {
                    if (Mellow.config.showStarsWithBrackets) {
                        return new String[] { "§7[" + stars + "§7]", "false" };
                    } else {
                        return new String[] { stars, "false" };
                    }
                }
                break;
            case 2: // Name
                return new String[] { teamColor + name, "false" };
            case 3: // FKDR
                if (fkdr != null && !fkdr.isEmpty()) {
                    return new String[] { fkdr, "false" };
                }
                break;
            case 4: // Winstreak
                if (
                    stats.getWinstreak() != null &&
                    !stats.getWinstreak().isEmpty()
                ) {
                    return new String[] { stats.getWinstreak(), "false" };
                }
                break;
            case 5: // WLR
                if (stats.getWlr() != null && !stats.getWlr().isEmpty()) {
                    return new String[] { stats.getWlr(), "false" }; // The color is already included in the string
                }
                break;
            case 6: // BBLR
                if (stats.getBblr() != null && !stats.getBblr().isEmpty()) {
                    return new String[] { stats.getBblr(), "false" }; // The color is already included in the string (if implemented)
                }
                break;
            case 7: // Wins
                if (stats.getWins() != null && !stats.getWins().isEmpty()) {
                    return new String[] { stats.getWins(), "false" }; // The color is already included in the string
                }
                break;
            case 8: // Beds
                if (stats.getBeds() != null && !stats.getBeds().isEmpty()) {
                    return new String[] { stats.getBeds(), "false" }; // The color is already included in the string
                }
                break;
            case 9: // Finals
                if (stats.getFinals() != null && !stats.getFinals().isEmpty()) {
                    return new String[] { stats.getFinals(), "false" }; // The color is already included in the string
                }
                break;
            case 10: // None
                return null;
        }
        return null;
    }

    private String appendBlacklistTag(String displayName, UUID playerUUID) {
        if (
            playerUUID != null &&
            Mellow.blacklistManager.isBlacklisted(playerUUID)
        ) {
            return displayName + " §8[§4LIST§8]";
        }
        return displayName;
    }

    private String getOriginalDisplayName(
        NetworkPlayerInfo networkPlayerInfoIn
    ) {
        if (networkPlayerInfoIn.getDisplayName() != null) {
            return networkPlayerInfoIn.getDisplayName().getFormattedText();
        }
        return ScorePlayerTeam.formatPlayerName(
            networkPlayerInfoIn.getPlayerTeam(),
            networkPlayerInfoIn.getGameProfile().getName()
        );
    }
}
