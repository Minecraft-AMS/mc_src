/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class MoveIntoWaterGoal
extends Goal {
    private final MobEntityWithAi mob;

    public MoveIntoWaterGoal(MobEntityWithAi mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        return this.mob.onGround && !this.mob.world.getFluidState(new BlockPos(this.mob)).matches(FluidTags.WATER);
    }

    @Override
    public void start() {
        Vec3i blockPos = null;
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.x - 2.0), MathHelper.floor(this.mob.y - 2.0), MathHelper.floor(this.mob.z - 2.0), MathHelper.floor(this.mob.x + 2.0), MathHelper.floor(this.mob.y), MathHelper.floor(this.mob.z + 2.0));
        for (BlockPos blockPos2 : iterable) {
            if (!this.mob.world.getFluidState(blockPos2).matches(FluidTags.WATER)) continue;
            blockPos = blockPos2;
            break;
        }
        if (blockPos != null) {
            this.mob.getMoveControl().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
        }
    }
}

