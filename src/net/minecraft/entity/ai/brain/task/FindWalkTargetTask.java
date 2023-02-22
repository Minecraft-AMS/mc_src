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
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask
extends Task<PathAwareEntity> {
    private static final int MIN_RUN_TIME = 10;
    private static final int MAX_RUN_TIME = 7;
    private final float walkSpeed;
    private final int maxHorizontalDistance;
    private final int maxVerticalDistance;

    public FindWalkTargetTask(float walkSpeed) {
        this(walkSpeed, 10, 7);
    }

    public FindWalkTargetTask(float walkSpeed, int maxHorizontalDistance, int maxVerticalDistance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.walkSpeed = walkSpeed;
        this.maxHorizontalDistance = maxHorizontalDistance;
        this.maxVerticalDistance = maxVerticalDistance;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        BlockPos blockPos = pathAwareEntity.getBlockPos();
        if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
            this.updateWalkTarget(pathAwareEntity);
        } else {
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
            ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
            if (chunkSectionPos2 != chunkSectionPos) {
                this.updateWalkTarget(pathAwareEntity, chunkSectionPos2);
            } else {
                this.updateWalkTarget(pathAwareEntity);
            }
        }
    }

    private void updateWalkTarget(PathAwareEntity entity, ChunkSectionPos pos2) {
        Optional<Vec3d> optional = Optional.ofNullable(NoPenaltyTargeting.findTo(entity, this.maxHorizontalDistance, this.maxVerticalDistance, Vec3d.ofBottomCenter(pos2.getCenterPos()), 1.5707963705062866));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(pos -> new WalkTarget((Vec3d)pos, this.walkSpeed, 0)));
    }

    private void updateWalkTarget(PathAwareEntity entity) {
        Optional<Vec3d> optional = Optional.ofNullable(FuzzyTargeting.find(entity, this.maxHorizontalDistance, this.maxVerticalDistance));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(pos -> new WalkTarget((Vec3d)pos, this.walkSpeed, 0)));
    }
}

