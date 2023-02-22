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
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class UpdateAttackTargetTask<E extends MobEntity>
extends Task<E> {
    private final Predicate<E> startCondition;
    private final Function<E, Optional<? extends LivingEntity>> targetGetter;

    public UpdateAttackTargetTask(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter) {
        this(startCondition, targetGetter, 60);
    }

    public UpdateAttackTargetTask(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter, int duration) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryModuleState.REGISTERED)), duration);
        this.startCondition = startCondition;
        this.targetGetter = targetGetter;
    }

    public UpdateAttackTargetTask(Function<E, Optional<? extends LivingEntity>> targetGetter) {
        this((E entity) -> true, targetGetter);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
        if (!this.startCondition.test(mobEntity)) {
            return false;
        }
        Optional<? extends LivingEntity> optional = this.targetGetter.apply(mobEntity);
        if (optional.isPresent()) {
            return ((LivingEntity)mobEntity).canTarget(optional.get());
        }
        return false;
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        this.targetGetter.apply(mobEntity).ifPresent(target -> UpdateAttackTargetTask.updateAttackTarget(mobEntity, target));
    }

    public static <E extends MobEntity> void updateAttackTarget(E entity, LivingEntity target) {
        entity.getBrain().remember(MemoryModuleType.ATTACK_TARGET, target);
        entity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}

