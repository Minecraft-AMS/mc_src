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
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GoToRememberedPositionTask<T>
extends Task<PathAwareEntity> {
    private final MemoryModuleType<T> entityMemory;
    private final float speed;
    private final int range;
    private final Function<T, Vec3d> posRetriever;

    public GoToRememberedPositionTask(MemoryModuleType<T> memoryType, float speed, int range, boolean requiresWalkTarget, Function<T, Vec3d> posRetriever) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)(requiresWalkTarget ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT)), memoryType, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityMemory = memoryType;
        this.speed = speed;
        this.range = range;
        this.posRetriever = posRetriever;
    }

    public static GoToRememberedPositionTask<BlockPos> toBlock(MemoryModuleType<BlockPos> memoryType, float speed, int range, boolean requiresWalkTarget) {
        return new GoToRememberedPositionTask<BlockPos>(memoryType, speed, range, requiresWalkTarget, Vec3d::ofBottomCenter);
    }

    public static GoToRememberedPositionTask<? extends Entity> toEntity(MemoryModuleType<? extends Entity> memoryType, float speed, int range, boolean requiresWalkTarget) {
        return new GoToRememberedPositionTask<Entity>(memoryType, speed, range, requiresWalkTarget, Entity::getPos);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        if (this.isWalkTargetPresentAndFar(pathAwareEntity)) {
            return false;
        }
        return pathAwareEntity.getPos().isInRange(this.getPos(pathAwareEntity), this.range);
    }

    private Vec3d getPos(PathAwareEntity entity) {
        return this.posRetriever.apply(entity.getBrain().getOptionalMemory(this.entityMemory).get());
    }

    private boolean isWalkTargetPresentAndFar(PathAwareEntity entity) {
        Vec3d vec3d2;
        if (!entity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
            return false;
        }
        WalkTarget walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (walkTarget.getSpeed() != this.speed) {
            return false;
        }
        Vec3d vec3d = walkTarget.getLookTarget().getPos().subtract(entity.getPos());
        return vec3d.dotProduct(vec3d2 = this.getPos(entity).subtract(entity.getPos())) < 0.0;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        GoToRememberedPositionTask.setWalkTarget(pathAwareEntity, this.getPos(pathAwareEntity), this.speed);
    }

    private static void setWalkTarget(PathAwareEntity entity, Vec3d pos, float speed) {
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = FuzzyTargeting.findFrom(entity, 16, 7, pos);
            if (vec3d == null) continue;
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, speed, 0));
            return;
        }
    }
}

