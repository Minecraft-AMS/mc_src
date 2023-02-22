/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class GoToWorkTask
extends Task<VillagerEntity> {
    public GoToWorkTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return villagerEntity.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        GlobalPos globalPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
        MinecraftServer minecraftServer = serverWorld.getServer();
        minecraftServer.getWorld(globalPos.getDimension()).getPointOfInterestStorage().getType(globalPos.getPos()).ifPresent(pointOfInterestType -> Registry.VILLAGER_PROFESSION.stream().filter(villagerProfession -> villagerProfession.getWorkStation() == pointOfInterestType).findFirst().ifPresent(villagerProfession -> {
            villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession((VillagerProfession)villagerProfession));
            villagerEntity.reinitializeBrain(serverWorld);
        }));
    }
}

