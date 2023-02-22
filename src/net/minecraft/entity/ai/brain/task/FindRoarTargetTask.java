/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.WardenEntity;

public class FindRoarTargetTask {
    public static <E extends WardenEntity> Task<E> create(Function<E, Optional<? extends LivingEntity>> targetFinder) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryAbsent(MemoryModuleType.ROAR_TARGET), context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryOptional(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply((Applicative)context, (roarTarget, attackTarget, cantReachWalkTargetSince) -> (world, entity, time) -> {
            Optional optional = (Optional)targetFinder.apply(entity);
            if (optional.filter(entity::isValidTarget).isEmpty()) {
                return false;
            }
            roarTarget.remember((LivingEntity)optional.get());
            cantReachWalkTargetSince.forget();
            return true;
        }));
    }
}

