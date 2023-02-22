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

public class ForcedChunkState
extends PersistentState {
    private LongSet chunks = new LongOpenHashSet();

    public ForcedChunkState() {
        super("chunks");
    }

    @Override
    public void fromTag(NbtCompound tag) {
        this.chunks = new LongOpenHashSet(tag.getLongArray("Forced"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLongArray("Forced", this.chunks.toLongArray());
        return nbt;
    }

    public LongSet getChunks() {
        return this.chunks;
    }
}

