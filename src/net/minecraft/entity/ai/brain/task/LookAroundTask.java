/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask
extends MultiTickTask<MobEntity> {
    public LookAroundTask(int minRunTime, int maxRunTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return mobEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LOOK_TARGET).filter(lookTarget -> lookTarget.isSeenBy(mobEntity)).isPresent();
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LOOK_TARGET).ifPresent(lookTarget -> mobEntity.getLookControl().lookAt(lookTarget.getPos()));
    }
}

