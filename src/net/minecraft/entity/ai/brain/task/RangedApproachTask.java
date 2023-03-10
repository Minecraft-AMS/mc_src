/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class RangedApproachTask
extends Task<MobEntity> {
    private static final int WEAPON_REACH_REDUCTION = 1;
    private final Function<LivingEntity, Float> speed;

    public RangedApproachTask(float speed) {
        this((LivingEntity livingEntity) -> Float.valueOf(speed));
    }

    public RangedApproachTask(Function<LivingEntity, Float> speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.speed = speed;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        LivingEntity livingEntity = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.isTargetWithinAttackRange(mobEntity, livingEntity, 1)) {
            this.forgetWalkTarget(mobEntity);
        } else {
            this.rememberWalkTarget(mobEntity, livingEntity);
        }
    }

    private void rememberWalkTarget(LivingEntity entity, LivingEntity target) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
        WalkTarget walkTarget = new WalkTarget(new EntityLookTarget(target, false), this.speed.apply(entity).floatValue(), 0);
        brain.remember(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    private void forgetWalkTarget(LivingEntity entity) {
        entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }
}

