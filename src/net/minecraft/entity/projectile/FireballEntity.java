/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class FireballEntity
extends AbstractFireballEntity {
    public int explosionPower = 1;

    public FireballEntity(EntityType<? extends FireballEntity> entityType, World world) {
        super((EntityType<? extends AbstractFireballEntity>)entityType, world);
    }

    @Environment(value=EnvType.CLIENT)
    public FireballEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.FIREBALL, x, y, z, velocityX, velocityY, velocityZ, world);
    }

    public FireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.FIREBALL, owner, velocityX, velocityY, velocityZ, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.world.isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                entity.damage(DamageSource.explosiveProjectile(this, this.owner), 6.0f);
                this.dealDamage(this.owner, entity);
            }
            boolean bl = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
            this.world.createExplosion(null, this.x, this.y, this.z, this.explosionPower, bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
            this.remove();
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("ExplosionPower", this.explosionPower);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("ExplosionPower", 99)) {
            this.explosionPower = tag.getInt("ExplosionPower");
        }
    }
}

