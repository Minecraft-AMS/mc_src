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
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AquaticStrollTask;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTimerTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.SeekWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class AxolotlBrain {
    private static final UniformIntProvider WALK_TOWARD_ADULT_RANGE = UniformIntProvider.create(5, 16);
    private static final float BREEDING_SPEED = 0.2f;
    private static final float ON_LAND_SPEED = 0.15f;
    private static final float IDLE_SPEED = 0.5f;
    private static final float TARGET_APPROACHING_SPEED = 0.6f;
    private static final float ADULT_FOLLOWING_SPEED = 0.6f;

    protected static Brain<?> create(Brain<AxolotlEntity> brain) {
        AxolotlBrain.addCoreActivities(brain);
        AxolotlBrain.addIdleActivities(brain);
        AxolotlBrain.addFightActivities(brain);
        AxolotlBrain.addPlayDeadActivities(brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addPlayDeadActivities(Brain<AxolotlEntity> brain) {
        brain.setTaskList(Activity.PLAY_DEAD, (ImmutableList<Pair<Integer, Task<AxolotlEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, (Object)new PlayDeadTask()), (Object)Pair.of((Object)1, new ForgetTask<AxolotlEntity>(AxolotlBrain::hasBreedTarget, MemoryModuleType.PLAY_DEAD_TICKS))), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, (Object)((Object)MemoryModuleState.VALUE_PRESENT))), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS));
    }

    private static void addFightActivities(Brain<AxolotlEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 0, (ImmutableList<Task<AxolotlEntity>>)ImmutableList.of(new ForgetAttackTargetTask<AxolotlEntity>(AxolotlEntity::appreciatePlayer), (Object)new RangedApproachTask(AxolotlBrain::getTargetApproachingSpeed), (Object)new MeleeAttackTask(20), new ForgetTask<AxolotlEntity>(AxolotlBrain::hasBreedTarget, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void addCoreActivities(Brain<AxolotlEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<AxolotlEntity>>)ImmutableList.of((Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(), (Object)new PlayDeadTimerTask(), (Object)new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<AxolotlEntity> brain) {
        brain.setTaskList(Activity.IDLE, (ImmutableList<Pair<Integer, Task<AxolotlEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), (Object)Pair.of((Object)1, (Object)new BreedTask(EntityType.AXOLOTL, 0.2f)), (Object)Pair.of((Object)2, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new TemptTask(AxolotlBrain::getTemptedSpeed), (Object)1), (Object)Pair.of(new WalkTowardClosestAdultTask(WALK_TOWARD_ADULT_RANGE, AxolotlBrain::getAdultFollowingSpeed), (Object)1)))), (Object)Pair.of((Object)3, new UpdateAttackTargetTask<AxolotlEntity>(AxolotlBrain::getAttackTarget)), (Object)Pair.of((Object)3, (Object)new SeekWaterTask(6, 0.15f)), (Object)Pair.of((Object)4, new CompositeTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Set<MemoryModuleType<?>>)ImmutableSet.of(), CompositeTask.Order.ORDERED, CompositeTask.RunMode.TRY_ALL, ImmutableList.of((Object)Pair.of((Object)new AquaticStrollTask(0.5f), (Object)2), (Object)Pair.of((Object)new StrollTask(0.15f, false), (Object)2), (Object)Pair.of((Object)new GoTowardsLookTarget(AxolotlBrain::canGoToLookTarget, AxolotlBrain::getTemptedSpeed, 3), (Object)3), (Object)Pair.of(new ConditionalTask<LivingEntity>(Entity::isInsideWaterOrBubbleColumn, new WaitTask(30, 60)), (Object)5), (Object)Pair.of(new ConditionalTask<LivingEntity>(Entity::isOnGround, new WaitTask(200, 400)), (Object)5))))));
    }

    private static boolean canGoToLookTarget(LivingEntity entity) {
        World world = entity.world;
        Optional<LookTarget> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET);
        if (optional.isPresent()) {
            BlockPos blockPos = optional.get().getBlockPos();
            return world.isWater(blockPos) == entity.isInsideWaterOrBubbleColumn();
        }
        return false;
    }

    public static void updateActivities(AxolotlEntity axolotl) {
        Brain<AxolotlEntity> brain = axolotl.getBrain();
        Activity activity = brain.getFirstPossibleNonCoreActivity().orElse(null);
        if (activity != Activity.PLAY_DEAD) {
            brain.resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.PLAY_DEAD, (Object)Activity.FIGHT, (Object)Activity.IDLE));
            if (activity == Activity.FIGHT && brain.getFirstPossibleNonCoreActivity().orElse(null) != Activity.FIGHT) {
                brain.remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
            }
        }
    }

    private static float getTargetApproachingSpeed(LivingEntity entity) {
        return entity.isInsideWaterOrBubbleColumn() ? 0.6f : 0.15f;
    }

    private static float getAdultFollowingSpeed(LivingEntity entity) {
        return entity.isInsideWaterOrBubbleColumn() ? 0.6f : 0.15f;
    }

    private static float getTemptedSpeed(LivingEntity entity) {
        return entity.isInsideWaterOrBubbleColumn() ? 0.5f : 0.15f;
    }

    private static Optional<? extends LivingEntity> getAttackTarget(AxolotlEntity axolotl) {
        if (AxolotlBrain.hasBreedTarget(axolotl)) {
            return Optional.empty();
        }
        return axolotl.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE);
    }

    private static boolean hasBreedTarget(AxolotlEntity axolotl) {
        return axolotl.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
    }

    public static Ingredient getTemptItems() {
        return Ingredient.fromTag(ItemTags.AXOLOTL_TEMPT_ITEMS);
    }
}

