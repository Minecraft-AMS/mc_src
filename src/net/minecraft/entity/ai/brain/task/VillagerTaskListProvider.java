/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BoneMealTask;
import net.minecraft.entity.ai.brain.task.CelebrateRaidWinTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.EndRaidTask;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.ai.brain.task.FarmerWorkTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.entity.ai.brain.task.FollowCustomerTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetBellRingTask;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.GatherItemsVillagerTask;
import net.minecraft.entity.ai.brain.task.GiveGiftsToHeroTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.GoToPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoToSecondaryPositionTask;
import net.minecraft.entity.ai.brain.task.GoToWorkTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.HideInHomeDuringRaidTask;
import net.minecraft.entity.ai.brain.task.HideInHomeTask;
import net.minecraft.entity.ai.brain.task.HideWhenBellRingsTask;
import net.minecraft.entity.ai.brain.task.HoldTradeOffersTask;
import net.minecraft.entity.ai.brain.task.JumpInBedTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LoseJobOnSiteLossTask;
import net.minecraft.entity.ai.brain.task.MeetVillagerTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.PlayWithVillagerBabiesTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RingBellTask;
import net.minecraft.entity.ai.brain.task.RunAroundAfterRaidTask;
import net.minecraft.entity.ai.brain.task.ScheduleActivityTask;
import net.minecraft.entity.ai.brain.task.SeekSkyAfterRaidWinTask;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.entity.ai.brain.task.StartRaidTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StopPanickingTask;
import net.minecraft.entity.ai.brain.task.TakeJobSiteTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerBreedTask;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.ai.brain.task.VillagerWorkTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WakeUpTask;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WalkTowardJobSiteTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.brain.task.WanderIndoorsTask;
import net.minecraft.entity.ai.brain.task.WorkStationCompetitionTask;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerTaskListProvider {
    private static final float field_30189 = 0.4f;

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createCoreTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new StayAboveWaterTask(0.8f)), (Object)Pair.of((Object)0, (Object)new OpenDoorsTask()), (Object)Pair.of((Object)0, (Object)new LookAroundTask(45, 90)), (Object)Pair.of((Object)0, (Object)new PanicTask()), (Object)Pair.of((Object)0, (Object)new WakeUpTask()), (Object)Pair.of((Object)0, (Object)new HideWhenBellRingsTask()), (Object)Pair.of((Object)0, (Object)new StartRaidTask()), (Object)Pair.of((Object)0, (Object)new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)), (Object)Pair.of((Object)0, (Object)new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)), (Object)Pair.of((Object)1, (Object)new WanderAroundTask()), (Object)Pair.of((Object)2, (Object)new WorkStationCompetitionTask(profession)), (Object)Pair.of((Object)3, (Object)new FollowCustomerTask(speed)), (Object[])new Pair[]{Pair.of((Object)5, new WalkToNearestVisibleWantedItemTask(speed, false, 4)), Pair.of((Object)6, (Object)new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), Pair.of((Object)7, (Object)new WalkTowardJobSiteTask(speed)), Pair.of((Object)8, (Object)new TakeJobSiteTask(speed)), Pair.of((Object)10, (Object)new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))), Pair.of((Object)10, (Object)new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of((Object)10, (Object)new GoToWorkTask()), Pair.of((Object)10, (Object)new LoseJobOnSiteLossTask())});
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createWorkTasks(VillagerProfession profession, float speed) {
        VillagerWorkTask villagerWorkTask = profession == VillagerProfession.FARMER ? new FarmerWorkTask() : new VillagerWorkTask();
        return ImmutableList.of(VillagerTaskListProvider.createBusyFollowTask(), (Object)Pair.of((Object)5, new RandomTask(ImmutableList.of((Object)Pair.of((Object)villagerWorkTask, (Object)7), (Object)Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4f, 4), (Object)2), (Object)Pair.of((Object)new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), (Object)5), (Object)Pair.of((Object)new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), (Object)5), (Object)Pair.of((Object)new FarmerVillagerTask(), (Object)(profession == VillagerProfession.FARMER ? 2 : 5)), (Object)Pair.of((Object)new BoneMealTask(), (Object)(profession == VillagerProfession.FARMER ? 4 : 7))))), (Object)Pair.of((Object)10, (Object)new HoldTradeOffersTask(400, 1600)), (Object)Pair.of((Object)10, (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)), (Object)Pair.of((Object)2, (Object)new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)), (Object)Pair.of((Object)3, (Object)new GiveGiftsToHeroTask(100)), (Object)Pair.of((Object)99, (Object)new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPlayTasks(float speed) {
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new WanderAroundTask(80, 120)), VillagerTaskListProvider.createFreeFollowTask(), (Object)Pair.of((Object)5, (Object)new PlayWithVillagerBabiesTask()), (Object)Pair.of((Object)5, new RandomTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), ImmutableList.of((Object)Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), (Object)2), (Object)Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), (Object)1), (Object)Pair.of((Object)new FindWalkTargetTask(speed), (Object)1), (Object)Pair.of((Object)new GoTowardsLookTarget(speed, 2), (Object)1), (Object)Pair.of((Object)new JumpInBedTask(speed), (Object)2), (Object)Pair.of((Object)new WaitTask(20, 40), (Object)2)))), (Object)Pair.of((Object)99, (Object)new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRestTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of((Object)Pair.of((Object)2, (Object)new VillagerWalkTowardsTask(MemoryModuleType.HOME, speed, 1, 150, 1200)), (Object)Pair.of((Object)3, (Object)new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)), (Object)Pair.of((Object)3, (Object)new SleepTask()), (Object)Pair.of((Object)5, new RandomTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_ABSENT)), ImmutableList.of((Object)Pair.of((Object)new WalkHomeTask(speed), (Object)1), (Object)Pair.of((Object)new WanderIndoorsTask(speed), (Object)4), (Object)Pair.of((Object)new GoToPointOfInterestTask(speed, 4), (Object)2), (Object)Pair.of((Object)new WaitTask(20, 40), (Object)2)))), VillagerTaskListProvider.createBusyFollowTask(), (Object)Pair.of((Object)99, (Object)new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createMeetTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of((Object)Pair.of((Object)2, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 0.4f, 40), (Object)2), (Object)Pair.of((Object)new MeetVillagerTask(), (Object)2)))), (Object)Pair.of((Object)10, (Object)new HoldTradeOffersTask(400, 1600)), (Object)Pair.of((Object)10, (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)), (Object)Pair.of((Object)2, (Object)new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed, 6, 100, 200)), (Object)Pair.of((Object)3, (Object)new GiveGiftsToHeroTask(100)), (Object)Pair.of((Object)3, (Object)new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)), (Object)Pair.of((Object)3, new CompositeTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of((Object)Pair.of((Object)new GatherItemsVillagerTask(), (Object)1)))), VillagerTaskListProvider.createFreeFollowTask(), (Object)Pair.of((Object)99, (Object)new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createIdleTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of((Object)Pair.of((Object)2, new RandomTask(ImmutableList.of((Object)Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), (Object)2), (Object)Pair.of(new FindEntityTask<VillagerEntity, PassiveEntity>(EntityType.VILLAGER, 8, PassiveEntity::isReadyToBreed, PassiveEntity::isReadyToBreed, MemoryModuleType.BREED_TARGET, speed, 2), (Object)1), (Object)Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), (Object)1), (Object)Pair.of((Object)new FindWalkTargetTask(speed), (Object)1), (Object)Pair.of((Object)new GoTowardsLookTarget(speed, 2), (Object)1), (Object)Pair.of((Object)new JumpInBedTask(speed), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)1)))), (Object)Pair.of((Object)3, (Object)new GiveGiftsToHeroTask(100)), (Object)Pair.of((Object)3, (Object)new FindInteractionTargetTask(EntityType.PLAYER, 4)), (Object)Pair.of((Object)3, (Object)new HoldTradeOffersTask(400, 1600)), (Object)Pair.of((Object)3, new CompositeTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of((Object)Pair.of((Object)new GatherItemsVillagerTask(), (Object)1)))), (Object)Pair.of((Object)3, new CompositeTask((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(), (Set<MemoryModuleType<?>>)ImmutableSet.of(MemoryModuleType.BREED_TARGET), CompositeTask.Order.ORDERED, CompositeTask.RunMode.RUN_ONE, ImmutableList.of((Object)Pair.of((Object)new VillagerBreedTask(), (Object)1)))), VillagerTaskListProvider.createFreeFollowTask(), (Object)Pair.of((Object)99, (Object)new ScheduleActivityTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPanicTasks(VillagerProfession profession, float speed) {
        float f = speed * 1.5f;
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new StopPanickingTask()), (Object)Pair.of((Object)1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)), (Object)Pair.of((Object)1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)), (Object)Pair.of((Object)3, (Object)new FindWalkTargetTask(f, 2, 2)), VillagerTaskListProvider.createBusyFollowTask());
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createPreRaidTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new RingBellTask()), (Object)Pair.of((Object)0, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, speed * 1.5f, 2, 150, 200), (Object)6), (Object)Pair.of((Object)new FindWalkTargetTask(speed * 1.5f), (Object)2)))), VillagerTaskListProvider.createBusyFollowTask(), (Object)Pair.of((Object)99, (Object)new EndRaidTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createRaidTasks(VillagerProfession profession, float speed) {
        return ImmutableList.of((Object)Pair.of((Object)0, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new SeekSkyAfterRaidWinTask(speed), (Object)5), (Object)Pair.of((Object)new RunAroundAfterRaidTask(speed * 1.1f), (Object)2)))), (Object)Pair.of((Object)0, (Object)new CelebrateRaidWinTask(600, 600)), (Object)Pair.of((Object)2, (Object)new HideInHomeDuringRaidTask(24, speed * 1.4f)), VillagerTaskListProvider.createBusyFollowTask(), (Object)Pair.of((Object)99, (Object)new EndRaidTask()));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createHideTasks(VillagerProfession profession, float speed) {
        int i = 2;
        return ImmutableList.of((Object)Pair.of((Object)0, (Object)new ForgetBellRingTask(15, 3)), (Object)Pair.of((Object)1, (Object)new HideInHomeTask(32, speed * 1.25f, 2)), VillagerTaskListProvider.createBusyFollowTask());
    }

    private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
        return Pair.of((Object)5, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.CAT, 8.0f), (Object)8), (Object)Pair.of((Object)new FollowMobTask(EntityType.VILLAGER, 8.0f), (Object)2), (Object)Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)2), (Object)Pair.of((Object)new FollowMobTask(SpawnGroup.CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(SpawnGroup.AXOLOTLS, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0f), (Object)1), (Object)Pair.of((Object)new FollowMobTask(SpawnGroup.MONSTER, 8.0f), (Object)1), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)2))));
    }

    private static Pair<Integer, Task<LivingEntity>> createBusyFollowTask() {
        return Pair.of((Object)5, new RandomTask(ImmutableList.of((Object)Pair.of((Object)new FollowMobTask(EntityType.VILLAGER, 8.0f), (Object)2), (Object)Pair.of((Object)new FollowMobTask(EntityType.PLAYER, 8.0f), (Object)2), (Object)Pair.of((Object)new WaitTask(30, 60), (Object)8))));
    }
}

