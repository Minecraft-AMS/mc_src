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
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
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
import net.minecraft.util.math.GlobalPos;

public class PiglinBruteBrain {
    private static final int field_30589 = 600;
    private static final int field_30590 = 20;
    private static final double field_30591 = 0.0125;
    private static final int field_30592 = 8;
    private static final int field_30593 = 8;
    private static final double TARGET_RANGE = 12.0;
    private static final float field_30595 = 0.6f;
    private static final int field_30596 = 2;
    private static final int field_30597 = 100;
    private static final int field_30598 = 5;

    protected static Brain<?> create(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
        PiglinBruteBrain.addCoreActivities(piglinBrute, brain);
        PiglinBruteBrain.addIdleActivities(piglinBrute, brain);
        PiglinBruteBrain.addFightActivities(piglinBrute, brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    protected static void setCurrentPosAsHome(PiglinBruteEntity piglinBrute) {
        GlobalPos globalPos = GlobalPos.create(piglinBrute.world.getRegistryKey(), piglinBrute.getBlockPos());
        piglinBrute.getBrain().remember(MemoryModuleType.HOME, globalPos);
    }

    private static void addCoreActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(), OpenDoorsTask.create(), ForgetAngryAtTargetTask.create()));
    }

    private static void addIdleActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of(UpdateAttackTargetTask.create(PiglinBruteBrain::getTarget), PiglinBruteBrain.getFollowTasks(), PiglinBruteBrain.getIdleTasks(), FindInteractionTargetTask.create(EntityType.PLAYER, 4)));
    }

    private static void addFightActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, (ImmutableList<Task<PiglinBruteEntity>>)ImmutableList.of(ForgetAttackTargetTask.create(entity -> !PiglinBruteBrain.isTarget(piglinBrute, entity)), RangedApproachTask.create(1.0f), MeleeAttackTask.create(20)), MemoryModuleType.ATTACK_TARGET);
    }

    private static RandomTask<PiglinBruteEntity> getFollowTasks() {
        return new RandomTask<PiglinBruteEntity>((List<Pair<Task<PiglinBruteEntity>, Integer>>)ImmutableList.of((Object)Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0f), (Object)1), (Object)Pair.of(LookAtMobTask.create(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of(LookAtMobTask.create(EntityType.PIGLIN_BRUTE, 8.0f), (Object)1), (Object)Pair.of(LookAtMobTask.create(8.0f), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    private static RandomTask<PiglinBruteEntity> getIdleTasks() {
        return new RandomTask<PiglinBruteEntity>((List<Pair<Task<PiglinBruteEntity>, Integer>>)ImmutableList.of((Object)Pair.of(StrollTask.create(0.6f), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(GoToNearbyPositionTask.create(MemoryModuleType.HOME, 0.6f, 2, 100), (Object)2), (Object)Pair.of(GoToIfNearbyTask.create(MemoryModuleType.HOME, 0.6f, 5), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)));
    }

    protected static void tick(PiglinBruteEntity piglinBrute) {
        Brain<PiglinBruteEntity> brain = piglinBrute.getBrain();
        Activity activity = brain.getFirstPossibleNonCoreActivity().orElse(null);
        brain.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity activity2 = brain.getFirstPossibleNonCoreActivity().orElse(null);
        if (activity != activity2) {
            PiglinBruteBrain.playSoundIfAngry(piglinBrute);
        }
        piglinBrute.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean isTarget(AbstractPiglinEntity piglin, LivingEntity entity) {
        return PiglinBruteBrain.getTarget(piglin).filter(target -> target == entity).isPresent();
    }

    private static Optional<? extends LivingEntity> getTarget(AbstractPiglinEntity piglin) {
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility(piglin, optional.get())) {
            return optional;
        }
        Optional<? extends LivingEntity> optional2 = PiglinBruteBrain.getTargetIfInRange(piglin, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
        if (optional2.isPresent()) {
            return optional2;
        }
        return piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    }

    private static Optional<? extends LivingEntity> getTargetIfInRange(AbstractPiglinEntity piglin, MemoryModuleType<? extends LivingEntity> targetMemoryModule) {
        return piglin.getBrain().getOptionalRegisteredMemory(targetMemoryModule).filter(target -> target.isInRange(piglin, 12.0));
    }

    protected static void tryRevenge(PiglinBruteEntity piglinBrute, LivingEntity target) {
        if (target instanceof AbstractPiglinEntity) {
            return;
        }
        PiglinBrain.tryRevenge(piglinBrute, target);
    }

    protected static void setTarget(PiglinBruteEntity piglinBrute, LivingEntity target) {
        piglinBrute.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        piglinBrute.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);
    }

    protected static void playSoundRandomly(PiglinBruteEntity piglinBrute) {
        if ((double)piglinBrute.world.random.nextFloat() < 0.0125) {
            PiglinBruteBrain.playSoundIfAngry(piglinBrute);
        }
    }

    private static void playSoundIfAngry(PiglinBruteEntity piglinBrute) {
        piglinBrute.getBrain().getFirstPossibleNonCoreActivity().ifPresent(activity -> {
            if (activity == Activity.FIGHT) {
                piglinBrute.playAngrySound();
            }
        });
    }
}

