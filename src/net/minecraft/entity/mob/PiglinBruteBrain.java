/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.dynamic.GlobalPos;

public class PiglinBruteBrain {
    protected static Brain<?> create(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
        PiglinBruteBrain.method_30257(piglinBrute, brain);
        PiglinBruteBrain.method_30260(piglinBrute, brain);
        PiglinBruteBrain.method_30262(piglinBrute, brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    protected static void method_30250(PiglinBruteEntity piglinBruteEntity) {
        GlobalPos globalPos = GlobalPos.create(piglinBruteEntity.world.getRegistryKey(), piglinBruteEntity.getBlockPos());
        piglinBruteEntity.getBrain().remember(MemoryModuleType.HOME, globalPos);
    }

    private static void method_30257(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(), (Object)new OpenDoorsTask(), new ForgetAngryAtTargetTask()));
    }

    private static void method_30260(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of(new UpdateAttackTargetTask<PiglinBruteEntity>(PiglinBruteBrain::method_30247), PiglinBruteBrain.method_30244(), PiglinBruteBrain.method_30254(), (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)));
    }

    private static void method_30262(PiglinBruteEntity piglinBruteEntity, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of(new ForgetAttackTargetTask(livingEntity -> !PiglinBruteBrain.method_30248(piglinBruteEntity, livingEntity)), (Object)new RangedApproachTask(1.0f), (Object)new MeleeAttackTask(20)), MemoryModuleType.ATTACK_TARGET);
    }

    private static RandomTask<PiglinBruteEntity> method_30244() {
        return new RandomTask<PiglinBruteEntity>((List<Pair<Task<PiglinBruteEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(EntityType.PIGLIN_BRUTE, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(8.0f), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    private static RandomTask<PiglinBruteEntity> method_30254() {
        return new RandomTask<PiglinBruteEntity>((List<Pair<Task<PiglinBruteEntity>, Integer>>)ImmutableList.of((Object)Pair.of((Object)new StrollTask(0.6f), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of((Object)new GoToNearbyPositionTask(MemoryModuleType.HOME, 0.6f, 2, 100), (Object)2), (Object)Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.HOME, 0.6f, 5), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    protected static void method_30256(PiglinBruteEntity piglinBruteEntity) {
        Brain<PiglinBruteEntity> brain = piglinBruteEntity.getBrain();
        Activity activity = brain.getFirstPossibleNonCoreActivity().orElse(null);
        brain.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity activity2 = brain.getFirstPossibleNonCoreActivity().orElse(null);
        if (activity != activity2) {
            PiglinBruteBrain.method_30261(piglinBruteEntity);
        }
        piglinBruteEntity.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean method_30248(AbstractPiglinEntity abstractPiglinEntity, LivingEntity livingEntity) {
        return PiglinBruteBrain.method_30247(abstractPiglinEntity).filter(livingEntity2 -> livingEntity2 == livingEntity).isPresent();
    }

    private static Optional<? extends LivingEntity> method_30247(AbstractPiglinEntity abstractPiglinEntity) {
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(abstractPiglinEntity, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && PiglinBruteBrain.method_30245(optional.get())) {
            return optional;
        }
        Optional<? extends LivingEntity> optional2 = PiglinBruteBrain.method_30249(abstractPiglinEntity, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        if (optional2.isPresent()) {
            return optional2;
        }
        return abstractPiglinEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    }

    private static boolean method_30245(LivingEntity livingEntity) {
        return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity);
    }

    private static Optional<? extends LivingEntity> method_30249(AbstractPiglinEntity abstractPiglinEntity, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
        return abstractPiglinEntity.getBrain().getOptionalMemory(memoryModuleType).filter(livingEntity -> livingEntity.isInRange(abstractPiglinEntity, 12.0));
    }

    protected static void method_30251(PiglinBruteEntity piglinBruteEntity, LivingEntity livingEntity) {
        if (livingEntity instanceof AbstractPiglinEntity) {
            return;
        }
        PiglinBrain.tryRevenge(piglinBruteEntity, livingEntity);
    }

    protected static void method_30258(PiglinBruteEntity piglinBruteEntity) {
        if ((double)piglinBruteEntity.world.random.nextFloat() < 0.0125) {
            PiglinBruteBrain.method_30261(piglinBruteEntity);
        }
    }

    private static void method_30261(PiglinBruteEntity piglinBruteEntity) {
        piglinBruteEntity.getBrain().getFirstPossibleNonCoreActivity().ifPresent(activity -> {
            if (activity == Activity.FIGHT) {
                piglinBruteEntity.playAngrySound();
            }
        });
    }
}

