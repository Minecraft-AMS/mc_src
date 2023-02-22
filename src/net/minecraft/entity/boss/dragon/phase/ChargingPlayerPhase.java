/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChargingPlayerPhase
extends AbstractPhase {
    private static final Logger LOGGER = LogManager.getLogger();
    private Vec3d target;
    private int field_7037;

    public ChargingPlayerPhase(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void serverTick() {
        if (this.target == null) {
            LOGGER.warn("Aborting charge player as no target was set.");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        if (this.field_7037 > 0 && this.field_7037++ >= 10) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        double d = this.target.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            ++this.field_7037;
        }
    }

    @Override
    public void beginPhase() {
        this.target = null;
        this.field_7037 = 0;
    }

    public void setTarget(Vec3d target) {
        this.target = target;
    }

    @Override
    public float method_6846() {
        return 3.0f;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.target;
    }

    public PhaseType<ChargingPlayerPhase> getType() {
        return PhaseType.CHARGING_PLAYER;
    }
}

