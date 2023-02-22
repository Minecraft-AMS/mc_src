/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

public class FindEntityTask {
    public static <T extends LivingEntity> Task<LivingEntity> create(EntityType<? extends T> type, int maxDistance, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        return FindEntityTask.create(type, maxDistance, entity -> true, entity -> true, targetModule, speed, completionRange);
    }

    public static <E extends LivingEntity, T extends LivingEntity> Task<E> create(EntityType<? extends T> type, int maxDistance, Predicate<E> entityPredicate, Predicate<T> targetPredicate, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        int i = maxDistance * maxDistance;
        Predicate<LivingEntity> predicate = entity -> type.equals(entity.getType()) && targetPredicate.test(entity);
        return TaskTriggerer.task(context -> context.group(context.queryMemoryOptional(targetModule), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.VISIBLE_MOBS)).apply((Applicative)context, (targetValue, lookTarget, walkTarget, visibleMobs) -> (world, entity, time) -> {
            LivingTargetCache livingTargetCache = (LivingTargetCache)context.getValue(visibleMobs);
            if (entityPredicate.test(entity) && livingTargetCache.anyMatch(predicate)) {
                Optional<LivingEntity> optional = livingTargetCache.findFirst(target -> target.squaredDistanceTo(entity) <= (double)i && predicate.test((LivingEntity)target));
                optional.ifPresent(target -> {
                    targetValue.remember(target);
                    lookTarget.remember(new EntityLookTarget((Entity)target, true));
                    walkTarget.remember(new WalkTarget(new EntityLookTarget((Entity)target, false), speed, completionRange));
                });
                return true;
            }
            return false;
        }));
    }
}

