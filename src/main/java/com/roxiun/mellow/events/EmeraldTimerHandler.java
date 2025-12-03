package com.roxiun.mellow.events;

import com.roxiun.mellow.api.hypixel.HypixelFeatures;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EmeraldTimerHandler {

    private final HypixelFeatures hypixelFeatures;
    private boolean gameStarted = false;
    private int tickCounter = 0;

    public EmeraldTimerHandler(HypixelFeatures hypixelFeatures) {
        this.hypixelFeatures = hypixelFeatures;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            tickCounter++;

            // Update emerald timer if in Bedwars
            if (hypixelFeatures.isInBedwars()) {
                hypixelFeatures.updateEmeraldTimer();

                // Check if game has just started (first tick in game)
                if (!gameStarted) {
                    gameStarted = true;
                }
            } else {
                // Reset game started flag when not in Bedwars
                gameStarted = false;
            }
        }
    }

    public void onGameStart(String mode) {
        hypixelFeatures.startNewGame();
        hypixelFeatures.setMode(mode);
        gameStarted = true;
        tickCounter = 0;
    }

    public void onGameEnd() {
        hypixelFeatures.resetEmeraldTimer();
        gameStarted = false;
        tickCounter = 0;
    }
}
