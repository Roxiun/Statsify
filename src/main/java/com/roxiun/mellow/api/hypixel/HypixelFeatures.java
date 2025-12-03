package com.roxiun.mellow.api.hypixel;

import net.hypixel.data.type.GameType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class HypixelFeatures {

    //
    // thanks yedelo - https://github.com/Yedelo/YedelMod/blob/master/src/main/java/at/yedel/yedelmod/features/major/BedwarsFeatures.java
    private static final HypixelFeatures INSTANCE = new HypixelFeatures();

    private boolean inBedwars;
    private int timeSinceGameStart = 0; // In ticks
    private int secondsSinceGameStart = 0; // In seconds
    private int timeUntilEmerald = -1;
    private int spawnCount = 0;
    private String mode = "";
    private int stage = 1;
    private String emeraldCounterTimeText = "";
    private int tickCounter = 0; // Counter to track ticks for second conversion

    public static HypixelFeatures getInstance() {
        return INSTANCE;
    }

    private HypixelFeatures() {
        HypixelModAPI.getInstance().registerHandler(
            ClientboundLocationPacket.class,
            this::handleLocationPacket
        );
    }

    private void handleLocationPacket(ClientboundLocationPacket packet) {
        boolean wasInBedwars = inBedwars;
        inBedwars =
            packet.getServerType().isPresent() &&
            packet.getServerType().get() == GameType.BEDWARS &&
            !packet.getLobbyName().isPresent();

        // Reset if we just left a Bedwars game
        if (wasInBedwars && !inBedwars) {
            resetEmeraldTimer();
        }
    }

    // Method to be called every tick to update the emerald timer
    public void updateEmeraldTimer() {
        if (!inBedwars) {
            return;
        }

        // Increment the tick counter every tick
        timeSinceGameStart++;

        // Only update once per second (every 20 ticks)
        tickCounter++;
        if (tickCounter % 20 != 0) {
            // Only update the emerald timer logic once per second
            return;
        }

        if (secondsSinceGameStart == 1) {
            stage = 1;
            timeUntilEmerald = 30;
        } else if (secondsSinceGameStart == 36) {
            // 36 seconds = 720 ticks
            stage = 2;
            timeUntilEmerald = 0;
        } else if (secondsSinceGameStart == 72) {
            // 72 seconds = 1440 ticks
            stage = 3;
            timeUntilEmerald = 0;
        }

        if (timeUntilEmerald == 0) {
            spawnCount++;
            if (stage == 1) {
                if ("doubles".equals(mode)) {
                    timeUntilEmerald = 65;
                } else if ("fours".equals(mode)) {
                    timeUntilEmerald = 55;
                }
            } else if (stage == 2) {
                if ("doubles".equals(mode)) {
                    timeUntilEmerald = 50;
                } else if ("fours".equals(mode)) {
                    timeUntilEmerald = 40;
                }
            } else if (stage == 3) {
                if ("doubles".equals(mode)) {
                    timeUntilEmerald = 35;
                } else if ("fours".equals(mode)) {
                    timeUntilEmerald = 27;
                }
            }
        }

        // Update the text for display
        emeraldCounterTimeText = getFormattedEmeraldCountText();

        // Only decrease the timer if it's greater than 0 to avoid negative values
        if (timeUntilEmerald > 0) {
            timeUntilEmerald--;
        }

        // Increment seconds counter after all processing, matching JS behavior
        secondsSinceGameStart++;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void resetEmeraldTimer() {
        timeSinceGameStart = 0;
        secondsSinceGameStart = 0;
        timeUntilEmerald = -1;
        spawnCount = 0;
        mode = "";
        stage = 1;
        emeraldCounterTimeText = "";
        tickCounter = 0;
    }

    public void startNewGame() {
        resetEmeraldTimer();
        inBedwars = true;
        // Since the ChatHandler waits 1 second before calling this (like in JS version),
        // we start at 1 second to match the JavaScript behavior
        secondsSinceGameStart = 1; // This will trigger the stage 1 condition on the next update
    }

    public boolean isInBedwars() {
        return inBedwars;
    }

    public int getEmeraldCounterTime() {
        return timeUntilEmerald;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public String getEmeraldCounterText() {
        return emeraldCounterTimeText;
    }

    private String getFormattedEmeraldCountText() {
        String timeString;
        if (timeUntilEmerald <= 0) {
            timeString = "0";
        } else {
            timeString = String.valueOf(timeUntilEmerald);
        }

        return "(§f" + spawnCount + "§a): §7" + timeString + "s";
    }
}
