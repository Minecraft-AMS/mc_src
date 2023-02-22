/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PiglinSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, (Object[])new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, PiglinSpecificSensor.findPiglinRepellent(world, entity));
        Optional<Object> optional = Optional.empty();
        Optional<Object> optional2 = Optional.empty();
        Optional<Object> optional3 = Optional.empty();
        Optional<Object> optional4 = Optional.empty();
        Optional<Object> optional5 = Optional.empty();
        Optional<Object> optional6 = Optional.empty();
        Optional<Object> optional7 = Optional.empty();
        int i = 0;
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        LivingTargetCache livingTargetCache = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
        for (LivingEntity livingEntity2 : livingTargetCache.iterate(livingEntity -> true)) {
            if (livingEntity2 instanceof HoglinEntity) {
                HoglinEntity hoglinEntity = (HoglinEntity)livingEntity2;
                if (hoglinEntity.isBaby() && optional3.isEmpty()) {
                    optional3 = Optional.of(hoglinEntity);
                    continue;
                }
                if (!hoglinEntity.isAdult()) continue;
                ++i;
                if (!optional2.isEmpty() || !hoglinEntity.canBeHunted()) continue;
                optional2 = Optional.of(hoglinEntity);
                continue;
            }
            if (livingEntity2 instanceof PiglinBruteEntity) {
                PiglinBruteEntity piglinBruteEntity = (PiglinBruteEntity)livingEntity2;
                list.add(piglinBruteEntity);
                continue;
            }
            if (livingEntity2 instanceof PiglinEntity) {
                PiglinEntity piglinEntity = (PiglinEntity)livingEntity2;
                if (piglinEntity.isBaby() && optional4.isEmpty()) {
                    optional4 = Optional.of(piglinEntity);
                    continue;
                }
                if (!piglinEntity.isAdult()) continue;
                list.add(piglinEntity);
                continue;
            }
            if (livingEntity2 instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)livingEntity2;
                if (optional6.isEmpty() && !PiglinBrain.wearsGoldArmor(playerEntity) && entity.canTarget(livingEntity2)) {
                    optional6 = Optional.of(playerEntity);
                }
                if (!optional7.isEmpty() || playerEntity.isSpectator() || !PiglinBrain.isGoldHoldingPlayer(playerEntity)) continue;
                optional7 = Optional.of(playerEntity);
                continue;
            }
            if (optional.isEmpty() && (livingEntity2 instanceof WitherSkeletonEntity || livingEntity2 instanceof WitherEntity)) {
                optional = Optional.of((MobEntity)livingEntity2);
                continue;
            }
            if (!optional5.isEmpty() || !PiglinBrain.isZombified(livingEntity2.getType())) continue;
            optional5 = Optional.of(livingEntity2);
        }
        List<LivingEntity> list3 = brain.getOptionalRegisteredMemory(MemoryModuleType.MOBS).orElse((List<LivingEntity>)ImmutableList.of());
        for (LivingEntity livingEntity2 : list3) {
            AbstractPiglinEntity abstractPiglinEntity;
            if (!(livingEntity2 instanceof AbstractPiglinEntity) || !(abstractPiglinEntity = (AbstractPiglinEntity)livingEntity2).isAdult()) continue;
            list2.add(abstractPiglinEntity);
        }
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional2);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional3);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional5);
        brain.remember(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional6);
        brain.remember(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional7);
        brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list2);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
        brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
        brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
    }

    private static Optional<BlockPos> findPiglinRepellent(ServerWorld world, LivingEntity entity) {
        return BlockPos.findClosest(entity.getBlockPos(), 8, 4, pos -> PiglinSpecificSensor.isPiglinRepellent(world, pos));
    }

    private static boolean isPiglinRepellent(ServerWorld world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        boolean bl = blockState.isIn(BlockTags.PIGLIN_REPELLENTS);
        if (bl && blockState.isOf(Blocks.SOUL_CAMPFIRE)) {
            return CampfireBlock.isLitCampfire(blockState);
        }
        return bl;
    }
}

