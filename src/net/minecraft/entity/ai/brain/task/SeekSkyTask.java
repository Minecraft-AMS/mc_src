/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

public class SeekSkyTask
extends Task<LivingEntity> {
    private final float speed;

    public SeekSkyTask(float speed) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.speed = speed;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Optional<Vec3d> optional = Optional.ofNullable(this.findNearbySky(world, entity));
        if (optional.isPresent()) {
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, optional.map(pos -> new WalkTarget((Vec3d)pos, this.speed, 0)));
        }
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return !world.isSkyVisible(entity.getBlockPos());
    }

    @Nullable
    private Vec3d findNearbySky(ServerWorld world, LivingEntity entity) {
        Random random = entity.getRandom();
        BlockPos blockPos = entity.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (!SeekSkyTask.isSkyVisible(world, entity, blockPos2)) continue;
            return Vec3d.ofBottomCenter(blockPos2);
        }
        return null;
    }

    public static boolean isSkyVisible(ServerWorld world, LivingEntity entity, BlockPos pos) {
        return world.isSkyVisible(pos) && (double)world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() <= entity.getY();
    }
}

