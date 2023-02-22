/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SwimAroundGoal
extends WanderAroundGoal {
    public SwimAroundGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
        super(mobEntityWithAi, d, i);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d vec3d = TargetFinder.findTarget(this.mob, 10, 7);
        int i = 0;
        while (vec3d != null && !this.mob.world.getBlockState(new BlockPos(vec3d)).canPlaceAtSide(this.mob.world, new BlockPos(vec3d), BlockPlacementEnvironment.WATER) && i++ < 10) {
            vec3d = TargetFinder.findTarget(this.mob, 10, 7);
        }
        return vec3d;
    }
}

