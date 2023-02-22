/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class ForgetAngryAtTargetTask<E extends MobEntity>
extends Task<E> {
    public ForgetAngryAtTargetTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.ANGRY_AT, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        LookTargetUtil.getEntity(mobEntity, MemoryModuleType.ANGRY_AT).ifPresent(target -> {
            if (target.isDead() && (target.getType() != EntityType.PLAYER || serverWorld.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))) {
                mobEntity.getBrain().forget(MemoryModuleType.ANGRY_AT);
            }
        });
    }
}

