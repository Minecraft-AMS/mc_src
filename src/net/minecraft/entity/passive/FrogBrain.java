/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AquaticStrollTask;
import net.minecraft.entity.ai.brain.task.BiasedLongJumpTask;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.CroakTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FrogEatEntityTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LayFrogSpawnTask;
import net.minecraft.entity.ai.brain.task.LeapingChargeTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLandTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsWaterTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;

public class FrogBrain {
    private static final float field_37469 = 2.0f;
    private static final float field_37470 = 1.0f;
    private static final float field_37471 = 1.0f;
    private static final float field_37472 = 1.0f;
    private static final float field_37473 = 0.75f;
    private static final UniformIntProvider longJumpCooldownRange = UniformIntProvider.create(100, 140);
    private static final int field_37475 = 2;
    private static final int field_37476 = 4;
    private static final float field_37477 = 1.5f;
    private static final float field_37478 = 1.25f;

    protected static void coolDownLongJump(FrogEntity frog, Random random) {
        frog.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, longJumpCooldownRange.get(random));
    }

    protected static Brain<?> create(Brain<FrogEntity> brain) {
        FrogBrain.addCoreActivities(brain);
        FrogBrain.addIdleActivities(brain);
        FrogBrain.addSwimActivities(brain);
        FrogBrain.addLaySpawnActivities(brain);
        FrogBrain.addTongueActivities(brain);
        FrogBrain.addLongJumpActivities(brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<FrogEntity>>)ImmutableList.of((Object)new WalkTask(2.0f), (Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(), (Object)new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), (Object)new TemptationCooldownTask(MemoryModuleType.LONG_JUMP_COOLING_DOWN)));
    }

    private static void addIdleActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.IDLE, (ImmutableList<Pair<Integer, Task<FrogEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), (Object)Pair.of((Object)0, (Object)new BreedTask(EntityType.FROG, 1.0f)), (Object)Pair.of((Object)1, (Object)new TemptTask(frog -> Float.valueOf(1.25f))), (Object)Pair.of((Object)2, new UpdateAttackTargetTask<FrogEntity>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))), (Object)Pair.of((Object)3, (Object)new WalkTowardsLandTask(6, 1.0f)), (Object)Pair.of((Object)4, new RandomTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), ImmutableList.of((Object)Pair.of((Object)new StrollTask(1.0f), (Object)1), (Object)Pair.of((Object)new GoTowardsLookTarget(1.0f, 3), (Object)1), (Object)Pair.of((Object)new CroakTask(), (Object)3), (Object)Pair.of(new ConditionalTask<LivingEntity>(Entity::isOnGround, new WaitTask(5, 20)), (Object)2))))), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Object)Pair.of(MemoryModuleType.IS_IN_WATER, (Object)((Object)MemoryModuleState.VALUE_ABSENT))));
    }

    private static void addSwimActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.SWIM, (ImmutableList<Pair<Integer, Task<FrogEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), (Object)Pair.of((Object)1, (Object)new TemptTask(frog -> Float.valueOf(1.25f))), (Object)Pair.of((Object)2, new UpdateAttackTargetTask<FrogEntity>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))), (Object)Pair.of((Object)3, (Object)new WalkTowardsLandTask(8, 1.5f)), (Object)Pair.of((Object)5, new CompositeTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Set<MemoryModuleType<?>>)ImmutableSet.of(), CompositeTask.Order.ORDERED, CompositeTask.RunMode.TRY_ALL, ImmutableList.of((Object)Pair.of((Object)new AquaticStrollTask(0.75f), (Object)1), (Object)Pair.of((Object)new StrollTask(1.0f, true), (Object)1), (Object)Pair.of((Object)new GoTowardsLookTarget(1.0f, 3), (Object)1), (Object)Pair.of(new ConditionalTask<LivingEntity>(Entity::isInsideWaterOrBubbleColumn, new WaitTask(30, 60)), (Object)5))))), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Object)Pair.of(MemoryModuleType.IS_IN_WATER, (Object)((Object)MemoryModuleState.VALUE_PRESENT))));
    }

    private static void addLaySpawnActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.LAY_SPAWN, (ImmutableList<Pair<Integer, Task<FrogEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), (Object)Pair.of((Object)1, new UpdateAttackTargetTask<FrogEntity>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))), (Object)Pair.of((Object)2, (Object)new WalkTowardsWaterTask(8, 1.0f)), (Object)Pair.of((Object)3, (Object)new LayFrogSpawnTask(Blocks.FROGSPAWN, MemoryModuleType.IS_PREGNANT)), (Object)Pair.of((Object)4, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new StrollTask(1.0f), (Object)2), (Object)Pair.of((Object)new GoTowardsLookTarget(1.0f, 3), (Object)1), (Object)Pair.of((Object)new CroakTask(), (Object)2), (Object)Pair.of(new ConditionalTask<LivingEntity>(Entity::isOnGround, new WaitTask(5, 20)), (Object)1))))), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Object)Pair.of(MemoryModuleType.IS_PREGNANT, (Object)((Object)MemoryModuleState.VALUE_PRESENT))));
    }

    private static void addLongJumpActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.LONG_JUMP, (ImmutableList<Pair<Integer, Task<FrogEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, (Object)new LeapingChargeTask(longJumpCooldownRange, SoundEvents.ENTITY_FROG_STEP)), (Object)Pair.of((Object)1, new BiasedLongJumpTask<FrogEntity>(longJumpCooldownRange, 2, 4, 1.5f, frog -> SoundEvents.ENTITY_FROG_LONG_JUMP, BlockTags.FROG_PREFER_JUMP_TO, 0.5f, state -> state.isOf(Blocks.LILY_PAD)))), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.TEMPTING_PLAYER, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Object)Pair.of(MemoryModuleType.BREED_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Object)Pair.of(MemoryModuleType.LONG_JUMP_COOLING_DOWN, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Object)Pair.of(MemoryModuleType.IS_IN_WATER, (Object)((Object)MemoryModuleState.VALUE_ABSENT))));
    }

    private static void addTongueActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.TONGUE, 0, (ImmutableList<Task<FrogEntity>>)ImmutableList.of(new ForgetAttackTargetTask(), (Object)new FrogEatEntityTask(SoundEvents.ENTITY_FROG_TONGUE, SoundEvents.ENTITY_FROG_EAT)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isNotBreeding(FrogEntity frog) {
        return !LookTargetUtil.hasBreedTarget(frog);
    }

    public static void updateActivities(FrogEntity frog) {
        frog.getBrain().resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.TONGUE, (Object)Activity.LAY_SPAWN, (Object)Activity.LONG_JUMP, (Object)Activity.SWIM, (Object)Activity.IDLE));
    }

    public static Ingredient getTemptItems() {
        return FrogEntity.SLIME_BALL;
    }
}

