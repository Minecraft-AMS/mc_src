/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public abstract class EntityNavigation {
    protected final MobEntity entity;
    protected final World world;
    @Nullable
    protected Path currentPath;
    protected double speed;
    protected int tickCount;
    protected int pathStartTime;
    protected Vec3d pathStartPos = Vec3d.ZERO;
    protected Vec3i lastNodePosition = Vec3i.ZERO;
    protected long currentNodeMs;
    protected long lastActiveTickMs;
    protected double currentNodeTimeout;
    protected float nodeReachProximity = 0.5f;
    protected boolean shouldRecalculate;
    protected long lastRecalculateTime;
    protected PathNodeMaker nodeMaker;
    private BlockPos currentTarget;
    private int currentDistance;
    private float rangeMultiplier = 1.0f;
    private final PathNodeNavigator pathNodeNavigator;
    private boolean nearPathStartPos;

    public EntityNavigation(MobEntity mob, World world) {
        this.entity = mob;
        this.world = world;
        int i = MathHelper.floor(mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 16.0);
        this.pathNodeNavigator = this.createPathNodeNavigator(i);
    }

    public void resetRangeMultiplier() {
        this.rangeMultiplier = 1.0f;
    }

    public void setRangeMultiplier(float rangeMultiplier) {
        this.rangeMultiplier = rangeMultiplier;
    }

    public BlockPos getTargetPos() {
        return this.currentTarget;
    }

    protected abstract PathNodeNavigator createPathNodeNavigator(int var1);

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean shouldRecalculatePath() {
        return this.shouldRecalculate;
    }

    public void recalculatePath() {
        if (this.world.getTime() - this.lastRecalculateTime > 20L) {
            if (this.currentTarget != null) {
                this.currentPath = null;
                this.currentPath = this.findPathTo(this.currentTarget, this.currentDistance);
                this.lastRecalculateTime = this.world.getTime();
                this.shouldRecalculate = false;
            }
        } else {
            this.shouldRecalculate = true;
        }
    }

    @Nullable
    public final Path findPathTo(double x, double y, double z, int distance) {
        return this.findPathTo(new BlockPos(x, y, z), distance);
    }

    @Nullable
    public Path findPathToAny(Stream<BlockPos> positions, int distance) {
        return this.findPathToAny(positions.collect(Collectors.toSet()), 8, false, distance);
    }

    @Nullable
    public Path method_29934(Set<BlockPos> set, int i) {
        return this.findPathToAny(set, 8, false, i);
    }

    @Nullable
    public Path findPathTo(BlockPos target, int distance) {
        return this.findPathToAny((Set<BlockPos>)ImmutableSet.of((Object)target), 8, false, distance);
    }

    @Nullable
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathToAny((Set<BlockPos>)ImmutableSet.of((Object)entity.getBlockPos()), 16, true, distance);
    }

    @Nullable
    protected Path findPathToAny(Set<BlockPos> positions, int range, boolean bl, int distance) {
        if (positions.isEmpty()) {
            return null;
        }
        if (this.entity.getY() < 0.0) {
            return null;
        }
        if (!this.isAtValidPosition()) {
            return null;
        }
        if (this.currentPath != null && !this.currentPath.isFinished() && positions.contains(this.currentTarget)) {
            return this.currentPath;
        }
        this.world.getProfiler().push("pathfind");
        float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        BlockPos blockPos = bl ? this.entity.getBlockPos().up() : this.entity.getBlockPos();
        int i = (int)(f + (float)range);
        ChunkCache chunkCache = new ChunkCache(this.world, blockPos.add(-i, -i, -i), blockPos.add(i, i, i));
        Path path = this.pathNodeNavigator.findPathToAny(chunkCache, this.entity, positions, f, distance, this.rangeMultiplier);
        this.world.getProfiler().pop();
        if (path != null && path.getTarget() != null) {
            this.currentTarget = path.getTarget();
            this.currentDistance = distance;
            this.resetNode();
        }
        return path;
    }

    public boolean startMovingTo(double x, double y, double z, double speed) {
        return this.startMovingAlong(this.findPathTo(x, y, z, 1), speed);
    }

    public boolean startMovingTo(Entity entity, double speed) {
        Path path = this.findPathTo(entity, 1);
        return path != null && this.startMovingAlong(path, speed);
    }

    public boolean startMovingAlong(@Nullable Path path, double speed) {
        if (path == null) {
            this.currentPath = null;
            return false;
        }
        if (!path.equalsPath(this.currentPath)) {
            this.currentPath = path;
        }
        if (this.isIdle()) {
            return false;
        }
        this.adjustPath();
        if (this.currentPath.getLength() <= 0) {
            return false;
        }
        this.speed = speed;
        Vec3d vec3d = this.getPos();
        this.pathStartTime = this.tickCount;
        this.pathStartPos = vec3d;
        return true;
    }

    @Nullable
    public Path getCurrentPath() {
        return this.currentPath;
    }

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
            this.continueFollowingPath();
        } else if (this.currentPath != null && !this.currentPath.isFinished()) {
            vec3d = this.getPos();
            Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity);
            if (vec3d.y > vec3d2.y && !this.entity.isOnGround() && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
                this.currentPath.next();
            }
        }
        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
        if (this.isIdle()) {
            return;
        }
        vec3d = this.currentPath.getNodePosition(this.entity);
        BlockPos blockPos = new BlockPos(vec3d);
        this.entity.getMoveControl().moveTo(vec3d.x, this.world.getBlockState(blockPos.down()).isAir() ? vec3d.y : LandPathNodeMaker.getFeetY(this.world, blockPos), vec3d.z, this.speed);
    }

    protected void continueFollowingPath() {
        boolean bl;
        Vec3d vec3d = this.getPos();
        this.nodeReachProximity = this.entity.getWidth() > 0.75f ? this.entity.getWidth() / 2.0f : 0.75f - this.entity.getWidth() / 2.0f;
        BlockPos vec3i = this.currentPath.method_31032();
        double d = Math.abs(this.entity.getX() - ((double)vec3i.getX() + 0.5));
        double e = Math.abs(this.entity.getY() - (double)vec3i.getY());
        double f = Math.abs(this.entity.getZ() - ((double)vec3i.getZ() + 0.5));
        boolean bl2 = bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity && e < 1.0;
        if (bl || this.entity.method_29244(this.currentPath.method_29301().type) && this.method_27799(vec3d)) {
            this.currentPath.next();
        }
        this.checkTimeouts(vec3d);
    }

    private boolean method_27799(Vec3d vec3d) {
        Vec3d vec3d5;
        if (this.currentPath.getCurrentNodeIndex() + 1 >= this.currentPath.getLength()) {
            return false;
        }
        Vec3d vec3d2 = Vec3d.ofBottomCenter(this.currentPath.method_31032());
        if (!vec3d.isInRange(vec3d2, 2.0)) {
            return false;
        }
        Vec3d vec3d3 = Vec3d.ofBottomCenter(this.currentPath.method_31031(this.currentPath.getCurrentNodeIndex() + 1));
        Vec3d vec3d4 = vec3d3.subtract(vec3d2);
        return vec3d4.dotProduct(vec3d5 = vec3d.subtract(vec3d2)) > 0.0;
    }

    protected void checkTimeouts(Vec3d currentPos) {
        if (this.tickCount - this.pathStartTime > 100) {
            if (currentPos.squaredDistanceTo(this.pathStartPos) < 2.25) {
                this.nearPathStartPos = true;
                this.stop();
            } else {
                this.nearPathStartPos = false;
            }
            this.pathStartTime = this.tickCount;
            this.pathStartPos = currentPos;
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            BlockPos vec3i = this.currentPath.method_31032();
            if (vec3i.equals(this.lastNodePosition)) {
                this.currentNodeMs += Util.getMeasuringTimeMs() - this.lastActiveTickMs;
            } else {
                this.lastNodePosition = vec3i;
                double d = currentPos.distanceTo(Vec3d.ofBottomCenter(this.lastNodePosition));
                double d2 = this.currentNodeTimeout = this.entity.getMovementSpeed() > 0.0f ? d / (double)this.entity.getMovementSpeed() * 1000.0 : 0.0;
            }
            if (this.currentNodeTimeout > 0.0 && (double)this.currentNodeMs > this.currentNodeTimeout * 3.0) {
                this.resetNodeAndStop();
            }
            this.lastActiveTickMs = Util.getMeasuringTimeMs();
        }
    }

    private void resetNodeAndStop() {
        this.resetNode();
        this.stop();
    }

    private void resetNode() {
        this.lastNodePosition = Vec3i.ZERO;
        this.currentNodeMs = 0L;
        this.currentNodeTimeout = 0.0;
        this.nearPathStartPos = false;
    }

    public boolean isIdle() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public boolean isFollowingPath() {
        return !this.isIdle();
    }

    public void stop() {
        this.currentPath = null;
    }

    protected abstract Vec3d getPos();

    protected abstract boolean isAtValidPosition();

    protected boolean isInLiquid() {
        return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isInLava();
    }

    protected void adjustPath() {
        if (this.currentPath == null) {
            return;
        }
        for (int i = 0; i < this.currentPath.getLength(); ++i) {
            PathNode pathNode = this.currentPath.getNode(i);
            PathNode pathNode2 = i + 1 < this.currentPath.getLength() ? this.currentPath.getNode(i + 1) : null;
            BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
            if (!blockState.isOf(Blocks.CAULDRON)) continue;
            this.currentPath.setNode(i, pathNode.copyWithNewPosition(pathNode.x, pathNode.y + 1, pathNode.z));
            if (pathNode2 == null || pathNode.y < pathNode2.y) continue;
            this.currentPath.setNode(i + 1, pathNode.copyWithNewPosition(pathNode2.x, pathNode.y + 1, pathNode2.z));
        }
    }

    protected abstract boolean canPathDirectlyThrough(Vec3d var1, Vec3d var2, int var3, int var4, int var5);

    public boolean isValidPosition(BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.world.getBlockState(blockPos).isOpaqueFullCube(this.world, blockPos);
    }

    public PathNodeMaker getNodeMaker() {
        return this.nodeMaker;
    }

    public void setCanSwim(boolean canSwim) {
        this.nodeMaker.setCanSwim(canSwim);
    }

    public boolean canSwim() {
        return this.nodeMaker.canSwim();
    }

    public void onBlockChanged(BlockPos pos) {
        if (this.currentPath == null || this.currentPath.isFinished() || this.currentPath.getLength() == 0) {
            return;
        }
        PathNode pathNode = this.currentPath.getEnd();
        Vec3d vec3d = new Vec3d(((double)pathNode.x + this.entity.getX()) / 2.0, ((double)pathNode.y + this.entity.getY()) / 2.0, ((double)pathNode.z + this.entity.getZ()) / 2.0);
        if (pos.isWithinDistance(vec3d, (double)(this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex()))) {
            this.recalculatePath();
        }
    }

    public boolean isNearPathStartPos() {
        return this.nearPathStartPos;
    }
}

