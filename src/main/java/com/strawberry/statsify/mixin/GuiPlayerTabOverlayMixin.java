package com.strawberry.statsify.mixin;

import com.strawberry.statsify.Statsify;
import com.strawberry.statsify.api.urchin.UrchinTag;
import com.strawberry.statsify.data.TabStats;
import com.strawberry.statsify.util.player.PlayerUtils;
import java.util.Arrays;
import java.util.List;
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
    private static final List<String> RED_URCHIN_TAGS = Arrays.asList(
        "sniper",
        "blatant cheater",
        "closet cheater",
        "confirmed cheater"
    );

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(
        NetworkPlayerInfo networkPlayerInfoIn,
        CallbackInfoReturnable<String> cir
    ) {
        if (Statsify.config == null || !Statsify.config.tabStats) {
            return;
        }

        String playerName = networkPlayerInfoIn.getGameProfile().getName();
        if (playerName == null) {
            return;
        }

        TabStats stats = Statsify.tabStats.get(playerName);
        boolean isNicked = Statsify.nickUtils.isNicked(playerName);
        String originalDisplayName = getOriginalDisplayName(
            networkPlayerInfoIn
        );
        UUID playerUUID = networkPlayerInfoIn.getGameProfile().getId();

        if (stats != null) {
            handlePlayerWithStats(
                playerName,
                stats,
                originalDisplayName,
                playerUUID,
                cir
            );
        } else if (isNicked && !originalDisplayName.contains("§8[§5NICK§8]")) {
            handleNickedPlayer(
                playerName,
                originalDisplayName,
                playerUUID,
                cir
            );
        } else {
            // Handle players who are not nicked and have no stats (e.g. in lobby)
            String newDisplayName = originalDisplayName;
            if (
                playerUUID != null &&
                Statsify.blacklistManager.isBlacklisted(playerUUID)
            ) {
                newDisplayName += " §c[BL]";
            }
            if (!originalDisplayName.equals(newDisplayName)) {
                cir.setReturnValue(newDisplayName);
            }
        }
    }

    private void handlePlayerWithStats(
        String playerName,
        TabStats stats,
        String originalDisplayName,
        UUID playerUUID,
        CallbackInfoReturnable<String> cir
    ) {
        String[] tabData = PlayerUtils.getTabDisplayName2(playerName);
        if (tabData == null || tabData.length < 2) {
            return;
        }
        String team = tabData[0];
        String name = tabData[1];

        String teamColor = team.length() >= 2 ? team.substring(0, 2) : "";
        String newDisplayName = formatDisplayNameWithStats(
            team,
            name,
            teamColor,
            stats,
            playerUUID
        );

        if (!originalDisplayName.equals(newDisplayName)) {
            cir.setReturnValue(newDisplayName);
        }
    }

    private String formatDisplayNameWithStats(
        String team,
        String name,
        String teamColor,
        TabStats stats,
        UUID playerUUID
    ) {
        String newDisplayName;
        String stars = stats.getStars();
        String fkdr = stats.getFkdr();
        String winstreak = stats.getWinstreak();

        switch (Statsify.config.tabFormat) {
            case 1: // [Star] Name · FKDR
                newDisplayName =
                    team +
                    stars +
                    " " +
                    teamColor +
                    name +
                    MIDDLE_DOT +
                    "§r" +
                    fkdr;
                break;
            case 2: // Name · FKDR
                newDisplayName =
                    team + teamColor + name + MIDDLE_DOT + "§r" + fkdr;
                break;
            case 0: // [Star] Name · FKDR
            default:
                newDisplayName =
                    team +
                    "§7[" +
                    stars +
                    "§7] " +
                    teamColor +
                    name +
                    MIDDLE_DOT +
                    "§r" +
                    fkdr;
                break;
        }

        if (winstreak != null && !winstreak.isEmpty()) {
            newDisplayName += MIDDLE_DOT + "§r" + winstreak;
        }

        if (stats.isUrchinTagged()) {
            boolean isRed = false;
            for (UrchinTag tag : stats.getUrchinTags()) {
                if (RED_URCHIN_TAGS.contains(tag.getType().toLowerCase())) {
                    isRed = true;
                    break;
                }
            }
            newDisplayName += (isRed ? " §c⚠" : " §e⚠");
        }

        if (
            playerUUID != null &&
            Statsify.blacklistManager.isBlacklisted(playerUUID)
        ) {
            newDisplayName += " §c[BL]";
        }

        return newDisplayName;
    }

    private void handleNickedPlayer(
        String playerName,
        String originalDisplayName,
        UUID playerUUID,
        CallbackInfoReturnable<String> cir
    ) {
        String[] tabData = PlayerUtils.getTabDisplayName2(playerName);
        String newDisplayName;
        if (tabData != null && tabData.length >= 3) {
            String team = tabData[0] != null ? tabData[0] : "";
            String name = tabData[1] != null ? tabData[1] : "";
            String suffix = tabData[2] != null ? tabData[2] : "";
            String teamColor = team.length() >= 2 ? team.substring(0, 2) : "";
            newDisplayName = team + "§8[§5NICK§8] " + teamColor + name + suffix;
        } else {
            newDisplayName = "§8[§5NICK§8] " + originalDisplayName;
        }

        if (
            playerUUID != null &&
            Statsify.blacklistManager.isBlacklisted(playerUUID)
        ) {
            newDisplayName += " §c[BL]";
        }

        cir.setReturnValue(newDisplayName);
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
