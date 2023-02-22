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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class SniffTask<E extends WardenEntity>
extends Task<E> {
    private static final double HORIZONTAL_RADIUS = 6.0;
    private static final double VERTICAL_RADIUS = 20.0;

    public SniffTask(int runTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.IS_SNIFFING, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.NEAREST_ATTACKABLE, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.DISTURBANCE_LOCATION, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.SNIFF_COOLDOWN, (Object)((Object)MemoryModuleState.REGISTERED)), runTime);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        ((Entity)wardenEntity).playSound(SoundEvents.ENTITY_WARDEN_SNIFF, 5.0f, 1.0f);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        if (((Entity)wardenEntity).isInPose(EntityPose.SNIFFING)) {
            ((Entity)wardenEntity).setPose(EntityPose.STANDING);
        }
        ((WardenEntity)wardenEntity).getBrain().forget(MemoryModuleType.IS_SNIFFING);
        ((WardenEntity)wardenEntity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE).filter(arg_0 -> wardenEntity.isValidTarget(arg_0)).ifPresent(target -> {
            if (wardenEntity.isInRange((Entity)target, 6.0, 20.0)) {
                wardenEntity.increaseAngerAt((Entity)target);
            }
            if (!wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION)) {
                WardenBrain.lookAtDisturbance(wardenEntity, target.getBlockPos());
            }
        });
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (E)((WardenEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (E)((WardenEntity)entity), time);
    }
}

