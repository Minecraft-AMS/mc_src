/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SecondaryPointsOfInterestSensor
extends Sensor<VillagerEntity> {
    public SecondaryPointsOfInterestSensor() {
        super(40);
    }

    @Override
    protected void sense(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        DimensionType dimensionType = serverWorld.getDimension().getType();
        BlockPos blockPos = new BlockPos(villagerEntity);
        ArrayList list = Lists.newArrayList();
        int i = 4;
        for (int j = -4; j <= 4; ++j) {
            for (int k = -2; k <= 2; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    BlockPos blockPos2 = blockPos.add(j, k, l);
                    if (!villagerEntity.getVillagerData().getProfession().getSecondaryJobSites().contains((Object)serverWorld.getBlockState(blockPos2).getBlock())) continue;
                    list.add(GlobalPos.create(dimensionType, blockPos2));
                }
            }
        }
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        if (!list.isEmpty()) {
            brain.putMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
        } else {
            brain.forget(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}
