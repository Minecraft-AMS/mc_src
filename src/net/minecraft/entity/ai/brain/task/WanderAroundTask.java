/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundTask
extends Task<MobEntity> {
    @Nullable
    private Path path;
    @Nullable
    private BlockPos lookTargetPos;
    private float speed;
    private int pathUpdateCountdownTicks;

    public WanderAroundTask(int runTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.PATH, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), runTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        Brain<?> brain = mobEntity.getBrain();
        WalkTarget walkTarget = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (!this.hasReached(mobEntity, walkTarget) && this.hasFinishedPath(mobEntity, walkTarget, serverWorld.getTime())) {
            this.lookTargetPos = walkTarget.getLookTarget().getBlockPos();
            return true;
        }
        brain.forget(MemoryModuleType.WALK_TARGET);
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (this.path == null || this.lookTargetPos == null) {
            return false;
        }
        Optional<WalkTarget> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
        EntityNavigation entityNavigation = mobEntity.getNavigation();
        return !entityNavigation.isIdle() && optional.isPresent() && !this.hasReached(mobEntity, optional.get());
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getNavigation().stop();
        mobEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        mobEntity.getBrain().forget(MemoryModuleType.PATH);
        this.path = null;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getBrain().putMemory(MemoryModuleType.PATH, this.path);
        mobEntity.getNavigation().startMovingAlong(this.path, this.speed);
        this.pathUpdateCountdownTicks = serverWorld.getRandom().nextInt(10);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        --this.pathUpdateCountdownTicks;
        if (this.pathUpdateCountdownTicks > 0) {
            return;
        }
        Path path = mobEntity.getNavigation().getCurrentPath();
        Brain<?> brain = mobEntity.getBrain();
        if (this.path != path) {
            this.path = path;
            brain.putMemory(MemoryModuleType.PATH, path);
        }
        if (path == null || this.lookTargetPos == null) {
            return;
        }
        WalkTarget walkTarget = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (walkTarget.getLookTarget().getBlockPos().getSquaredDistance(this.lookTargetPos) > 4.0 && this.hasFinishedPath(mobEntity, walkTarget, serverWorld.getTime())) {
            this.lookTargetPos = walkTarget.getLookTarget().getBlockPos();
            this.run(serverWorld, mobEntity, l);
        }
    }

    private boolean hasFinishedPath(MobEntity entity, WalkTarget walkTarget, long time) {
        BlockPos blockPos = walkTarget.getLookTarget().getBlockPos();
        this.path = entity.getNavigation().findPathTo(blockPos, 0);
        this.speed = walkTarget.getSpeed();
        if (!this.hasReached(entity, walkTarget)) {
            boolean bl;
            Brain<Long> brain = entity.getBrain();
            boolean bl2 = bl = this.path != null && this.path.reachesTarget();
            if (bl) {
                brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
            } else if (!brain.hasMemoryModule(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                brain.putMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);
            }
            if (this.path != null) {
                return true;
            }
            Vec3d vec3d = TargetFinder.findTargetTowards((MobEntityWithAi)entity, 10, 7, new Vec3d(blockPos));
            if (vec3d != null) {
                this.path = entity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.path != null;
            }
        }
        return false;
    }

    private boolean hasReached(MobEntity entity, WalkTarget walkTarget) {
        return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(new BlockPos(entity)) <= walkTarget.getCompletionRange();
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (MobEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (MobEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (MobEntity)entity, time);
    }
}

