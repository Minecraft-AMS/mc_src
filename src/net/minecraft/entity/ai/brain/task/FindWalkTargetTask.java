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
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class FindWalkTargetTask
extends Task<MobEntityWithAi> {
    private final float walkSpeed;
    private final int field_19352;
    private final int field_19353;

    public FindWalkTargetTask(float walkSpeed) {
        this(walkSpeed, 10, 7);
    }

    public FindWalkTargetTask(float f, int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.walkSpeed = f;
        this.field_19352 = i;
        this.field_19353 = j;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        BlockPos blockPos = new BlockPos(mobEntityWithAi);
        if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
            this.method_20429(mobEntityWithAi);
        } else {
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
            ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
            if (chunkSectionPos2 != chunkSectionPos) {
                this.method_20430(mobEntityWithAi, chunkSectionPos2);
            } else {
                this.method_20429(mobEntityWithAi);
            }
        }
    }

    private void method_20430(MobEntityWithAi mobEntityWithAi, ChunkSectionPos chunkSectionPos) {
        BlockPos blockPos = chunkSectionPos.getCenterPos();
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.method_6373(mobEntityWithAi, this.field_19352, this.field_19353, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
        mobEntityWithAi.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget((Vec3d)vec3d, this.walkSpeed, 0)));
    }

    private void method_20429(MobEntityWithAi mobEntityWithAi) {
        Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(mobEntityWithAi, this.field_19352, this.field_19353));
        mobEntityWithAi.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget((Vec3d)vec3d, this.walkSpeed, 0)));
    }
}

