/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class ScheduleActivityTask
extends Task<LivingEntity> {
    public ScheduleActivityTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        entity.getBrain().refreshActivities(world.getTimeOfDay(), world.getTime());
    }
}

