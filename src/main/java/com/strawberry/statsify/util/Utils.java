package com.strawberry.statsify.util;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class Utils {

    public static String getUUIDFromPlayerName(String playerName) {
        if (
            Minecraft.getMinecraft().getNetHandler() == null ||
            Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap() == null
        ) {
            return null;
        }
        Collection<NetworkPlayerInfo> playerInfoMap = Minecraft.getMinecraft()
            .getNetHandler()
            .getPlayerInfoMap();
        for (NetworkPlayerInfo networkPlayerInfo : playerInfoMap) {
            if (
                networkPlayerInfo
                    .getGameProfile()
                    .getName()
                    .equalsIgnoreCase(playerName)
            ) {
                return networkPlayerInfo
                    .getGameProfile()
                    .getId()
                    .toString()
                    .replace("-", "");
            }
        }
        return null; // Player not found in tab list
    }

    public static String formatWinstreak(String text) {
        String color = "§r";
        int Winstreak = Integer.parseInt(text);
        if (Winstreak >= 5 && Winstreak < 10) {
            color = "§b";
        }
        if (Winstreak >= 10 && Winstreak < 20) {
            color = "§6";
        }
        if (Winstreak >= 20) {
            color = "§4";
        }
        return color + text;
    }

    public static String formatStars(String text) {
        String color = "§7";

        int Stars = Integer.parseInt(text);
        if (Stars < 100) {
            color = "§7";
            return color + text + "\u272b";
        }

        if (Stars >= 100 && Stars < 200) {
            color = "§f";
            return color + text + "\u272b";
        }
        if (Stars >= 200 && Stars < 300) {
            color = "§6";
            return color + text + "\u272b";
        }
        if (Stars >= 300 && Stars < 400) {
            color = "§b";
            return color + text + "\u272b";
        }
        if (Stars >= 400 && Stars < 500) {
            color = "§2";
            return color + text + "\u272b";
        }
        if (Stars >= 500 && Stars < 600) {
            color = "§3";
            return color + text + "\u272b";
        }
        if (Stars >= 600 && Stars < 700) {
            color = "§4";
            return color + text + "\u272b";
        }
        if (Stars >= 700 && Stars < 800) {
            color = "§d";
            return color + text + "\u272b";
        }
        if (Stars >= 800 && Stars < 900) {
            color = "§9";
            return color + text + "\u272b";
        }
        if (Stars >= 900 && Stars < 1000) {
            color = "§5";
            return color + text + "\u272b";
        }
        if (Stars >= 1000 && Stars < 1100) {
            String[] digit = text.split("");
            return (
                "§6" +
                digit[0] +
                "§e" +
                digit[1] +
                "§a" +
                digit[2] +
                "§b" +
                digit[3] +
                "§d" +
                "\u272a"
            );
        }
        if (Stars >= 1100 && Stars < 1200) {
            String[] digit = text.split("");
            return (
                "§f" + digit[0] + digit[1] + digit[2] + digit[3] + "\u272a"
            );
        }
        if (Stars >= 1200 && Stars < 1300) {
            String[] digit = text.split("");
            return (
                "§e" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§6" +
                "\u272a"
            );
        }
        if (Stars >= 1300 && Stars < 1400) {
            String[] digit = text.split("");
            return (
                "§b" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§3" +
                "\u272a"
            );
        }
        if (Stars >= 1400 && Stars < 1500) {
            String[] digit = text.split("");
            return (
                "§a" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§2" +
                "\u272a"
            );
        }
        if (Stars >= 1500 && Stars < 1600) {
            String[] digit = text.split("");
            return (
                "§3" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§9" +
                "\u272a"
            );
        }
        if (Stars >= 1600 && Stars < 1700) {
            String[] digit = text.split("");
            return (
                "§c" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§4" +
                "\u272a"
            );
        }
        if (Stars >= 1700 && Stars < 1800) {
            String[] digit = text.split("");
            return (
                "§d" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§5" +
                "\u272a"
            );
        }
        if (Stars >= 1800 && Stars < 1900) {
            String[] digit = text.split("");
            return (
                "§9" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§1" +
                "\u272a"
            );
        }
        if (Stars >= 1900 && Stars < 2000) {
            String[] digit = text.split("");
            return (
                "§5" +
                digit[0] +
                digit[1] +
                digit[2] +
                digit[3] +
                "§8" +
                "\u272a"
            );
        }
        if (Stars >= 2000 && Stars < 2100) {
            String[] digit = text.split("");
            return (
                "§7" +
                digit[0] +
                "§f" +
                digit[1] +
                digit[2] +
                "§7" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2100 && Stars < 2200) {
            String[] digit = text.split("");
            return (
                "§f" +
                digit[0] +
                "§e" +
                digit[1] +
                digit[2] +
                "§6" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2200 && Stars < 2300) {
            String[] digit = text.split("");
            return (
                "§6" +
                digit[0] +
                "§f" +
                digit[1] +
                digit[2] +
                "§b" +
                digit[3] +
                "§3" +
                "\u269d"
            );
        }
        if (Stars >= 2300 && Stars < 2400) {
            String[] digit = text.split("");
            return (
                "§5" +
                digit[0] +
                "§d" +
                digit[1] +
                digit[2] +
                "§6" +
                digit[3] +
                "§e" +
                "\u269d"
            );
        }
        if (Stars >= 2400 && Stars < 2500) {
            String[] digit = text.split("");
            return (
                "§b" +
                digit[0] +
                "§f" +
                digit[1] +
                digit[2] +
                "§7" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2500 && Stars < 2600) {
            String[] digit = text.split("");
            return (
                "§f" +
                digit[0] +
                "§a" +
                digit[1] +
                digit[2] +
                "§2" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2600 && Stars < 2700) {
            String[] digit = text.split("");
            return (
                "§4" +
                digit[0] +
                "§c" +
                digit[1] +
                digit[2] +
                "§d" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2700 && Stars < 2800) {
            String[] digit = text.split("");
            return (
                "§e" +
                digit[0] +
                "§f" +
                digit[1] +
                digit[2] +
                "§8" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2800 && Stars < 2900) {
            String[] digit = text.split("");
            return (
                "§a" +
                digit[0] +
                "§2" +
                digit[1] +
                digit[2] +
                "§6" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 2900 && Stars < 3000) {
            String[] digit = text.split("");
            return (
                "§b" +
                digit[0] +
                "§3" +
                digit[1] +
                digit[2] +
                "§9" +
                digit[3] +
                "\u269d"
            );
        }
        if (Stars >= 3000) {
            String[] digit = text.split("");
            return (
                "§e" +
                digit[0] +
                "§6" +
                digit[1] +
                digit[2] +
                "§c" +
                digit[3] +
                "\u269d"
            );
        }
        return "NaN";
    }

    public static String formatRank(String rank) {
        return rank
            .replace("[VIP", "§a[VIP")
            .replace("[MVP+", "§b[MVP+")
            .replace("[MVP++", "§6[MVP++");
    }

    public static String parseUsername(String str) {
        str = str.trim();
        String[] words = str.split("\\s+");
        return words.length > 0 ? words[words.length - 1] : "";
    }

    public static String getTabDisplayName(String playerName) {
        ScorePlayerTeam playerTeam = Minecraft.getMinecraft()
            .theWorld.getScoreboard()
            .getPlayersTeam(playerName);
        if (playerTeam == null) {
            return playerName;
        }

        int length = playerTeam.getColorPrefix().length();
        if (length == 10) {
            return (
                playerTeam.getColorPrefix() +
                playerName +
                playerTeam.getColorSuffix()
            );
        }
        if (length == 8) {
            return playerTeam.getColorPrefix() + playerName;
        }
        return playerName;
    }

    public static String[] getTabDisplayName2(String playerName) {
        ScorePlayerTeam playerTeam = Minecraft.getMinecraft()
            .theWorld.getScoreboard()
            .getPlayersTeam(playerName);
        if (playerTeam == null) {
            return new String[] { "", playerName, "" };
        }
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) {
            String val[] = new String[3];
            val[0] = playerTeam.getColorPrefix();
            val[1] = playerName;
            val[2] = playerTeam.getColorSuffix();
            return val;
        }
        if (length == 8) {
            String val[] = new String[3];
            val[0] = playerTeam.getColorPrefix();
            val[1] = playerName;
            val[2] = "";
            return val;
        }
        return new String[] { "", playerName, "" };
    }

    public static int countOccurrences(String str, String subStr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(subStr, idx)) != -1) {
            count++;
            idx += subStr.length();
        }
        return count;
    }

    public static String extractValue(
        String text,
        String startDelimiter,
        String endDelimiter
    ) {
        int startIndex = text.indexOf(startDelimiter);
        if (startIndex == -1) return "N/A"; // Not found
        startIndex += startDelimiter.length();
        int endIndex = text.indexOf(endDelimiter, startIndex);
        if (endIndex == -1) return "N/A"; // Not found
        return text.substring(startIndex, endIndex).trim();
    }
}
