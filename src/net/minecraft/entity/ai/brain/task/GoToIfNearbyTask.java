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
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class GoToIfNearbyTask
extends Task<PathAwareEntity> {
    private static final int UPDATE_INTERVAL = 180;
    private static final int HORIZONTAL_RANGE = 8;
    private static final int VERTICAL_RANGE = 6;
    private final MemoryModuleType<GlobalPos> target;
    private long nextUpdateTime;
    private final int maxDistance;
    private final float walkSpeed;

    public GoToIfNearbyTask(MemoryModuleType<GlobalPos> target, float walkSpeed, int maxDistance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), target, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.target = target;
        this.walkSpeed = walkSpeed;
        this.maxDistance = maxDistance;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        Optional<GlobalPos> optional = pathAwareEntity.getBrain().getOptionalMemory(this.target);
        return optional.isPresent() && serverWorld.getRegistryKey() == optional.get().getDimension() && optional.get().getPos().isWithinDistance(pathAwareEntity.getPos(), (double)this.maxDistance);
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        if (l > this.nextUpdateTime) {
            Optional<Vec3d> optional = Optional.ofNullable(FuzzyTargeting.find(pathAwareEntity, 8, 6));
            pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(pos -> new WalkTarget((Vec3d)pos, this.walkSpeed, 1)));
            this.nextUpdateTime = l + 180L;
        }
    }
}

