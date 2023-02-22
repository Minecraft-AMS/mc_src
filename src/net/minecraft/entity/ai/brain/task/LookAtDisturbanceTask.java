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
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask
extends Task<WardenEntity> {
    public LookAtDisturbanceTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.DISTURBANCE_LOCATION, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ROAR_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
        return wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.DISTURBANCE_LOCATION) || wardenEntity.getBrain().hasMemoryModule(MemoryModuleType.ROAR_TARGET);
    }

    @Override
    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        BlockPos blockPos = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET).map(Entity::getBlockPos).or(() -> wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.DISTURBANCE_LOCATION)).get();
        wardenEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
    }
}

