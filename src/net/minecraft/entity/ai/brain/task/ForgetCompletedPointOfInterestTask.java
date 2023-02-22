/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.Applicative;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;

public class ForgetCompletedPointOfInterestTask {
    private static final int MAX_RANGE = 16;

    public static Task<LivingEntity> create(Predicate<RegistryEntry<PointOfInterestType>> poiTypePredicate, MemoryModuleType<GlobalPos> poiPosModule) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(poiPosModule)).apply((Applicative)context, poiPos -> (world, entity, time) -> {
            GlobalPos globalPos = (GlobalPos)context.getValue(poiPos);
            BlockPos blockPos = globalPos.getPos();
            if (world.getRegistryKey() != globalPos.getDimension() || !blockPos.isWithinDistance(entity.getPos(), 16.0)) {
                return false;
            }
            ServerWorld serverWorld = world.getServer().getWorld(globalPos.getDimension());
            if (serverWorld == null || !serverWorld.getPointOfInterestStorage().test(blockPos, poiTypePredicate)) {
                poiPos.forget();
            } else if (ForgetCompletedPointOfInterestTask.isBedOccupiedByOthers(serverWorld, blockPos, entity)) {
                poiPos.forget();
                world.getPointOfInterestStorage().releaseTicket(blockPos);
                DebugInfoSender.sendPointOfInterest(world, blockPos);
            }
            return true;
        }));
    }

    private static boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED) != false && !entity.isSleeping();
    }
}

