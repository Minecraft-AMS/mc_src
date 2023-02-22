/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.CollisionView;

public class BreatheAirGoal
extends Goal {
    private final MobEntityWithAi mob;

    public BreatheAirGoal(MobEntityWithAi mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.mob.getAir() < 140;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        this.moveToAir();
    }

    private void moveToAir() {
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.x - 1.0), MathHelper.floor(this.mob.y), MathHelper.floor(this.mob.z - 1.0), MathHelper.floor(this.mob.x + 1.0), MathHelper.floor(this.mob.y + 8.0), MathHelper.floor(this.mob.z + 1.0));
        Vec3i blockPos = null;
        for (BlockPos blockPos2 : iterable) {
            if (!this.isAirPos(this.mob.world, blockPos2)) continue;
            blockPos = blockPos2;
            break;
        }
        if (blockPos == null) {
            blockPos = new BlockPos(this.mob.x, this.mob.y + 8.0, this.mob.z);
        }
        this.mob.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), 1.0);
    }

    @Override
    public void tick() {
        this.moveToAir();
        this.mob.updateVelocity(0.02f, new Vec3d(this.mob.sidewaysSpeed, this.mob.upwardSpeed, this.mob.forwardSpeed));
        this.mob.move(MovementType.SELF, this.mob.getVelocity());
    }

    private boolean isAirPos(CollisionView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return (world.getFluidState(pos).isEmpty() || blockState.getBlock() == Blocks.BUBBLE_COLUMN) && blockState.canPlaceAtSide(world, pos, BlockPlacementEnvironment.LAND);
    }
}

