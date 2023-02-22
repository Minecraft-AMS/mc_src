/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerBreedTask
extends Task<VillagerEntity> {
    private long field_18368;

    public VillagerBreedTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.BREED_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 350, 350);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return this.method_18972(villagerEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return l <= this.field_18368 && this.method_18972(villagerEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
        serverWorld.sendEntityStatus(villagerEntity2, (byte)18);
        serverWorld.sendEntityStatus(villagerEntity, (byte)18);
        int i = 275 + villagerEntity.getRandom().nextInt(50);
        this.field_18368 = l + (long)i;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
        if (villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
        if (l >= this.field_18368) {
            villagerEntity.eatForBreeding();
            villagerEntity2.eatForBreeding();
            this.method_20643(serverWorld, villagerEntity, villagerEntity2);
        } else if (villagerEntity.getRandom().nextInt(35) == 0) {
            serverWorld.sendEntityStatus(villagerEntity2, (byte)12);
            serverWorld.sendEntityStatus(villagerEntity, (byte)12);
        }
    }

    private void method_20643(ServerWorld serverWorld, VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
        Optional<BlockPos> optional = this.method_19573(serverWorld, villagerEntity);
        if (!optional.isPresent()) {
            serverWorld.sendEntityStatus(villagerEntity2, (byte)13);
            serverWorld.sendEntityStatus(villagerEntity, (byte)13);
        } else {
            Optional<VillagerEntity> optional2 = this.method_18970(villagerEntity, villagerEntity2);
            if (optional2.isPresent()) {
                this.method_19572(serverWorld, optional2.get(), optional.get());
            } else {
                serverWorld.getPointOfInterestStorage().releaseTicket(optional.get());
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
    }

    private VillagerEntity method_19570(VillagerEntity villagerEntity) {
        return villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean method_18972(VillagerEntity villagerEntity) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        if (!brain.getOptionalMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
            return false;
        }
        VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
        return LookTargetUtil.canSee(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && villagerEntity.isReadyToBreed() && villagerEntity2.isReadyToBreed();
    }

    private Optional<BlockPos> method_19573(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return serverWorld.getPointOfInterestStorage().getPosition(PointOfInterestType.HOME.getCompletionCondition(), blockPos -> this.method_20642(villagerEntity, (BlockPos)blockPos), new BlockPos(villagerEntity), 48);
    }

    private boolean method_20642(VillagerEntity villagerEntity, BlockPos blockPos) {
        Path path = villagerEntity.getNavigation().findPathTo(blockPos, PointOfInterestType.HOME.method_21648());
        return path != null && path.method_21655();
    }

    private Optional<VillagerEntity> method_18970(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
        VillagerEntity villagerEntity3 = villagerEntity.createChild(villagerEntity2);
        if (villagerEntity3 == null) {
            return Optional.empty();
        }
        villagerEntity.setBreedingAge(6000);
        villagerEntity2.setBreedingAge(6000);
        villagerEntity3.setBreedingAge(-24000);
        villagerEntity3.refreshPositionAndAngles(villagerEntity.x, villagerEntity.y, villagerEntity.z, 0.0f, 0.0f);
        villagerEntity.world.spawnEntity(villagerEntity3);
        villagerEntity.world.sendEntityStatus(villagerEntity3, (byte)12);
        return Optional.of(villagerEntity3);
    }

    private void method_19572(ServerWorld serverWorld, VillagerEntity villagerEntity, BlockPos blockPos) {
        GlobalPos globalPos = GlobalPos.create(serverWorld.getDimension().getType(), blockPos);
        villagerEntity.getBrain().putMemory(MemoryModuleType.HOME, globalPos);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

