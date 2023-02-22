/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class WalkHomeTask
extends Task<LivingEntity> {
    private static final int POI_EXPIRY = 40;
    private static final int MAX_TRIES = 5;
    private static final int RUN_TIME = 20;
    private static final int MAX_DISTANCE = 4;
    private final float speed;
    private final Long2LongMap positionToExpiry = new Long2LongOpenHashMap();
    private int tries;
    private long expiryTimeLimit;

    public WalkHomeTask(float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        if (world.getTime() - this.expiryTimeLimit < 20L) {
            return false;
        }
        PathAwareEntity pathAwareEntity = (PathAwareEntity)entity;
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        return optional.isPresent() && !(optional.get().getSquaredDistance(pathAwareEntity.getBlockPos()) <= 4.0);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Predicate<BlockPos> predicate;
        this.tries = 0;
        this.expiryTimeLimit = world.getTime() + (long)world.getRandom().nextInt(20);
        PathAwareEntity pathAwareEntity = (PathAwareEntity)entity;
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = pointOfInterestStorage.getTypesAndPositions(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), predicate = pos -> {
            long l = pos.asLong();
            if (this.positionToExpiry.containsKey(l)) {
                return false;
            }
            if (++this.tries >= 5) {
                return false;
            }
            this.positionToExpiry.put(l, this.expiryTimeLimit + 40L);
            return true;
        }, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toSet());
        Path path = FindPointOfInterestTask.findPathToPoi(pathAwareEntity, set);
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos = path.getTarget();
            Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(blockPos);
            if (optional.isPresent()) {
                entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, this.speed, 1));
                DebugInfoSender.sendPointOfInterest(world, blockPos);
            }
        } else if (this.tries < 5) {
            this.positionToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTimeLimit);
        }
    }
}

