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
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PlayWithVillagerBabiesTask
extends Task<MobEntityWithAi> {
    public PlayWithVillagerBabiesTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        return serverWorld.getRandom().nextInt(10) == 0 && this.hasVisibleVillagerBabies(mobEntityWithAi);
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        LivingEntity livingEntity2 = this.findVisibleVillagerBaby(mobEntityWithAi);
        if (livingEntity2 != null) {
            this.method_19585(serverWorld, mobEntityWithAi, livingEntity2);
            return;
        }
        Optional<LivingEntity> optional = this.method_19588(mobEntityWithAi);
        if (optional.isPresent()) {
            PlayWithVillagerBabiesTask.method_19580(mobEntityWithAi, optional.get());
            return;
        }
        this.getVisibleMob(mobEntityWithAi).ifPresent(livingEntity -> PlayWithVillagerBabiesTask.method_19580(mobEntityWithAi, livingEntity));
    }

    private void method_19585(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = TargetFinder.findGroundTarget(mobEntityWithAi, 20, 8);
            if (vec3d == null || !serverWorld.isNearOccupiedPointOfInterest(new BlockPos(vec3d))) continue;
            mobEntityWithAi.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.6f, 0));
            return;
        }
    }

    private static void method_19580(MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
        Brain<?> brain = mobEntityWithAi.getBrain();
        brain.putMemory(MemoryModuleType.INTERACTION_TARGET, livingEntity);
        brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntity));
        brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(livingEntity), 0.6f, 1));
    }

    private Optional<LivingEntity> getVisibleMob(MobEntityWithAi entity) {
        return this.getVisibleVillagerBabies(entity).stream().findAny();
    }

    private Optional<LivingEntity> method_19588(MobEntityWithAi mobEntityWithAi) {
        Map<LivingEntity, Integer> map = this.method_19592(mobEntityWithAi);
        return map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> (Integer)entry.getValue() > 0 && (Integer)entry.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }

    private Map<LivingEntity, Integer> method_19592(MobEntityWithAi mobEntityWithAi) {
        HashMap map = Maps.newHashMap();
        this.getVisibleVillagerBabies(mobEntityWithAi).stream().filter(this::hasInteractionTarget).forEach(livingEntity2 -> map.compute(this.getInteractionTarget((LivingEntity)livingEntity2), (livingEntity, integer) -> integer == null ? 1 : integer + 1));
        return map;
    }

    private List<LivingEntity> getVisibleVillagerBabies(MobEntityWithAi entity) {
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

    private boolean hasVisibleVillagerBabies(MobEntityWithAi entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }
}

