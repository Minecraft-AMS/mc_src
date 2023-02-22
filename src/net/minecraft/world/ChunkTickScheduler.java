/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.ShortList
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;

public class ChunkTickScheduler<T>
implements TickScheduler<T> {
    protected final Predicate<T> shouldExclude;
    private final ChunkPos pos;
    private final ShortList[] scheduledPositions = new ShortList[16];

    public ChunkTickScheduler(Predicate<T> shouldExclude, ChunkPos pos) {
        this(shouldExclude, pos, new NbtList());
    }

    public ChunkTickScheduler(Predicate<T> shouldExclude, ChunkPos pos, NbtList tag) {
        this.shouldExclude = shouldExclude;
        this.pos = pos;
        for (int i = 0; i < tag.size(); ++i) {
            NbtList nbtList = tag.getList(i);
            for (int j = 0; j < nbtList.size(); ++j) {
                Chunk.getList(this.scheduledPositions, i).add(nbtList.getShort(j));
            }
        }
    }

    public NbtList toNbt() {
        return ChunkSerializer.toNbt(this.scheduledPositions);
    }

    public void tick(TickScheduler<T> scheduler, Function<BlockPos, T> dataMapper) {
        for (int i = 0; i < this.scheduledPositions.length; ++i) {
            if (this.scheduledPositions[i] == null) continue;
            for (Short short_ : this.scheduledPositions[i]) {
                BlockPos blockPos = ProtoChunk.joinBlockPos(short_, i, this.pos);
                scheduler.schedule(blockPos, dataMapper.apply(blockPos), 0);
            }
            this.scheduledPositions[i].clear();
        }
    }

    @Override
    public boolean isScheduled(BlockPos pos, T object) {
        return false;
    }

    @Override
    public void schedule(BlockPos pos, T object, int delay, TickPriority priority) {
        Chunk.getList(this.scheduledPositions, pos.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(pos));
    }

    @Override
    public boolean isTicking(BlockPos pos, T object) {
        return false;
    }
}

