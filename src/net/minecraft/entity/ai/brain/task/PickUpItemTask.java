/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PickUpItemTask
extends Task<VillagerEntity> {
    private List<ItemEntity> nearbyItems = Lists.newArrayList();

    public PickUpItemTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        this.nearbyItems = serverWorld.getNonSpectatingEntities(ItemEntity.class, villagerEntity.getBoundingBox().expand(4.0, 2.0, 4.0));
        return !this.nearbyItems.isEmpty();
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        ItemEntity itemEntity = this.nearbyItems.get(serverWorld.random.nextInt(this.nearbyItems.size()));
        if (villagerEntity.canGather(itemEntity.getStack().getItem())) {
            Vec3d vec3d = itemEntity.getPos();
            villagerEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(new BlockPos(vec3d)));
            villagerEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.5f, 0));
        }
    }
}

