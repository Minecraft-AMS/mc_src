/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class MemoryTransferTask<E extends MobEntity, T>
extends Task<E> {
    private final Predicate<E> runPredicate;
    private final MemoryModuleType<? extends T> sourceType;
    private final MemoryModuleType<T> targetType;
    private final UniformIntProvider duration;

    public MemoryTransferTask(Predicate<E> runPredicate, MemoryModuleType<? extends T> sourceType, MemoryModuleType<T> targetType, UniformIntProvider duration) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(sourceType, (Object)((Object)MemoryModuleState.VALUE_PRESENT), targetType, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.runPredicate = runPredicate;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.duration = duration;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
        return this.runPredicate.test(mobEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        Brain<?> brain = ((LivingEntity)mobEntity).getBrain();
        brain.remember(this.targetType, brain.getOptionalMemory(this.sourceType).get(), this.duration.get(serverWorld.random));
    }
}

