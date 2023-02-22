/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public class FlyOntoTreeGoal
extends WanderAroundFarGoal {
    public FlyOntoTreeGoal(PathAwareEntity pathAwareEntity, double d) {
        super(pathAwareEntity, d);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d vec3d = null;
        if (this.mob.isTouchingWater()) {
            vec3d = FuzzyTargeting.find(this.mob, 15, 15);
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3d = this.getTreeTarget();
        }
        return vec3d == null ? super.getWanderTarget() : vec3d;
    }

    @Nullable
    private Vec3d getTreeTarget() {
        BlockPos blockPos = this.mob.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 3.0), MathHelper.floor(this.mob.getY() - 6.0), MathHelper.floor(this.mob.getZ() - 3.0), MathHelper.floor(this.mob.getX() + 3.0), MathHelper.floor(this.mob.getY() + 6.0), MathHelper.floor(this.mob.getZ() + 3.0));
        for (BlockPos blockPos2 : iterable) {
            BlockState blockState;
            boolean bl;
            if (blockPos.equals(blockPos2) || !(bl = (blockState = this.mob.world.getBlockState(mutable2.set((Vec3i)blockPos2, Direction.DOWN))).getBlock() instanceof LeavesBlock || blockState.isIn(BlockTags.LOGS)) || !this.mob.world.isAir(blockPos2) || !this.mob.world.isAir(mutable.set((Vec3i)blockPos2, Direction.UP))) continue;
            return Vec3d.ofBottomCenter(blockPos2);
        }
        return null;
    }
}

