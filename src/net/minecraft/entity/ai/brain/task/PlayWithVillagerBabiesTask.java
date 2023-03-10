/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PlayWithVillagerBabiesTask
extends Task<PathAwareEntity> {
    private static final int HORIZONTAL_RANGE = 20;
    private static final int VERTICAL_RANGE = 8;
    private static final float WALK_SPEED = 0.6f;
    private static final float PLAYING_WALK_SPEED = 0.6f;
    private static final int MAX_BABY_INTERACTION_COUNT = 5;
    private static final int RUN_CHANCE = 10;

    public PlayWithVillagerBabiesTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return serverWorld.getRandom().nextInt(10) == 0 && this.hasVisibleVillagerBabies(pathAwareEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        LivingEntity livingEntity = this.findVisibleVillagerBaby(pathAwareEntity);
        if (livingEntity != null) {
            this.setGroundTarget(serverWorld, pathAwareEntity, livingEntity);
            return;
        }
        Optional<LivingEntity> optional = this.getLeastPopularBabyInteractionTarget(pathAwareEntity);
        if (optional.isPresent()) {
            PlayWithVillagerBabiesTask.setPlayTarget(pathAwareEntity, optional.get());
            return;
        }
        this.getVisibleMob(pathAwareEntity).ifPresent(target -> PlayWithVillagerBabiesTask.setPlayTarget(pathAwareEntity, target));
    }

    private void setGroundTarget(ServerWorld world, PathAwareEntity entity, LivingEntity unusedBaby) {
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = FuzzyTargeting.find(entity, 20, 8);
            if (vec3d == null || !world.isNearOccupiedPointOfInterest(new BlockPos(vec3d))) continue;
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.6f, 0));
            return;
        }
    }

    private static void setPlayTarget(PathAwareEntity entity, LivingEntity target) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.INTERACTION_TARGET, target);
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
        brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(target, false), 0.6f, 1));
    }

    private Optional<LivingEntity> getVisibleMob(PathAwareEntity entity) {
        return this.getVisibleVillagerBabies(entity).stream().findAny();
    }

    private Optional<LivingEntity> getLeastPopularBabyInteractionTarget(PathAwareEntity entity) {
        Map<LivingEntity, Integer> map = this.getBabyInteractionTargetCounts(entity);
        return map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }

    private Map<LivingEntity, Integer> getBabyInteractionTargetCounts(PathAwareEntity entity) {
        HashMap map = Maps.newHashMap();
        this.getVisibleVillagerBabies(entity).stream().filter(this::hasInteractionTarget).forEach(livingEntity2 -> map.compute(this.getInteractionTarget((LivingEntity)livingEntity2), (livingEntity, integer) -> integer == null ? 1 : integer + 1));
        return map;
    }

    private List<LivingEntity> getVisibleVillagerBabies(PathAwareEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
    }

    private LivingEntity getInteractionTarget(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    @Nullable
    private LivingEntity findVisibleVillagerBaby(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get().stream().filter(livingEntity2 -> this.isInteractionTargetOf(entity, (LivingEntity)livingEntity2)).findAny().orElse(null);
    }

    private boolean hasInteractionTarget(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private boolean isInteractionTargetOf(LivingEntity entity, LivingEntity other) {
        return other.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).filter(livingEntity2 -> livingEntity2 == entity).isPresent();
    }

    private boolean hasVisibleVillagerBabies(PathAwareEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }
}

