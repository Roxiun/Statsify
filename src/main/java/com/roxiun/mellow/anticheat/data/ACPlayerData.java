package com.roxiun.mellow.anticheat.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class ACPlayerData {

    private final EntityPlayer player;
    public final Map<String, CheckData> checkDataMap =
        new ConcurrentHashMap<>();
    public final List<PositionSample> positionHistory = new ArrayList<>();
    public Vec3 velocity;
    public Vec3 lastVelocity;

    public long lastSwingTime;
    public long lastUseItemTime;
    public long lastBlockPlaceTime;
    public long lastDamageTime;
    public long lastBlockStartTime;

    public boolean isUsingItem;
    public boolean isBlocking;
    public boolean isSprinting;
    public boolean isCrouching;
    public boolean isOnGround;
    public boolean wasSwinging;
    public boolean wasBlocking;
    public boolean wasUsingItem;
    public boolean wasCrouching;
    public float swingProgress;
    public float lastSwingProgress;

    // Eagle Check specific fields
    public long currentTick;
    public long lastCrouchStartTick;
    public long lastCrouchEndTick;
    public long lastSwingTick;
    public int eaglePatternCount;
    public long lastEaglePatternTick;
    public Vec3 lastPosition;
    public final List<Integer> crouchDurations = new ArrayList<>();
    public int eagleConsecutiveViolations;

    // Scaffold Check specific fields
    public int scaffoldConsecutiveViolations;
    public String lastScaffoldViolationType;
    public long lastScaffoldViolationTime;

    public ACPlayerData(EntityPlayer player) {
        this.player = player;
    }

    public void updatePosition(double x, double y, double z) {
        long now = System.currentTimeMillis();
        if (!positionHistory.isEmpty()) {
            PositionSample lastSample = positionHistory.get(
                positionHistory.size() - 1
            );
            long timeDelta = now - lastSample.timestamp;
            if (timeDelta > 0) {
                this.lastVelocity = this.velocity;
                double dx = x - lastSample.pos.xCoord;
                double dy = y - lastSample.pos.yCoord;
                double dz = z - lastSample.pos.zCoord;
                // Convert to per-second velocity
                this.velocity = new Vec3(
                    dx / (timeDelta / 1000.0),
                    dy / (timeDelta / 1000.0),
                    dz / (timeDelta / 1000.0)
                );
            }
        }
        positionHistory.add(new PositionSample(new Vec3(x, y, z), now));
        if (positionHistory.size() > 20) {
            positionHistory.remove(0);
        }
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public boolean isHoldingBlock() {
        if (player.getHeldItem() == null) {
            return false;
        }
        return (
            player.getHeldItem().getItem() instanceof
                net.minecraft.item.ItemBlock
        );
    }

    public static class PositionSample {

        public final Vec3 pos;
        public final long timestamp;

        public PositionSample(Vec3 pos, long timestamp) {
            this.pos = pos;
            this.timestamp = timestamp;
        }
    }

    // AutoBlock Check specific fields
    public List<SwingData> swingHistory = new ArrayList<>();
    public long lastSwingDetected = 0;

    public static class SwingData {

        public long time;
        public boolean wasBlockingBefore;
        public Boolean wasBlockingAfter; // Using Boolean to allow null

        public SwingData(long time, boolean wasBlockingBefore) {
            this.time = time;
            this.wasBlockingBefore = wasBlockingBefore;
            this.wasBlockingAfter = null;
        }
    }

    public static class CheckData {

        public double violations = 0;
        public long lastAlertTime = 0;
    }
}
