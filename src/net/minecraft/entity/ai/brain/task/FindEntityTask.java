/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class FindEntityTask<E extends LivingEntity, T extends LivingEntity>
extends Task<E> {
    private final int completionRange;
    private final float speed;
    private final EntityType<? extends T> entityType;
    private final int maxSquaredDistance;
    private final Predicate<T> predicate;
    private final Predicate<E> shouldRunPredicate;
    private final MemoryModuleType<T> targetModule;

    public FindEntityTask(EntityType<? extends T> entityType, int maxDistance, Predicate<E> shouldRunPredicate, Predicate<T> predicate, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityType = entityType;
        this.speed = speed;
        this.maxSquaredDistance = maxDistance * maxDistance;
        this.completionRange = completionRange;
        this.predicate = predicate;
        this.shouldRunPredicate = shouldRunPredicate;
        this.targetModule = targetModule;
    }

    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(EntityType<? extends T> entityType, int maxDistance, MemoryModuleType<T> targetModule, float speed, int completionRange) {
        return new FindEntityTask<LivingEntity, LivingEntity>(entityType, maxDistance, entity -> true, entity -> true, targetModule, speed, completionRange);
    }

    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(EntityType<? extends T> entityType, int maxDistance, Predicate<T> condition, MemoryModuleType<T> moduleType, float speed, int completionRange) {
        return new FindEntityTask<LivingEntity, T>(entityType, maxDistance, entity -> true, condition, moduleType, speed, completionRange);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.shouldRunPredicate.test(entity) && this.anyVisibleTo(entity);
    }

    private boolean anyVisibleTo(E entity) {
        LivingTargetCache livingTargetCache = ((LivingEntity)entity).getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
        return livingTargetCache.anyMatch(this::testPredicate);
    }

    private boolean testPredicate(LivingEntity entity) {
        return this.entityType.equals(entity.getType()) && this.predicate.test(entity);
    }

    @Override
    protected void run(ServerWorld world, E entity, long time) {
        Brain<?> brain = ((LivingEntity)entity).getBrain();
        Optional<LivingTargetCache> optional = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
        if (optional.isEmpty()) {
            return;
        }
        LivingTargetCache livingTargetCache = optional.get();
        livingTargetCache.findFirst(target -> this.shouldTarget(entity, (LivingEntity)target)).ifPresent(target -> {
            brain.remember(this.targetModule, target);
            brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)target, true));
            brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget((Entity)target, false), this.speed, this.completionRange));
        });
    }

    private boolean shouldTarget(E self, LivingEntity target) {
        return this.entityType.equals(target.getType()) && target.squaredDistanceTo((Entity)self) <= (double)this.maxSquaredDistance && this.predicate.test(target);
    }
}

