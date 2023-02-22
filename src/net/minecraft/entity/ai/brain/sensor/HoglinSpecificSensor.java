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
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class HoglinSpecificSensor
extends Sensor<HoglinEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object[])new MemoryModuleType[0]);
    }

    @Override
    protected void sense(ServerWorld serverWorld, HoglinEntity hoglinEntity) {
        Brain<HoglinEntity> brain = hoglinEntity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestWarpedFungus(serverWorld, hoglinEntity));
        Optional<Object> optional = Optional.empty();
        int i = 0;
        ArrayList list = Lists.newArrayList();
        LivingTargetCache livingTargetCache = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
        for (LivingEntity livingEntity2 : livingTargetCache.iterate(livingEntity -> !livingEntity.isBaby() && (livingEntity instanceof PiglinEntity || livingEntity instanceof HoglinEntity))) {
            if (livingEntity2 instanceof PiglinEntity) {
                PiglinEntity piglinEntity = (PiglinEntity)livingEntity2;
                ++i;
                if (optional.isEmpty()) {
                    optional = Optional.of(piglinEntity);
                }
            }
            if (!(livingEntity2 instanceof HoglinEntity)) continue;
            HoglinEntity hoglinEntity2 = (HoglinEntity)livingEntity2;
            list.add(hoglinEntity2);
        }
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, list);
        brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, i);
        brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, list.size());
    }

    private Optional<BlockPos> findNearestWarpedFungus(ServerWorld world, HoglinEntity hoglin) {
        return BlockPos.findClosest(hoglin.getBlockPos(), 8, 4, pos -> world.getBlockState((BlockPos)pos).isIn(BlockTags.HOGLIN_REPELLENTS));
    }
}

