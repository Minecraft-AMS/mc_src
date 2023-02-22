/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity
extends AbstractFireballEntity {
    public SmallFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
        super((EntityType<? extends AbstractFireballEntity>)entityType, world);
    }

    public SmallFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, owner, velocityX, velocityY, velocityZ, world);
    }

    public SmallFireballEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, x, y, z, velocityX, velocityY, velocityZ, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            BlockHitResult blockHitResult;
            BlockPos blockPos;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                if (!entity.isFireImmune()) {
                    int i = entity.getFireTicks();
                    entity.setOnFireFor(5);
                    boolean bl = entity.damage(DamageSource.explosiveProjectile(this, this.owner), 5.0f);
                    if (bl) {
                        this.dealDamage(this.owner, entity);
                    } else {
                        entity.setFireTicks(i);
                    }
                }
            } else if ((this.owner == null || !(this.owner instanceof MobEntity) || this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) && this.world.isAir(blockPos = (blockHitResult = (BlockHitResult)hitResult).getBlockPos().offset(blockHitResult.getSide()))) {
                this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
            }
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }
}

