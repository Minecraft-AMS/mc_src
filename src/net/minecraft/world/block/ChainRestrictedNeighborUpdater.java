/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.block;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChainRestrictedNeighborUpdater
implements NeighborUpdater {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final World world;
    private final int maxChainDepth;
    private final ArrayDeque<Entry> queue = new ArrayDeque();
    private final List<Entry> pending = new ArrayList<Entry>();
    private int depth = 0;

    public ChainRestrictedNeighborUpdater(World world, int maxChainDepth) {
        this.world = world;
        this.maxChainDepth = maxChainDepth;
    }

    @Override
    public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        this.enqueue(pos, new StateReplacementEntry(direction, neighborState, pos.toImmutable(), neighborPos.toImmutable(), flags));
    }

    @Override
    public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
        this.enqueue(pos, new SimpleEntry(pos, sourceBlock, sourcePos.toImmutable()));
    }

    @Override
    public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.enqueue(pos, new StatefulEntry(state, pos.toImmutable(), sourceBlock, sourcePos.toImmutable(), notify));
    }

    @Override
    public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
        this.enqueue(pos, new SixWayEntry(pos.toImmutable(), sourceBlock, except));
    }

    private void enqueue(BlockPos pos, Entry entry) {
        boolean bl = this.depth > 0;
        boolean bl2 = this.maxChainDepth >= 0 && this.depth >= this.maxChainDepth;
        ++this.depth;
        if (!bl2) {
            if (bl) {
                this.pending.add(entry);
            } else {
                this.queue.push(entry);
            }
        } else if (this.depth - 1 == this.maxChainDepth) {
            LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + pos.toShortString());
        }
        if (!bl) {
            this.runQueuedUpdates();
        }
    }

    private void runQueuedUpdates() {
        try {
            block3: while (!this.queue.isEmpty() || !this.pending.isEmpty()) {
                for (int i = this.pending.size() - 1; i >= 0; --i) {
                    this.queue.push(this.pending.get(i));
                }
                this.pending.clear();
                Entry entry = this.queue.peek();
                while (this.pending.isEmpty()) {
                    if (entry.update(this.world)) continue;
                    this.queue.pop();
                    continue block3;
                }
            }
        }
        finally {
            this.queue.clear();
            this.pending.clear();
            this.depth = 0;
        }
    }

    record StateReplacementEntry(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int updateFlags) implements Entry
    {
        @Override
        public boolean update(World world) {
            NeighborUpdater.replaceWithStateForNeighborUpdate(world, this.direction, this.neighborState, this.pos, this.neighborPos, this.updateFlags, 512);
            return false;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StateReplacementEntry.class, "direction;state;pos;neighborPos;updateFlags", "direction", "neighborState", "pos", "neighborPos", "updateFlags"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StateReplacementEntry.class, "direction;state;pos;neighborPos;updateFlags", "direction", "neighborState", "pos", "neighborPos", "updateFlags"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StateReplacementEntry.class, "direction;state;pos;neighborPos;updateFlags", "direction", "neighborState", "pos", "neighborPos", "updateFlags"}, this, object);
        }
    }

    static interface Entry {
        public boolean update(World var1);
    }

    record SimpleEntry(BlockPos pos, Block sourceBlock, BlockPos sourcePos) implements Entry
    {
        @Override
        public boolean update(World world) {
            BlockState blockState = world.getBlockState(this.pos);
            NeighborUpdater.tryNeighborUpdate(world, blockState, this.pos, this.sourceBlock, this.sourcePos, false);
            return false;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SimpleEntry.class, "pos;block;neighborPos", "pos", "sourceBlock", "sourcePos"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SimpleEntry.class, "pos;block;neighborPos", "pos", "sourceBlock", "sourcePos"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SimpleEntry.class, "pos;block;neighborPos", "pos", "sourceBlock", "sourcePos"}, this, object);
        }
    }

    record StatefulEntry(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean movedByPiston) implements Entry
    {
        @Override
        public boolean update(World world) {
            NeighborUpdater.tryNeighborUpdate(world, this.state, this.pos, this.sourceBlock, this.sourcePos, this.movedByPiston);
            return false;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StatefulEntry.class, "state;pos;block;neighborPos;movedByPiston", "state", "pos", "sourceBlock", "sourcePos", "movedByPiston"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StatefulEntry.class, "state;pos;block;neighborPos;movedByPiston", "state", "pos", "sourceBlock", "sourcePos", "movedByPiston"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StatefulEntry.class, "state;pos;block;neighborPos;movedByPiston", "state", "pos", "sourceBlock", "sourcePos", "movedByPiston"}, this, object);
        }
    }

    static final class SixWayEntry
    implements Entry {
        private final BlockPos pos;
        private final Block sourceBlock;
        @Nullable
        private final Direction except;
        private int currentDirectionIndex = 0;

        SixWayEntry(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
            this.pos = pos;
            this.sourceBlock = sourceBlock;
            this.except = except;
            if (NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == except) {
                ++this.currentDirectionIndex;
            }
        }

        @Override
        public boolean update(World world) {
            BlockPos blockPos = this.pos.offset(NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++]);
            BlockState blockState = world.getBlockState(blockPos);
            blockState.neighborUpdate(world, blockPos, this.sourceBlock, this.pos, false);
            if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
                ++this.currentDirectionIndex;
            }
            return this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length;
        }
    }
}
