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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class WalkTowardClosestAdultTask<E extends PassiveEntity>
extends Task<E> {
    private final IntRange executionRange;
    private final float speed;

    public WalkTowardClosestAdultTask(IntRange executionRange, float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.executionRange = executionRange;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E passiveEntity) {
        if (!((PassiveEntity)passiveEntity).isBaby()) {
            return false;
        }
        PassiveEntity passiveEntity2 = this.getNearestVisibleAdult(passiveEntity);
        return ((Entity)passiveEntity).isInRange(passiveEntity2, this.executionRange.getMax() + 1) && !((Entity)passiveEntity).isInRange(passiveEntity2, this.executionRange.getMin());
    }

    @Override
    protected void run(ServerWorld serverWorld, E passiveEntity, long l) {
        LookTargetUtil.walkTowards(passiveEntity, this.getNearestVisibleAdult(passiveEntity), this.speed, this.executionRange.getMin() - 1);
    }

    private PassiveEntity getNearestVisibleAdult(E entity) {
        return ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}

