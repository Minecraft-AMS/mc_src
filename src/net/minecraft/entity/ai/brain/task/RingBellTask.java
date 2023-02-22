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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class RingBellTask
extends Task<LivingEntity> {
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
        if (blockPos.isWithinDistance(new BlockPos(entity), 3.0) && (blockState = world.getBlockState(blockPos)).getBlock() == Blocks.BELL) {
            BellBlock bellBlock = (BellBlock)blockState.getBlock();
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (!bellBlock.ring(world, blockState, world.getBlockEntity(blockPos), new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), direction, blockPos, false), null, false)) continue;
                break;
            }
        }
    }
}

