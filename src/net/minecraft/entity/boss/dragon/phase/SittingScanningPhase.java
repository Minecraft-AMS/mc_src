/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingScanningPhase
extends AbstractSittingPhase {
    private static final TargetPredicate PLAYER_WITHIN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(150.0);
    private final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(20.0).setPredicate(livingEntity -> Math.abs(livingEntity.getY() - dragon.getY()) <= 10.0);
    private int ticks;

    public SittingScanningPhase(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void serverTick() {
        ++this.ticks;
        PlayerEntity livingEntity = this.dragon.world.getClosestPlayer(this.CLOSE_PLAYER_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (livingEntity != null) {
            if (this.ticks > 25) {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
            } else {
                Vec3d vec3d = new Vec3d(livingEntity.getX() - this.dragon.getX(), 0.0, livingEntity.getZ() - this.dragon.getZ()).normalize();
                Vec3d vec3d2 = new Vec3d(MathHelper.sin(this.dragon.yaw * ((float)Math.PI / 180)), 0.0, -MathHelper.cos(this.dragon.yaw * ((float)Math.PI / 180))).normalize();
                float f = (float)vec3d2.dotProduct(vec3d);
                float g = (float)(Math.acos(f) * 57.2957763671875) + 0.5f;
                if (g < 0.0f || g > 10.0f) {
                    float i;
                    double d = livingEntity.getX() - this.dragon.partHead.getX();
                    double e = livingEntity.getZ() - this.dragon.partHead.getZ();
                    double h = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(d, e) * 57.2957763671875 - (double)this.dragon.yaw), -100.0, 100.0);
                    this.dragon.field_20865 *= 0.8f;
                    float j = i = MathHelper.sqrt(d * d + e * e) + 1.0f;
                    if (i > 40.0f) {
                        i = 40.0f;
                    }
                    this.dragon.field_20865 = (float)((double)this.dragon.field_20865 + h * (double)(0.7f / i / j));
                    this.dragon.yaw += this.dragon.field_20865;
                }
            }
        } else if (this.ticks >= 100) {
            livingEntity = this.dragon.world.getClosestPlayer(PLAYER_WITHIN_RANGE_PREDICATE, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            if (livingEntity != null) {
                this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                this.dragon.getPhaseManager().create(PhaseType.CHARGING_PLAYER).setTarget(new Vec3d(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ()));
            }
        }
    }

    @Override
    public void beginPhase() {
        this.ticks = 0;
    }

    public PhaseType<SittingScanningPhase> getType() {
        return PhaseType.SITTING_SCANNING;
    }
}

