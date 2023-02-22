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
    private final float field_18381;

    public GoToNearbyEntityTask(MemoryModuleType<? extends Entity> memoryModuleType, float f) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), memoryModuleType, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.entityMemory = memoryModuleType;
        this.field_18381 = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        Entity entity = mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
        return mobEntityWithAi.squaredDistanceTo(entity) < 36.0;
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        Entity entity = mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
        GoToNearbyEntityTask.method_19596(mobEntityWithAi, entity, this.field_18381);
    }

    public static void method_19596(MobEntityWithAi mobEntityWithAi, Entity entity, float f) {
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = new Vec3d(entity.x, entity.y, entity.z);
            Vec3d vec3d2 = TargetFinder.method_20658(mobEntityWithAi, 16, 7, vec3d);
            if (vec3d2 == null) continue;
            mobEntityWithAi.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d2, f, 0));
            return;
        }
    }
}

