/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.util.Unit;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class StartSniffingTask {
    private static final IntProvider COOLDOWN = UniformIntProvider.create(100, 200);

    public static Task<LivingEntity> create() {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryOptional(MemoryModuleType.IS_SNIFFING), context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryAbsent(MemoryModuleType.SNIFF_COOLDOWN), context.queryMemoryValue(MemoryModuleType.NEAREST_ATTACKABLE), context.queryMemoryAbsent(MemoryModuleType.DISTURBANCE_LOCATION)).apply((Applicative)context, (isSniffing, walkTarget, sniffCooldown, nearestAttackable, disturbanceLocation) -> (world, entity, time) -> {
            isSniffing.remember(Unit.INSTANCE);
            sniffCooldown.remember(Unit.INSTANCE, COOLDOWN.get(world.getRandom()));
            walkTarget.forget();
            entity.setPose(EntityPose.SNIFFING);
            return true;
        }));
    }
}

