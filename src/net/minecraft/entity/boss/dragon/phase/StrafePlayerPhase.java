/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StrafePlayerPhase
extends AbstractPhase {
    private static final Logger LOGGER = LogManager.getLogger();
    private int field_7060;
    private Path field_7059;
    private Vec3d field_7057;
    private LivingEntity field_7062;
    private boolean field_7058;

    public StrafePlayerPhase(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void serverTick() {
        double h;
        double e;
        double d;
        if (this.field_7062 == null) {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        if (this.field_7059 != null && this.field_7059.isFinished()) {
            d = this.field_7062.getX();
            e = this.field_7062.getZ();
            double f = d - this.dragon.getX();
            double g = e - this.dragon.getZ();
            h = MathHelper.sqrt(f * f + g * g);
            double i = Math.min((double)0.4f + h / 80.0 - 1.0, 10.0);
            this.field_7057 = new Vec3d(d, this.field_7062.getY() + i, e);
        }
        double d2 = d = this.field_7057 == null ? 0.0 : this.field_7057.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0) {
            this.method_6860();
        }
        e = 64.0;
        if (this.field_7062.squaredDistanceTo(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.field_7062)) {
                ++this.field_7060;
                Vec3d vec3d = new Vec3d(this.field_7062.getX() - this.dragon.getX(), 0.0, this.field_7062.getZ() - this.dragon.getZ()).normalize();
                Vec3d vec3d2 = new Vec3d(MathHelper.sin(this.dragon.yaw * ((float)Math.PI / 180)), 0.0, -MathHelper.cos(this.dragon.yaw * ((float)Math.PI / 180))).normalize();
                float j = (float)vec3d2.dotProduct(vec3d);
                float k = (float)(Math.acos(j) * 57.2957763671875);
                k += 0.5f;
                if (this.field_7060 >= 5 && k >= 0.0f && k < 10.0f) {
                    h = 1.0;
                    Vec3d vec3d3 = this.dragon.getRotationVec(1.0f);
                    double l = this.dragon.partHead.getX() - vec3d3.x * 1.0;
                    double m = this.dragon.partHead.getBodyY(0.5) + 0.5;
                    double n = this.dragon.partHead.getZ() - vec3d3.z * 1.0;
                    double o = this.field_7062.getX() - l;
                    double p = this.field_7062.getBodyY(0.5) - m;
                    double q = this.field_7062.getZ() - n;
                    this.dragon.world.playLevelEvent(null, 1017, new BlockPos(this.dragon), 0);
                    DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.world, this.dragon, o, p, q);
                    dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0f, 0.0f);
                    this.dragon.world.spawnEntity(dragonFireballEntity);
                    this.field_7060 = 0;
                    if (this.field_7059 != null) {
                        while (!this.field_7059.isFinished()) {
                            this.field_7059.next();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                }
            } else if (this.field_7060 > 0) {
                --this.field_7060;
            }
        } else if (this.field_7060 > 0) {
            --this.field_7060;
        }
    }

    private void method_6860() {
        if (this.field_7059 == null || this.field_7059.isFinished()) {
            int i;
            int j = i = this.dragon.method_6818();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.field_7058 = !this.field_7058;
                j += 6;
            }
            j = this.field_7058 ? ++j : --j;
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
                j -= 12;
                j &= 7;
                j += 12;
            } else if ((j %= 12) < 0) {
                j += 12;
            }
            this.field_7059 = this.dragon.method_6833(i, j, null);
            if (this.field_7059 != null) {
                this.field_7059.next();
            }
        }
        this.method_6861();
    }

    private void method_6861() {
        if (this.field_7059 != null && !this.field_7059.isFinished()) {
            double f;
            Vec3d vec3d = this.field_7059.getCurrentPosition();
            this.field_7059.next();
            double d = vec3d.x;
            double e = vec3d.z;
            while ((f = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0f)) < vec3d.y) {
            }
            this.field_7057 = new Vec3d(d, f, e);
        }
    }

    @Override
    public void beginPhase() {
        this.field_7060 = 0;
        this.field_7057 = null;
        this.field_7059 = null;
        this.field_7062 = null;
    }

    public void method_6862(LivingEntity livingEntity) {
        this.field_7062 = livingEntity;
        int i = this.dragon.method_6818();
        int j = this.dragon.method_6822(this.field_7062.getX(), this.field_7062.getY(), this.field_7062.getZ());
        int k = MathHelper.floor(this.field_7062.getX());
        int l = MathHelper.floor(this.field_7062.getZ());
        double d = (double)k - this.dragon.getX();
        double e = (double)l - this.dragon.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = Math.min((double)0.4f + f / 80.0 - 1.0, 10.0);
        int m = MathHelper.floor(this.field_7062.getY() + g);
        PathNode pathNode = new PathNode(k, m, l);
        this.field_7059 = this.dragon.method_6833(i, j, pathNode);
        if (this.field_7059 != null) {
            this.field_7059.next();
            this.method_6861();
        }
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7057;
    }

    public PhaseType<StrafePlayerPhase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
}

