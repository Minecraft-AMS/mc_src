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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class HuntHoglinTask<E extends PiglinEntity>
extends Task<E> {
    public HuntHoglinTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HUNTED_RECENTLY, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PiglinEntity piglinEntity) {
        return !piglinEntity.isBaby() && !PiglinBrain.haveHuntedHoglinsRecently(piglinEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
        HoglinEntity hoglinEntity = ((PiglinEntity)piglinEntity).getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();
        PiglinBrain.becomeAngryWith(piglinEntity, hoglinEntity);
        PiglinBrain.rememberHunting(piglinEntity);
        PiglinBrain.angerAtCloserTargets(piglinEntity, hoglinEntity);
        PiglinBrain.rememberGroupHunting(piglinEntity);
    }
}

