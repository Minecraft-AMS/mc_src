/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.raid.Raid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class SeekSkyAfterRaidWinTask
extends SeekSkyTask {
    public SeekSkyAfterRaidWinTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        Raid raid = world.getRaidAt(new BlockPos(entity));
        return raid != null && raid.hasWon() && super.shouldRun(world, entity);
    }
}

