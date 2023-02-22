/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RingBellTask
extends Task<LivingEntity> {
    private static final float RUN_CHANCE = 0.95f;
    public static final int MAX_DISTANCE = 3;

    public RingBellTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return world.random.nextFloat() > 0.95f;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        BlockState blockState;
        Brain<?> brain = entity.getBrain();
        BlockPos blockPos = brain.getOptionalMemory(MemoryModuleType.MEETING_POINT).get().getPos();
        if (blockPos.isWithinDistance(entity.getBlockPos(), 3.0) && (blockState = world.getBlockState(blockPos)).isOf(Blocks.BELL)) {
            BellBlock bellBlock = (BellBlock)blockState.getBlock();
            bellBlock.ring(entity, world, blockPos, null);
        }
    }
}

