/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class ChunkUpdateState
extends PersistentState {
    private static final String REMAINING_KEY = "Remaining";
    private static final String ALL_KEY = "All";
    private final LongSet all;
    private final LongSet remaining;

    private ChunkUpdateState(LongSet all, LongSet remaining) {
        this.all = all;
        this.remaining = remaining;
    }

    public ChunkUpdateState() {
        this((LongSet)new LongOpenHashSet(), (LongSet)new LongOpenHashSet());
    }

    public static ChunkUpdateState fromNbt(NbtCompound nbt) {
        return new ChunkUpdateState((LongSet)new LongOpenHashSet(nbt.getLongArray(ALL_KEY)), (LongSet)new LongOpenHashSet(nbt.getLongArray(REMAINING_KEY)));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLongArray(ALL_KEY, this.all.toLongArray());
        nbt.putLongArray(REMAINING_KEY, this.remaining.toLongArray());
        return nbt;
    }

    public void add(long l) {
        this.all.add(l);
        this.remaining.add(l);
    }

    public boolean contains(long l) {
        return this.all.contains(l);
    }

    public boolean isRemaining(long l) {
        return this.remaining.contains(l);
    }

    public void markResolved(long l) {
        this.remaining.remove(l);
    }

    public LongSet getAll() {
        return this.all;
    }
}

