/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class GoToCelebrateTask<E extends MobEntity>
extends Task<E> {
    private final int completionRange;
    private final float field_23130;

    public GoToCelebrateTask(int completionRange, float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.CELEBRATE_LOCATION, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
        this.completionRange = completionRange;
        this.field_23130 = speed;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        BlockPos blockPos = GoToCelebrateTask.getCelebrateLocation(mobEntity);
        boolean bl = blockPos.isWithinDistance(mobEntity.getBlockPos(), (double)this.completionRange);
        if (!bl) {
            LookTargetUtil.walkTowards((LivingEntity)mobEntity, GoToCelebrateTask.fuzz(mobEntity, blockPos), this.field_23130, this.completionRange);
        }
    }

    private static BlockPos fuzz(MobEntity mob, BlockPos pos) {
        Random random = mob.world.random;
        return pos.add(GoToCelebrateTask.fuzz(random), 0, GoToCelebrateTask.fuzz(random));
    }

    private static int fuzz(Random random) {
        return random.nextInt(3) - 1;
    }

    private static BlockPos getCelebrateLocation(MobEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.CELEBRATE_LOCATION).get();
    }
}

