/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FlyingEntity
extends MobEntity {
    protected FlyingEntity(EntityType<? extends FlyingEntity> type, World world) {
        super((EntityType<? extends MobEntity>)type, world);
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isTouchingWater()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8f));
        } else if (this.isInLava()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.5));
        } else {
            float f = 0.91f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().y1 - 1.0, this.z)).getBlock().getSlipperiness() * 0.91f;
            }
            float g = 0.16277137f / (f * f * f);
            f = 0.91f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().y1 - 1.0, this.z)).getBlock().getSlipperiness() * 0.91f;
            }
            this.updateVelocity(this.onGround ? 0.1f * g : 0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(f));
        }
        this.lastLimbDistance = this.limbDistance;
        double d = this.x - this.prevX;
        double e = this.z - this.prevZ;
        float h = MathHelper.sqrt(d * d + e * e) * 4.0f;
        if (h > 1.0f) {
            h = 1.0f;
        }
        this.limbDistance += (h - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }
}

