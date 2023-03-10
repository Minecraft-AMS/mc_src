/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.WaterPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SwimNavigation
extends EntityNavigation {
    private boolean canJumpOutOfWater;

    public SwimNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.canJumpOutOfWater = this.entity.getType() == EntityType.DOLPHIN;
        this.nodeMaker = new WaterPathNodeMaker(this.canJumpOutOfWater);
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.canJumpOutOfWater || this.isInLiquid();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.entity.getBodyY(0.5), this.entity.getZ());
    }

    @Override
    protected double adjustTargetY(Vec3d pos) {
        return pos.y;
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
        Vec3d vec3d = new Vec3d(target.x, target.y + (double)this.entity.getHeight() * 0.5, target.z);
        return this.world.raycast(new RaycastContext(origin, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this.entity)).getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        return !this.world.getBlockState(pos).isOpaqueFullCube(this.world, pos);
    }

    @Override
    public void setCanSwim(boolean canSwim) {
    }
}

