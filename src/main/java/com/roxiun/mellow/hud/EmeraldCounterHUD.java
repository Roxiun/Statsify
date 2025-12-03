package com.roxiun.mellow.hud;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import com.roxiun.mellow.api.hypixel.HypixelFeatures;

public class EmeraldCounterHUD extends SingleTextHud {

    public static final OneColor EMPTY_COLOR = new OneColor(0, 0, 0, 0);

    public EmeraldCounterHUD() {
        super(
            "Emeralds", // title is actually useful now
            true, // enabled obviously
            5, // x
            25, // y
            1, // normal size
            false, // no background it's ugly
            false, // no rounded corners it's also ugly
            0, // NO rounded corners
            0, // no x padding why would i want it
            0, // no y padding for the same reason
            EMPTY_COLOR, // no background color
            false, // no border
            0, // NO border
            EMPTY_COLOR // no border color
        );
        textType = 1;
    }

    @Override
    public boolean shouldShow() {
        return (
            super.shouldShow() && HypixelFeatures.getInstance().isInBedwars()
        );
    }

    @Override
    protected String getText(boolean example) {
        if (example) return "§b25§as";
        else {
            return HypixelFeatures.getInstance().getEmeraldCounterText();
        }
    }
}
