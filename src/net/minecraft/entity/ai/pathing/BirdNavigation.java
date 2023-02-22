/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BirdNavigation
extends EntityNavigation {
    public BirdNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int i) {
        this.nodeMaker = new BirdPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.canSwim() && this.isInLiquid() || !this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.x, this.entity.y, this.entity.z);
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(new BlockPos(entity), distance);
    }

    @Override
    public void tick() {
        Vec3d vec3d;
        ++this.tickCount;
        if (this.shouldRecalculate) {
            this.recalculatePath();
        }
        if (this.isIdle()) {
            return;
        }
        if (this.isAtValidPosition()) {
            this.method_6339();
        } else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
            vec3d = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
            if (MathHelper.floor(this.entity.x) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.entity.y) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.entity.z) == MathHelper.floor(vec3d.z)) {
                this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
            }
        }
        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.field_6683);
        if (this.isIdle()) {
            return;
        }
        vec3d = this.currentPath.getNodePosition(this.entity);
        this.entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor(origin.x);
        int j = MathHelper.floor(origin.y);
        int k = MathHelper.floor(origin.z);
        double d = target.x - origin.x;
        double e = target.y - origin.y;
        double f = target.z - origin.z;
        double g = d * d + e * e + f * f;
        if (g < 1.0E-8) {
            return false;
        }
        double h = 1.0 / Math.sqrt(g);
        double l = 1.0 / Math.abs(d *= h);
        double m = 1.0 / Math.abs(e *= h);
        double n = 1.0 / Math.abs(f *= h);
        double o = (double)i - origin.x;
        double p = (double)j - origin.y;
        double q = (double)k - origin.z;
        if (d >= 0.0) {
            o += 1.0;
        }
        if (e >= 0.0) {
            p += 1.0;
        }
        if (f >= 0.0) {
            q += 1.0;
        }
        o /= d;
        p /= e;
        q /= f;
        int r = d < 0.0 ? -1 : 1;
        int s = e < 0.0 ? -1 : 1;
        int t = f < 0.0 ? -1 : 1;
        int u = MathHelper.floor(target.x);
        int v = MathHelper.floor(target.y);
        int w = MathHelper.floor(target.z);
        int x = u - i;
        int y = v - j;
        int z = w - k;
        while (x * r > 0 || y * s > 0 || z * t > 0) {
            if (o < q && o <= p) {
                o += l;
                x = u - (i += r);
                continue;
            }
            if (p < o && p <= q) {
                p += m;
                y = v - (j += s);
                continue;
            }
            q += n;
            z = w - (k += t);
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean canPathThroughDoors) {
        this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
    }

    public void setCanEnterOpenDoors(boolean canEnterOpenDoors) {
        this.nodeMaker.setCanEnterOpenDoors(canEnterOpenDoors);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        return this.world.getBlockState(pos).hasSolidTopSurface(this.world, pos, this.entity);
    }
}

