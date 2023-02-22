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
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Timestamp;

public class VillagerWorkTask
extends Task<VillagerEntity> {
    private long lastCheckedTime;

    public VillagerWorkTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
            return false;
        }
        if (serverWorld.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = serverWorld.getTime();
        GlobalPos globalPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        return Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType()) && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        brain.putMemory(MemoryModuleType.LAST_WORKED_AT_POI, Timestamp.of(l));
        brain.getOptionalMemory(MemoryModuleType.JOB_SITE).ifPresent(globalPos -> brain.putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
        villagerEntity.playWorkSound();
        if (villagerEntity.shouldRestock()) {
            villagerEntity.restock();
        }
    }
}

