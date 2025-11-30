package com.roxiun.mellow.anticheat.check.impl;

import com.roxiun.mellow.Mellow;
import com.roxiun.mellow.anticheat.AnticheatManager;
import com.roxiun.mellow.anticheat.check.Check;
import com.roxiun.mellow.anticheat.data.ACPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoBlockCheck extends Check {

    public AutoBlockCheck() {
        super("AutoBlock", "Checks for attacking while blocking.");
    }

    @Override
    public void onPlayerTick(
        AnticheatManager manager,
        TickEvent.PlayerTickEvent event,
        ACPlayerData data
    ) {
        if (!Mellow.config.autoBlockCheckEnabled) return;
        EntityPlayer player = data.getPlayer();
        if (player == null || player == Minecraft.getMinecraft().thePlayer) {
            // Don't check self
            return;
        }

        boolean isBlocking = data.isBlocking;
        float currentSwingProgress = data.swingProgress;
        float lastSwingProgress = data.lastSwingProgress;

        if (
            isBlocking && currentSwingProgress > 0f && lastSwingProgress == 0f
        ) {
            String heldItemName = "nothing";
            if (player.getHeldItem() != null) {
                heldItemName = player.getHeldItem().getDisplayName();
            }
            manager.flag(
                data,
                this,
                "item: " +
                    heldItemName +
                    ", swing: " +
                    String.format("%.2f", currentSwingProgress),
                1.0
            );
        }
    }
}
