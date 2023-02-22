/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity
extends ZombieEntity {
    public HuskEntity(EntityType<? extends HuskEntity> entityType, World world) {
        super((EntityType<? extends ZombieEntity>)entityType, world);
    }

    public static boolean canSpawn(EntityType<HuskEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
        return HuskEntity.canSpawnInDark(type, world, spawnType, pos, random) && (spawnType == SpawnType.SPAWNER || world.isSkyVisible(pos));
    }

    @Override
    protected boolean burnsInDaylight() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HUSK_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HUSK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_HUSK_STEP;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl && this.getMainHandStack().isEmpty() && target instanceof LivingEntity) {
            float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
            ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f));
        }
        return bl;
    }

    @Override
    protected boolean canConvertInWater() {
        return true;
    }

    @Override
    protected void convertInWater() {
        this.convertTo(EntityType.ZOMBIE);
        this.world.playLevelEvent(null, 1041, new BlockPos(this), 0);
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
}

