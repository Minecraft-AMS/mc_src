/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class OpenDoorsTask
extends Task<LivingEntity> {
    public OpenDoorsTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.PATH, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.INTERACTABLE_DOORS, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.OPENED_DOORS, (Object)((Object)MemoryModuleState.REGISTERED)));
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> brain = entity.getBrain();
        Path path = brain.getOptionalMemory(MemoryModuleType.PATH).get();
        List<GlobalPos> list = brain.getOptionalMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
        List<BlockPos> list2 = path.getNodes().stream().map(pathNode -> new BlockPos(pathNode.x, pathNode.y, pathNode.z)).collect(Collectors.toList());
        Set<BlockPos> set = this.getDoorsOnPath(world, list, list2);
        int i = path.getCurrentNodeIndex() - 1;
        this.findAndCloseOpenedDoors(world, list2, set, i, entity, brain);
    }

    private Set<BlockPos> getDoorsOnPath(ServerWorld world, List<GlobalPos> doors, List<BlockPos> path) {
        return doors.stream().filter(globalPos -> globalPos.getDimension() == world.getDimension().getType()).map(GlobalPos::getPos).filter(path::contains).collect(Collectors.toSet());
    }

    private void findAndCloseOpenedDoors(ServerWorld world, List<BlockPos> path, Set<BlockPos> doors, int lastNodeIndex, LivingEntity entity, Brain<?> brain) {
        doors.forEach(blockPos -> {
            int j = path.indexOf(blockPos);
            BlockState blockState = world.getBlockState((BlockPos)blockPos);
            Block block = blockState.getBlock();
            if (BlockTags.WOODEN_DOORS.contains(block) && block instanceof DoorBlock) {
                boolean bl = j >= lastNodeIndex;
                ((DoorBlock)block).setOpen(world, (BlockPos)blockPos, bl);
                GlobalPos globalPos = GlobalPos.create(world.getDimension().getType(), blockPos);
                if (!brain.getOptionalMemory(MemoryModuleType.OPENED_DOORS).isPresent() && bl) {
                    brain.putMemory(MemoryModuleType.OPENED_DOORS, Sets.newHashSet((Object[])new GlobalPos[]{globalPos}));
                } else {
                    brain.getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
                        if (bl) {
                            set.add(globalPos);
                        } else {
                            set.remove(globalPos);
                        }
                    });
                }
            }
        });
        OpenDoorsTask.closeOpenedDoors(world, path, lastNodeIndex, entity, brain);
    }

    public static void closeOpenedDoors(ServerWorld world, List<BlockPos> path, int currentPathIndex, LivingEntity entity, Brain<?> brain) {
        brain.getOptionalMemory(MemoryModuleType.OPENED_DOORS).ifPresent(set -> {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                GlobalPos globalPos = (GlobalPos)iterator.next();
                BlockPos blockPos = globalPos.getPos();
                int j = path.indexOf(blockPos);
                if (world.getDimension().getType() != globalPos.getDimension()) {
                    iterator.remove();
                    continue;
                }
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (!BlockTags.WOODEN_DOORS.contains(block) || !(block instanceof DoorBlock) || j >= currentPathIndex || !blockPos.isWithinDistance(entity.getPos(), 4.0)) continue;
                ((DoorBlock)block).setOpen(world, blockPos, false);
                iterator.remove();
            }
        });
    }
}

