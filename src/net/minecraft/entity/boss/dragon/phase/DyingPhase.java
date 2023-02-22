/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;

public class DyingPhase
extends AbstractPhase {
    private Vec3d field_7041;
    private int ticks;

    public DyingPhase(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void clientTick() {
        if (this.ticks++ % 10 == 0) {
            float f = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            float g = (this.dragon.getRandom().nextFloat() - 0.5f) * 4.0f;
            float h = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            this.dragon.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.x + (double)f, this.dragon.y + 2.0 + (double)g, this.dragon.z + (double)h, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void serverTick() {
        double d;
        ++this.ticks;
        if (this.field_7041 == null) {
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN);
            this.field_7041 = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        if ((d = this.field_7041.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z)) < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.dragon.setHealth(0.0f);
        } else {
            this.dragon.setHealth(1.0f);
        }
    }

    @Override
    public void beginPhase() {
        this.field_7041 = null;
        this.ticks = 0;
    }

    @Override
    public float method_6846() {
        return 3.0f;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7041;
    }

    public PhaseType<DyingPhase> getType() {
        return PhaseType.DYING;
    }
}
