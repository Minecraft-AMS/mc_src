/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class AdmireItemTask<E extends PiglinEntity>
extends Task<E> {
    private final int duration;

    public AdmireItemTask(int duration) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.ADMIRING_DISABLED, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.duration = duration;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E piglinEntity) {
        ItemEntity itemEntity = ((PiglinEntity)piglinEntity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return PiglinBrain.isGoldenItem(itemEntity.getStack());
    }

    @Override
    protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
        ((PiglinEntity)piglinEntity).getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, this.duration);
    }
}

