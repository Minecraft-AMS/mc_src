/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask
extends MultiTickTask<MobEntity> {
    private final float chance;

    public StayAboveWaterTask(float chance) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
        this.chance = chance;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        return mobEntity.isTouchingWater() && mobEntity.getFluidHeight(FluidTags.WATER) > mobEntity.getSwimHeight() || mobEntity.isInLava();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        return this.shouldRun(serverWorld, mobEntity);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        if (mobEntity.getRandom().nextFloat() < this.chance) {
            mobEntity.getJumpControl().setActive();
        }
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (MobEntity)entity, time);
    }
}

