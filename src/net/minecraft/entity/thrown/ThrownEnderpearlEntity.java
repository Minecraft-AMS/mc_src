/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class ThrownEnderpearlEntity
extends ThrownItemEntity {
    private LivingEntity owner;

    public ThrownEnderpearlEntity(EntityType<? extends ThrownEnderpearlEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>)entityType, world);
    }

    public ThrownEnderpearlEntity(World world, LivingEntity owner) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.ENDER_PEARL, owner, world);
        this.owner = owner;
    }

    @Environment(value=EnvType.CLIENT)
    public ThrownEnderpearlEntity(World world, double x, double y, double z) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.ENDER_PEARL, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        BlockPos blockPos;
        BlockEntity blockEntity;
        LivingEntity livingEntity = this.getOwner();
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)hitResult).getEntity();
            if (entity == this.owner) {
                return;
            }
            entity.damage(DamageSource.thrownProjectile(this, livingEntity), 0.0f);
        }
        if (hitResult.getType() == HitResult.Type.BLOCK && (blockEntity = this.world.getBlockEntity(blockPos = ((BlockHitResult)hitResult).getBlockPos())) instanceof EndGatewayBlockEntity) {
            EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
            if (livingEntity != null) {
                if (livingEntity instanceof ServerPlayerEntity) {
                    Criterions.ENTER_BLOCK.trigger((ServerPlayerEntity)livingEntity, this.world.getBlockState(blockPos));
                }
                endGatewayBlockEntity.tryTeleportingEntity(livingEntity);
                this.remove();
                return;
            }
            endGatewayBlockEntity.tryTeleportingEntity(this);
            return;
        }
        for (int i = 0; i < 32; ++i) {
            this.world.addParticle(ParticleTypes.PORTAL, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }
        if (!this.world.isClient) {
            if (livingEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
                if (serverPlayerEntity.networkHandler.getConnection().isOpen() && serverPlayerEntity.world == this.world && !serverPlayerEntity.isSleeping()) {
                    if (this.random.nextFloat() < 0.05f && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                        EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(this.world);
                        endermiteEntity.setPlayerSpawned(true);
                        endermiteEntity.refreshPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
                        this.world.spawnEntity(endermiteEntity);
                    }
                    if (livingEntity.hasVehicle()) {
                        livingEntity.stopRiding();
                    }
                    livingEntity.requestTeleport(this.x, this.y, this.z);
                    livingEntity.fallDistance = 0.0f;
                    livingEntity.damage(DamageSource.FALL, 5.0f);
                }
            } else if (livingEntity != null) {
                livingEntity.requestTeleport(this.x, this.y, this.z);
                livingEntity.fallDistance = 0.0f;
            }
            this.remove();
        }
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity != null && livingEntity instanceof PlayerEntity && !livingEntity.isAlive()) {
            this.remove();
        } else {
            super.tick();
        }
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType newDimension) {
        if (this.owner.dimension != newDimension) {
            this.owner = null;
        }
        return super.changeDimension(newDimension);
    }
}

