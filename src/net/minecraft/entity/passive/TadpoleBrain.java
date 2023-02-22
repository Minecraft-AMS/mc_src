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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.FollowMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.WalkTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class TadpoleBrain {
    private static final float field_37500 = 2.0f;
    private static final float field_37502 = 0.5f;
    private static final float field_39409 = 1.25f;

    protected static Brain<?> create(Brain<TadpoleEntity> brain) {
        TadpoleBrain.addCoreActivities(brain);
        TadpoleBrain.addIdleActivities(brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<TadpoleEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, (ImmutableList<Task<TadpoleEntity>>)ImmutableList.of((Object)new WalkTask(2.0f), (Object)new LookAroundTask(45, 90), (Object)new WanderAroundTask(), (Object)new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<TadpoleEntity> brain) {
        brain.setTaskList(Activity.IDLE, (ImmutableList<Pair<Integer, Task<TadpoleEntity>>>)ImmutableList.of((Object)Pair.of((Object)0, FollowMobWithIntervalTask.follow(EntityType.PLAYER, 6.0f, UniformIntProvider.create(30, 60))), (Object)Pair.of((Object)1, (Object)new TemptTask(livingEntity -> Float.valueOf(1.25f))), (Object)Pair.of((Object)2, new CompositeTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), (Set<MemoryModuleType<?>>)ImmutableSet.of(), CompositeTask.Order.ORDERED, CompositeTask.RunMode.TRY_ALL, ImmutableList.of((Object)Pair.of(StrollTask.createDynamicRadius(0.5f), (Object)2), (Object)Pair.of(GoTowardsLookTargetTask.create(0.5f, 3), (Object)3), (Object)Pair.of(TaskTriggerer.predicate(Entity::isInsideWaterOrBubbleColumn), (Object)5))))));
    }

    public static void updateActivities(TadpoleEntity tadpole) {
        tadpole.getBrain().resetPossibleActivities((List<Activity>)ImmutableList.of((Object)Activity.IDLE));
    }
}

