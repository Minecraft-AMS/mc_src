/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PiglinBrain;

public class AdmireItemTask {
    public static Task<LivingEntity> create(int duration) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), context.queryMemoryAbsent(MemoryModuleType.ADMIRING_ITEM), context.queryMemoryAbsent(MemoryModuleType.ADMIRING_DISABLED), context.queryMemoryAbsent(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply((Applicative)context, (nearestVisibleWantedItem, admiringItem, admiringDisabled, disableWalkToAdmireItem) -> (world, entity, time) -> {
            ItemEntity itemEntity = (ItemEntity)context.getValue(nearestVisibleWantedItem);
            if (!PiglinBrain.isGoldenItem(itemEntity.getStack())) {
                return false;
            }
            admiringItem.remember(true, duration);
            return true;
        }));
    }
}

