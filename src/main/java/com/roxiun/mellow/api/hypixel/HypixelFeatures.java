package com.roxiun.mellow.api.hypixel;

import net.hypixel.data.type.GameType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class HypixelFeatures {

    //
    // thanks yedelo - https://github.com/Yedelo/YedelMod/blob/master/src/main/java/at/yedel/yedelmod/features/major/BedwarsFeatures.java
    private static final HypixelFeatures INSTANCE = new HypixelFeatures();

    private boolean inBedwars;
    private int emeraldCounterTime;
    private String emeraldCounterTimeText;

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
        inBedwars =
            packet.getServerType().isPresent() &&
            packet.getServerType().get() == GameType.BEDWARS &&
            !packet.getLobbyName().isPresent();
    }

    public boolean isInBedwars() {
        return inBedwars;
    }

    public int getEmeraldCoutnerTime() {
        return emeraldCounterTime;
    }
}
