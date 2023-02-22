/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DragonFireballEntity
extends ExplosiveProjectileEntity {
    public DragonFireballEntity(EntityType<? extends DragonFireballEntity> entityType, World world) {
        super((EntityType<? extends ExplosiveProjectileEntity>)entityType, world);
    }

    @Environment(value=EnvType.CLIENT)
    public DragonFireballEntity(World world, double x, double y, double z, double directionX, double directionY, double directionZ) {
        super(EntityType.DRAGON_FIREBALL, x, y, z, directionX, directionY, directionZ, world);
    }

    public DragonFireballEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(EntityType.DRAGON_FIREBALL, owner, directionX, directionY, directionZ, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity().isPartOf(this.owner)) {
            return;
        }
        if (!this.world.isClient) {
            List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(4.0, 2.0, 4.0));
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
            areaEffectCloudEntity.setOwner(this.owner);
            areaEffectCloudEntity.setParticleType(ParticleTypes.DRAGON_BREATH);
            areaEffectCloudEntity.setRadius(3.0f);
            areaEffectCloudEntity.setDuration(600);
            areaEffectCloudEntity.setRadiusGrowth((7.0f - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
            areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1));
            if (!list.isEmpty()) {
                for (LivingEntity livingEntity : list) {
                    double d = this.squaredDistanceTo(livingEntity);
                    if (!(d < 16.0)) continue;
                    areaEffectCloudEntity.updatePosition(livingEntity.x, livingEntity.y, livingEntity.z);
                    break;
                }
            }
            this.world.playLevelEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
            this.world.spawnEntity(areaEffectCloudEntity);
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

    @Override
    protected ParticleEffect getParticleType() {
        return ParticleTypes.DRAGON_BREATH;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }
}

