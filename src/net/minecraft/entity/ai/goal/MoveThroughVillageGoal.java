/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

public class MoveThroughVillageGoal
extends Goal {
    protected final PathAwareEntity mob;
    private final double speed;
    @Nullable
    private Path targetPath;
    private BlockPos target;
    private final boolean requiresNighttime;
    private final List<BlockPos> visitedTargets = Lists.newArrayList();
    private final int distance;
    private final BooleanSupplier doorPassingThroughGetter;

    public MoveThroughVillageGoal(PathAwareEntity entity, double speed, boolean requiresNighttime, int distance, BooleanSupplier doorPassingThroughGetter) {
        this.mob = entity;
        this.speed = speed;
        this.requiresNighttime = requiresNighttime;
        this.distance = distance;
        this.doorPassingThroughGetter = doorPassingThroughGetter;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        if (!NavigationConditions.hasMobNavigation(entity)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean canStart() {
        if (!NavigationConditions.hasMobNavigation(this.mob)) {
            return false;
        }
        this.forgetOldTarget();
        if (this.requiresNighttime && this.mob.world.isDay()) {
            return false;
        }
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        BlockPos blockPos = this.mob.getBlockPos();
        if (!serverWorld.isNearOccupiedPointOfInterest(blockPos, 6)) {
            return false;
        }
        Vec3d vec3d = FuzzyTargeting.find(this.mob, 15, 7, pos -> {
            if (!serverWorld.isNearOccupiedPointOfInterest((BlockPos)pos)) {
                return Double.NEGATIVE_INFINITY;
            }
            Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(poiType -> poiType.isIn(PointOfInterestTypeTags.VILLAGE), this::shouldVisit, (BlockPos)pos, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
            return optional.map(blockPos2 -> -blockPos2.getSquaredDistance(blockPos)).orElse(Double.NEGATIVE_INFINITY);
        });
        if (vec3d == null) {
            return false;
        }
        Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage().getPosition(poiType -> poiType.isIn(PointOfInterestTypeTags.VILLAGE), this::shouldVisit, new BlockPos(vec3d), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
        if (optional.isEmpty()) {
            return false;
        }
        this.target = optional.get().toImmutable();
        MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
        boolean bl = mobNavigation.canEnterOpenDoors();
        mobNavigation.setCanPathThroughDoors(this.doorPassingThroughGetter.getAsBoolean());
        this.targetPath = mobNavigation.findPathTo(this.target, 0);
        mobNavigation.setCanPathThroughDoors(bl);
        if (this.targetPath == null) {
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.mob, 10, 7, Vec3d.ofBottomCenter(this.target), 1.5707963705062866);
            if (vec3d2 == null) {
                return false;
            }
            mobNavigation.setCanPathThroughDoors(this.doorPassingThroughGetter.getAsBoolean());
            this.targetPath = this.mob.getNavigation().findPathTo(vec3d2.x, vec3d2.y, vec3d2.z, 0);
            mobNavigation.setCanPathThroughDoors(bl);
            if (this.targetPath == null) {
                return false;
            }
        }
        for (int i = 0; i < this.targetPath.getLength(); ++i) {
            PathNode pathNode = this.targetPath.getNode(i);
            BlockPos blockPos2 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
            if (!DoorBlock.isWoodenDoor(this.mob.world, blockPos2)) continue;
            this.targetPath = this.mob.getNavigation().findPathTo(pathNode.x, (double)pathNode.y, pathNode.z, 0);
            break;
        }
        return this.targetPath != null;
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getNavigation().isIdle()) {
            return false;
        }
        return !this.target.isWithinDistance(this.mob.getPos(), (double)(this.mob.getWidth() + (float)this.distance));
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingAlong(this.targetPath, this.speed);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isIdle() || this.target.isWithinDistance(this.mob.getPos(), (double)this.distance)) {
            this.visitedTargets.add(this.target);
        }
    }

    private boolean shouldVisit(BlockPos pos) {
        for (BlockPos blockPos : this.visitedTargets) {
            if (!Objects.equals(pos, blockPos)) continue;
            return false;
        }
        return true;
    }

    private void forgetOldTarget() {
        if (this.visitedTargets.size() > 15) {
            this.visitedTargets.remove(0);
        }
    }
}

