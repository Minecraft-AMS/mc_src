/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VillagerWalkTowardsTask
extends Task<VillagerEntity> {
    private final MemoryModuleType<GlobalPos> destination;
    private final float speed;
    private final int completionRange;
    private final int maxRange;
    private final int maxRunTime;

    public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> destination, float speed, int completionRange, int maxRange, int maxRunTime) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), destination, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.destination = destination;
        this.speed = speed;
        this.completionRange = completionRange;
        this.maxRange = maxRange;
        this.maxRunTime = maxRunTime;
    }

    private void giveUp(VillagerEntity villager, long time) {
        Brain<VillagerEntity> brain = villager.getBrain();
        villager.releaseTicketFor(this.destination);
        brain.forget(this.destination);
        brain.putMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        brain.getOptionalMemory(this.destination).ifPresent(globalPos -> {
            if (this.shouldGiveUp(serverWorld, villagerEntity)) {
                this.giveUp(villagerEntity, l);
            } else if (this.exceedsMaxRange(serverWorld, villagerEntity, (GlobalPos)globalPos)) {
                int i;
                Vec3d vec3d = null;
                int j = 1000;
                for (i = 0; i < 1000 && (vec3d == null || this.exceedsMaxRange(serverWorld, villagerEntity, GlobalPos.create(villagerEntity.dimension, new BlockPos(vec3d)))); ++i) {
                    vec3d = TargetFinder.findTargetTowards(villagerEntity, 15, 7, new Vec3d(globalPos.getPos()));
                }
                if (i == 1000) {
                    this.giveUp(villagerEntity, l);
                    return;
                }
                brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, this.completionRange));
            } else if (!this.reachedDestination(serverWorld, villagerEntity, (GlobalPos)globalPos)) {
                brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), this.speed, this.completionRange));
            }
        });
    }

    private boolean shouldGiveUp(ServerWorld world, VillagerEntity villager) {
        Optional<Long> optional = villager.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        if (optional.isPresent()) {
            return world.getTime() - optional.get() > (long)this.maxRunTime;
        }
        return false;
    }

    private boolean exceedsMaxRange(ServerWorld world, VillagerEntity villager, GlobalPos pos) {
        return pos.getDimension() != world.getDimension().getType() || pos.getPos().getManhattanDistance(new BlockPos(villager)) > this.maxRange;
    }

    private boolean reachedDestination(ServerWorld world, VillagerEntity villager, GlobalPos pos) {
        return pos.getDimension() == world.getDimension().getType() && pos.getPos().getManhattanDistance(new BlockPos(villager)) <= this.completionRange;
    }
}

