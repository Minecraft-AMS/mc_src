/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.kinds.OptionalBox$Mu
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.OptionalBox;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class OpenDoorsTask {
    private static final int RUN_TIME = 20;
    private static final double PATHING_DISTANCE = 3.0;
    private static final double REACH_DISTANCE = 2.0;

    public static Task<LivingEntity> create() {
        MutableObject mutableObject = new MutableObject(null);
        MutableInt mutableInt = new MutableInt(0);
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(MemoryModuleType.PATH), context.queryMemoryOptional(MemoryModuleType.DOORS_TO_CLOSE), context.queryMemoryOptional(MemoryModuleType.MOBS)).apply((Applicative)context, (path, doorsToClose, mobs) -> (world, entity, time) -> {
            DoorBlock doorBlock2;
            BlockPos blockPos2;
            BlockState blockState2;
            Path path = (Path)context.getValue(path);
            Optional<Set<GlobalPos>> optional = context.getOptionalValue(doorsToClose);
            if (path.isStart() || path.isFinished()) {
                return false;
            }
            if (Objects.equals(mutableObject.getValue(), path.getCurrentNode())) {
                mutableInt.setValue(20);
            } else if (mutableInt.decrementAndGet() > 0) {
                return false;
            }
            mutableObject.setValue((Object)path.getCurrentNode());
            PathNode pathNode = path.getLastNode();
            PathNode pathNode2 = path.getCurrentNode();
            BlockPos blockPos = pathNode.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
                DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
                if (!doorBlock.isOpen(blockState)) {
                    doorBlock.setOpen(entity, world, blockState, blockPos, true);
                }
                optional = OpenDoorsTask.storePos(doorsToClose, optional, world, blockPos);
            }
            if ((blockState2 = world.getBlockState(blockPos2 = pathNode2.getBlockPos())).isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock) && !(doorBlock2 = (DoorBlock)blockState2.getBlock()).isOpen(blockState2)) {
                doorBlock2.setOpen(entity, world, blockState2, blockPos2, true);
                optional = OpenDoorsTask.storePos(doorsToClose, optional, world, blockPos2);
            }
            optional.ifPresent(doors -> OpenDoorsTask.pathToDoor(world, entity, pathNode, pathNode2, doors, context.getOptionalValue(mobs)));
            return true;
        }));
    }

    public static void pathToDoor(ServerWorld world, LivingEntity entity, @Nullable PathNode lastNode, @Nullable PathNode currentNode, Set<GlobalPos> doors, Optional<List<LivingEntity>> otherMobs) {
        Iterator<GlobalPos> iterator = doors.iterator();
        while (iterator.hasNext()) {
            GlobalPos globalPos = iterator.next();
            BlockPos blockPos = globalPos.getPos();
            if (lastNode != null && lastNode.getBlockPos().equals(blockPos) || currentNode != null && currentNode.getBlockPos().equals(blockPos)) continue;
            if (OpenDoorsTask.cannotReachDoor(world, entity, globalPos)) {
                iterator.remove();
                continue;
            }
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
                iterator.remove();
                continue;
            }
            DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
            if (!doorBlock.isOpen(blockState)) {
                iterator.remove();
                continue;
            }
            if (OpenDoorsTask.hasOtherMobReachedDoor(entity, blockPos, otherMobs)) {
                iterator.remove();
                continue;
            }
            doorBlock.setOpen(entity, world, blockState, blockPos, false);
            iterator.remove();
        }
    }

    private static boolean hasOtherMobReachedDoor(LivingEntity entity, BlockPos pos, Optional<List<LivingEntity>> otherMobs) {
        if (otherMobs.isEmpty()) {
            return false;
        }
        return otherMobs.get().stream().filter(mob -> mob.getType() == entity.getType()).filter(mob -> pos.isWithinDistance(mob.getPos(), 2.0)).anyMatch(mob -> OpenDoorsTask.hasReached(mob.getBrain(), pos));
    }

    private static boolean hasReached(Brain<?> brain, BlockPos pos) {
        if (!brain.hasMemoryModule(MemoryModuleType.PATH)) {
            return false;
        }
        Path path = brain.getOptionalRegisteredMemory(MemoryModuleType.PATH).get();
        if (path.isFinished()) {
            return false;
        }
        PathNode pathNode = path.getLastNode();
        if (pathNode == null) {
            return false;
        }
        PathNode pathNode2 = path.getCurrentNode();
        return pos.equals(pathNode.getBlockPos()) || pos.equals(pathNode2.getBlockPos());
    }

    private static boolean cannotReachDoor(ServerWorld world, LivingEntity entity, GlobalPos doorPos) {
        return doorPos.getDimension() != world.getRegistryKey() || !doorPos.getPos().isWithinDistance(entity.getPos(), 3.0);
    }

    private static Optional<Set<GlobalPos>> storePos(MemoryQueryResult<OptionalBox.Mu, Set<GlobalPos>> queryResult, Optional<Set<GlobalPos>> doors, ServerWorld world, BlockPos pos) {
        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
        return Optional.of(doors.map(doorSet -> {
            doorSet.add(globalPos);
            return doorSet;
        }).orElseGet(() -> {
            HashSet set = Sets.newHashSet((Object[])new GlobalPos[]{globalPos});
            queryResult.remember(set);
            return set;
        }));
    }
}

