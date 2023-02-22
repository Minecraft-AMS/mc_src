/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundPointOfInterestGoal
extends WanderAroundGoal {
    public WanderAroundPointOfInterestGoal(MobEntityWithAi mobEntityWithAi, double d) {
        super(mobEntityWithAi, d, 10);
    }

    @Override
    public boolean canStart() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos = new BlockPos(this.mob);
        if (serverWorld.isNearOccupiedPointOfInterest(blockPos)) {
            return false;
        }
        return super.canStart();
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos = new BlockPos(this.mob);
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
        ChunkSectionPos chunkSectionPos2 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(serverWorld, chunkSectionPos, 2);
        if (chunkSectionPos2 != chunkSectionPos) {
            return TargetFinder.findTargetTowards(this.mob, 10, 7, new Vec3d(chunkSectionPos2.getCenterPos()));
        }
        return null;
    }
}

