/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class DigTask<E extends WardenEntity>
extends MultiTickTask<E> {
    public DigTask(int duration) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), duration);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return ((Entity)wardenEntity).getRemovalReason() == null;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
        return ((Entity)wardenEntity).isOnGround() || ((Entity)wardenEntity).isTouchingWater() || ((Entity)wardenEntity).isInLava();
    }

    @Override
    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        if (((Entity)wardenEntity).isOnGround()) {
            ((Entity)wardenEntity).setPose(EntityPose.DIGGING);
            ((Entity)wardenEntity).playSound(SoundEvents.ENTITY_WARDEN_DIG, 5.0f, 1.0f);
        } else {
            ((Entity)wardenEntity).playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0f, 1.0f);
            this.finishRunning(serverWorld, wardenEntity, l);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        if (((Entity)wardenEntity).getRemovalReason() == null) {
            ((LivingEntity)wardenEntity).remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (E)((WardenEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (E)((WardenEntity)entity), time);
    }
}

