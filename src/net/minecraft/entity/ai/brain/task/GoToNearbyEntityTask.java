/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class GoToNearbyEntityTask
extends Task<MobEntityWithAi> {
    private final MemoryModuleType<? extends Entity> entityMemory;
    private final float speed;

    public GoToNearbyEntityTask(MemoryModuleType<? extends Entity> entityMemory, float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), entityMemory, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityMemory = entityMemory;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        Entity entity = mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
        return mobEntityWithAi.squaredDistanceTo(entity) < 36.0;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        Entity entity = mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
        GoToNearbyEntityTask.setWalkTarget(mobEntityWithAi, entity, this.speed);
    }

    public static void setWalkTarget(MobEntityWithAi entity, Entity target, float speed) {
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = TargetFinder.findGroundTargetAwayFrom(entity, 16, 7, target.getPos());
            if (vec3d == null) continue;
            entity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, speed, 0));
            return;
        }
    }
}

