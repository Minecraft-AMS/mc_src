/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class SleepTask
extends Task<LivingEntity> {
    private long field_18848;

    public SleepTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.HOME, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        if (entity.hasVehicle()) {
            return false;
        }
        GlobalPos globalPos = entity.getBrain().getOptionalMemory(MemoryModuleType.HOME).get();
        if (!Objects.equals(world.getDimension().getType(), globalPos.getDimension())) {
            return false;
        }
        BlockState blockState = world.getBlockState(globalPos.getPos());
        return globalPos.getPos().isWithinDistance(entity.getPos(), 2.0) && blockState.getBlock().matches(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED) == false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos = optional.get().getPos();
        return entity.getBrain().hasActivity(Activity.REST) && entity.y > (double)blockPos.getY() + 0.4 && blockPos.isWithinDistance(entity.getPos(), 1.14);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        if (time > this.field_18848) {
            entity.getBrain().getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> OpenDoorsTask.method_21697(world, (List<BlockPos>)ImmutableList.of(), 0, entity, entity.getBrain()));
            entity.sleep(entity.getBrain().getOptionalMemory(MemoryModuleType.HOME).get().getPos());
        }
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    @Override
    protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        if (entity.isSleeping()) {
            entity.wakeUp();
            this.field_18848 = time + 40L;
        }
    }
}
