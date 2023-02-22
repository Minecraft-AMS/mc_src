/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class GoToIfNearbyTask
extends Task<MobEntityWithAi> {
    private final MemoryModuleType<GlobalPos> target;
    private long nextUpdateTime;
    private final int maxDistance;

    public GoToIfNearbyTask(MemoryModuleType<GlobalPos> target, int maxDistance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), target, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.target = target;
        this.maxDistance = maxDistance;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getOptionalMemory(this.target);
        return optional.isPresent() && Objects.equals(serverWorld.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().isWithinDistance(mobEntityWithAi.getPos(), (double)this.maxDistance);
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        if (l > this.nextUpdateTime) {
            Optional<Vec3d> optional = Optional.ofNullable(TargetFinder.findGroundTarget(mobEntityWithAi, 8, 6));
            mobEntityWithAi.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget((Vec3d)vec3d, 0.4f, 1)));
            this.nextUpdateTime = l + 180L;
        }
    }
}

