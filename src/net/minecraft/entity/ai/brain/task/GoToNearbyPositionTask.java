/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class GoToNearbyPositionTask
extends Task<MobEntityWithAi> {
    private final MemoryModuleType<GlobalPos> memoryModuleType;
    private final int field_18863;
    private final int maxDistance;
    private long nextRunTime;

    public GoToNearbyPositionTask(MemoryModuleType<GlobalPos> memoryModuleType, int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), memoryModuleType, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.memoryModuleType = memoryModuleType;
        this.field_18863 = i;
        this.maxDistance = j;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.memoryModuleType);
        return optional.isPresent() && Objects.equals(serverWorld.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        if (l > this.nextRunTime) {
            Brain<?> brain = mobEntityWithAi.getBrain();
            Optional<GlobalPos> optional = brain.getOptionalMemory(this.memoryModuleType);
            optional.ifPresent(globalPos -> brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), 0.4f, this.field_18863)));
            this.nextRunTime = l + 80L;
        }
    }
}

