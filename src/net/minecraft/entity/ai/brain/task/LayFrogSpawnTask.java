/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LayFrogSpawnTask
extends Task<FrogEntity> {
    private final Block frogSpawn;
    private final MemoryModuleType<?> triggerMemory;

    public LayFrogSpawnTask(Block frogSpawn, MemoryModuleType<?> triggerMemory) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.IS_PREGNANT, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        this.frogSpawn = frogSpawn;
        this.triggerMemory = triggerMemory;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        return !frogEntity.isTouchingWater() && frogEntity.isOnGround();
    }

    @Override
    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        BlockPos blockPos = frogEntity.getBlockPos().down();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos3;
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!serverWorld.getBlockState(blockPos2).getCollisionShape(serverWorld, blockPos2).getFace(Direction.UP).isEmpty() || !serverWorld.getFluidState(blockPos2).isOf(Fluids.WATER) || !serverWorld.getBlockState(blockPos3 = blockPos2.up()).isAir()) continue;
            serverWorld.setBlockState(blockPos3, this.frogSpawn.getDefaultState(), 3);
            serverWorld.playSoundFromEntity(null, frogEntity, SoundEvents.ENTITY_FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            frogEntity.getBrain().forget(this.triggerMemory);
            return;
        }
    }
}

