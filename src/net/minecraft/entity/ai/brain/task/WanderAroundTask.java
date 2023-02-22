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
    private Path field_18369;
    @Nullable
    private BlockPos field_18370;
    private float field_18371;
    private int field_18964;

    public WanderAroundTask(int runTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.PATH, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), runTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        Brain<?> brain = mobEntity.getBrain();
        WalkTarget walkTarget = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (!this.method_18980(mobEntity, walkTarget) && this.method_18977(mobEntity, walkTarget, serverWorld.getTime())) {
            this.field_18370 = walkTarget.getLookTarget().getBlockPos();
            return true;
        }
        brain.forget(MemoryModuleType.WALK_TARGET);
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (this.field_18369 == null || this.field_18370 == null) {
            return false;
        }
        Optional<WalkTarget> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
        EntityNavigation entityNavigation = mobEntity.getNavigation();
        return !entityNavigation.isIdle() && optional.isPresent() && !this.method_18980(mobEntity, optional.get());
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getNavigation().stop();
        mobEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        mobEntity.getBrain().forget(MemoryModuleType.PATH);
        this.field_18369 = null;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        mobEntity.getBrain().putMemory(MemoryModuleType.PATH, this.field_18369);
        mobEntity.getNavigation().startMovingAlong(this.field_18369, this.field_18371);
        this.field_18964 = serverWorld.getRandom().nextInt(10);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        --this.field_18964;
        if (this.field_18964 > 0) {
            return;
        }
        Path path = mobEntity.getNavigation().getCurrentPath();
        Brain<?> brain = mobEntity.getBrain();
        if (this.field_18369 != path) {
            this.field_18369 = path;
            brain.putMemory(MemoryModuleType.PATH, path);
        }
        if (path == null || this.field_18370 == null) {
            return;
        }
        WalkTarget walkTarget = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (walkTarget.getLookTarget().getBlockPos().getSquaredDistance(this.field_18370) > 4.0 && this.method_18977(mobEntity, walkTarget, serverWorld.getTime())) {
            this.field_18370 = walkTarget.getLookTarget().getBlockPos();
            this.run(serverWorld, mobEntity, l);
        }
    }

    private boolean method_18977(MobEntity mobEntity, WalkTarget walkTarget, long l) {
        BlockPos blockPos = walkTarget.getLookTarget().getBlockPos();
        this.field_18369 = mobEntity.getNavigation().findPathTo(blockPos, 0);
        this.field_18371 = walkTarget.getSpeed();
        if (!this.method_18980(mobEntity, walkTarget)) {
            boolean bl;
            Brain<Long> brain = mobEntity.getBrain();
            boolean bl2 = bl = this.field_18369 != null && this.field_18369.method_21655();
            if (bl) {
                brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
            } else if (!brain.hasMemoryModule(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                brain.putMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, l);
            }
            if (this.field_18369 != null) {
                return true;
            }
            Vec3d vec3d = TargetFinder.method_6373((MobEntityWithAi)mobEntity, 10, 7, new Vec3d(blockPos));
            if (vec3d != null) {
                this.field_18369 = mobEntity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.field_18369 != null;
            }
        }
        return false;
    }

    private boolean method_18980(MobEntity mobEntity, WalkTarget walkTarget) {
        return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(new BlockPos(mobEntity)) <= walkTarget.getCompletionRange();
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

