/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;

public class TakeoffPhase
extends AbstractPhase {
    private boolean field_7056;
    private Path field_7054;
    private Vec3d field_7055;

    public TakeoffPhase(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void serverTick() {
        if (this.field_7056 || this.field_7054 == null) {
            this.field_7056 = false;
            this.method_6858();
        } else {
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            if (!blockPos.isWithinDistance(this.dragon.getPos(), 10.0)) {
                this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            }
        }
    }

    @Override
    public void beginPhase() {
        this.field_7056 = true;
        this.field_7054 = null;
        this.field_7055 = null;
    }

    private void method_6858() {
        int i = this.dragon.method_6818();
        Vec3d vec3d = this.dragon.method_6834(1.0f);
        int j = this.dragon.method_6822(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
        if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
            j -= 12;
            j &= 7;
            j += 12;
        } else if ((j %= 12) < 0) {
            j += 12;
        }
        this.field_7054 = this.dragon.method_6833(i, j, null);
        this.method_6859();
    }

    private void method_6859() {
        if (this.field_7054 != null) {
            this.field_7054.next();
            if (!this.field_7054.isFinished()) {
                double d;
                Vec3d vec3d = this.field_7054.getCurrentPosition();
                this.field_7054.next();
                while ((d = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0f)) < vec3d.y) {
                }
                this.field_7055 = new Vec3d(vec3d.x, d, vec3d.z);
            }
        }
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7055;
    }

    public PhaseType<TakeoffPhase> getType() {
        return PhaseType.TAKEOFF;
    }
}
