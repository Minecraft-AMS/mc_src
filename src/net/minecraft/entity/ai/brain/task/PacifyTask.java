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

public class PacifyTask
extends Task<LivingEntity> {
    private final int duration;

    public PacifyTask(MemoryModuleType<?> requiredMemoryModuleType, int duration) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.PACIFIED, (Object)((Object)MemoryModuleState.VALUE_ABSENT), requiredMemoryModuleType, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.duration = duration;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        entity.getBrain().remember(MemoryModuleType.PACIFIED, true, this.duration);
        entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
    }
}

