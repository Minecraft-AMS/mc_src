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
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FleeTask
extends MultiTickTask<PathAwareEntity> {
    private static final int MIN_RUN_TIME = 100;
    private static final int MAX_RUN_TIME = 120;
    private static final int HORIZONTAL_RANGE = 5;
    private static final int VERTICAL_RANGE = 4;
    private static final Predicate<PathAwareEntity> PANIC_PREDICATE = entity -> entity.getAttacker() != null || entity.shouldEscapePowderSnow() || entity.isOnFire();
    private final float speed;
    private final Predicate<PathAwareEntity> predicate;

    public FleeTask(float speed) {
        this(speed, PANIC_PREDICATE);
    }

    public FleeTask(float speed, Predicate<PathAwareEntity> predicate) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.IS_PANICKING, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.HURT_BY, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 100, 120);
        this.speed = speed;
        this.predicate = predicate;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return this.predicate.test(pathAwareEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        pathAwareEntity.getBrain().remember(MemoryModuleType.IS_PANICKING, true);
        pathAwareEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        Brain<?> brain = pathAwareEntity.getBrain();
        brain.forget(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        Vec3d vec3d;
        if (pathAwareEntity.getNavigation().isIdle() && (vec3d = this.findTarget(pathAwareEntity, serverWorld)) != null) {
            pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
        }
    }

    @Nullable
    private Vec3d findTarget(PathAwareEntity entity, ServerWorld world) {
        Optional<Vec3d> optional;
        if (entity.isOnFire() && (optional = this.findClosestWater(world, entity).map(Vec3d::ofBottomCenter)).isPresent()) {
            return optional.get();
        }
        return FuzzyTargeting.find(entity, 5, 4);
    }

    private Optional<BlockPos> findClosestWater(BlockView world, Entity entity) {
        BlockPos blockPos = entity.getBlockPos();
        if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
            return Optional.empty();
        }
        Predicate<BlockPos> predicate = MathHelper.ceil(entity.getWidth()) == 2 ? pos2 -> BlockPos.streamSouthEastSquare(pos2).allMatch(pos -> world.getFluidState((BlockPos)pos).isIn(FluidTags.WATER)) : pos -> world.getFluidState((BlockPos)pos).isIn(FluidTags.WATER);
        return BlockPos.findClosest(blockPos, 5, 1, predicate);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (PathAwareEntity)entity, time);
    }
}

