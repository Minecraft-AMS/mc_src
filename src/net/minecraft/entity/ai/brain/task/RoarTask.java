/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;

public class RoarTask
extends MultiTickTask<WardenEntity> {
    private static final int SOUND_DELAY = 25;
    private static final int ANGER_INCREASE = 20;

    public RoarTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ROAR_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.ROAR_SOUND_COOLDOWN, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ROAR_SOUND_DELAY, (Object)((Object)MemoryModuleState.REGISTERED)), WardenBrain.ROAR_DURATION);
    }

    @Override
    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Brain<WardenEntity> brain = wardenEntity.getBrain();
        brain.remember(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
        brain.forget(MemoryModuleType.WALK_TARGET);
        LivingEntity livingEntity = wardenEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET).get();
        LookTargetUtil.lookAt(wardenEntity, livingEntity);
        wardenEntity.setPose(EntityPose.ROARING);
        wardenEntity.increaseAngerAt(livingEntity, 20, false);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        return true;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        if (wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_DELAY) || wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            return;
        }
        wardenEntity.getBrain().remember(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, WardenBrain.ROAR_DURATION - 25);
        wardenEntity.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 3.0f, 1.0f);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        if (wardenEntity.isInPose(EntityPose.ROARING)) {
            wardenEntity.setPose(EntityPose.STANDING);
        }
        wardenEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ROAR_TARGET).ifPresent(wardenEntity::updateAttackTarget);
        wardenEntity.getBrain().forget(MemoryModuleType.ROAR_TARGET);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (WardenEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (WardenEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (WardenEntity)entity, time);
    }
}

