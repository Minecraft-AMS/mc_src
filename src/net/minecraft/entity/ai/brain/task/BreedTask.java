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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class BreedTask
extends Task<AnimalEntity> {
    private final EntityType<? extends AnimalEntity> targetType;
    private final float field_23129;
    private long breedTime;

    public BreedTask(EntityType<? extends AnimalEntity> targetType, float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.VISIBLE_MOBS, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.BREED_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED)), 325);
        this.targetType = targetType;
        this.field_23129 = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, AnimalEntity animalEntity) {
        return animalEntity.isInLove() && this.findBreedTarget(animalEntity).isPresent();
    }

    @Override
    protected void run(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        AnimalEntity animalEntity2 = this.findBreedTarget(animalEntity).get();
        animalEntity.getBrain().remember(MemoryModuleType.BREED_TARGET, animalEntity2);
        animalEntity2.getBrain().remember(MemoryModuleType.BREED_TARGET, animalEntity);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, this.field_23129);
        int i = 275 + animalEntity.getRandom().nextInt(50);
        this.breedTime = l + (long)i;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        if (!this.hasBreedTarget(animalEntity)) {
            return false;
        }
        AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
        return animalEntity2.isAlive() && animalEntity.canBreedWith(animalEntity2) && LookTargetUtil.canSee(animalEntity.getBrain(), animalEntity2) && l <= this.breedTime;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, this.field_23129);
        if (!animalEntity.isInRange(animalEntity2, 3.0)) {
            return;
        }
        if (l >= this.breedTime) {
            animalEntity.breed(serverWorld, animalEntity2);
            animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
            animalEntity2.getBrain().forget(MemoryModuleType.BREED_TARGET);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
        animalEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        animalEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        this.breedTime = 0L;
    }

    private AnimalEntity getBreedTarget(AnimalEntity animal) {
        return (AnimalEntity)animal.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasBreedTarget(AnimalEntity animal) {
        Brain<PassiveEntity> brain = animal.getBrain();
        return brain.hasMemoryModule(MemoryModuleType.BREED_TARGET) && brain.getOptionalMemory(MemoryModuleType.BREED_TARGET).get().getType() == this.targetType;
    }

    private Optional<? extends AnimalEntity> findBreedTarget(AnimalEntity animal) {
        return animal.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().filter(livingEntity -> livingEntity.getType() == this.targetType).map(livingEntity -> (AnimalEntity)livingEntity).filter(animal::canBreedWith).findFirst();
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (AnimalEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (AnimalEntity)entity, time);
    }
}
