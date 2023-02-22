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
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class GoToWorkTask
extends Task<VillagerEntity> {
    public GoToWorkTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        BlockPos blockPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        return blockPos.isWithinDistance(villagerEntity.getPos(), 2.0) || villagerEntity.isNatural();
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        GlobalPos globalPos = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
        villagerEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
        villagerEntity.getBrain().remember(MemoryModuleType.JOB_SITE, globalPos);
        serverWorld.sendEntityStatus(villagerEntity, (byte)14);
        if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.NONE) {
            return;
        }
        MinecraftServer minecraftServer = serverWorld.getServer();
        Optional.ofNullable(minecraftServer.getWorld(globalPos.getDimension())).flatMap(world -> world.getPointOfInterestStorage().getType(globalPos.getPos())).flatMap(registryEntry -> Registry.VILLAGER_PROFESSION.stream().filter(profession -> profession.heldWorkstation().test((RegistryEntry<PointOfInterestType>)registryEntry)).findFirst()).ifPresent(profession -> {
            villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession((VillagerProfession)profession));
            villagerEntity.reinitializeBrain(serverWorld);
        });
    }
}

