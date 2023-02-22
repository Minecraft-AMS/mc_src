/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.SectionDistanceLevelPropagator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.jetbrains.annotations.Nullable;

public abstract class LightStorage<M extends ChunkToNibbleArrayMap<M>>
extends SectionDistanceLevelPropagator {
    protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightType lightType;
    private final ChunkProvider chunkProvider;
    protected final LongSet field_15808 = new LongOpenHashSet();
    protected final LongSet field_15797 = new LongOpenHashSet();
    protected final LongSet field_15804 = new LongOpenHashSet();
    protected volatile M uncachedLightArrays;
    protected final M lightArrays;
    protected final LongSet field_15802 = new LongOpenHashSet();
    protected final LongSet dirtySections = new LongOpenHashSet();
    protected final Long2ObjectMap<ChunkNibbleArray> lightArraysToAdd = new Long2ObjectOpenHashMap();
    private final LongSet field_19342 = new LongOpenHashSet();
    private final LongSet lightArraysToRemove = new LongOpenHashSet();
    protected volatile boolean hasLightUpdates;

    protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M lightData) {
        super(3, 16, 256);
        this.lightType = lightType;
        this.chunkProvider = chunkProvider;
        this.lightArrays = lightData;
        this.uncachedLightArrays = ((ChunkToNibbleArrayMap)lightData).copy();
        ((ChunkToNibbleArrayMap)this.uncachedLightArrays).disableCache();
    }

    protected boolean hasLight(long sectionPos) {
        return this.getLightArray(sectionPos, true) != null;
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(long sectionPos, boolean cached) {
        return this.getLightArray(cached ? this.lightArrays : this.uncachedLightArrays, sectionPos);
    }

    @Nullable
    protected ChunkNibbleArray getLightArray(M storage, long sectionPos) {
        return ((ChunkToNibbleArrayMap)storage).get(sectionPos);
    }

    @Nullable
    public ChunkNibbleArray method_20533(long l) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return this.getLightArray(l, false);
    }

    protected abstract int getLight(long var1);

    protected int get(long blockPos) {
        long l = ChunkSectionPos.fromGlobalPos(blockPos);
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(l, true);
        return chunkNibbleArray.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    protected void set(long blockPos, int value) {
        long l = ChunkSectionPos.fromGlobalPos(blockPos);
        if (this.field_15802.add(l)) {
            ((ChunkToNibbleArrayMap)this.lightArrays).replaceWithCopy(l);
        }
        ChunkNibbleArray chunkNibbleArray = this.getLightArray(l, true);
        chunkNibbleArray.set(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)), value);
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(blockPos, j, k, i)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long id) {
        if (id == Long.MAX_VALUE) {
            return 2;
        }
        if (this.field_15808.contains(id)) {
            return 0;
        }
        if (!this.lightArraysToRemove.contains(id) && ((ChunkToNibbleArrayMap)this.lightArrays).containsKey(id)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getInitialLevel(long id) {
        if (this.field_15797.contains(id)) {
            return 2;
        }
        if (this.field_15808.contains(id) || this.field_15804.contains(id)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long id, int level) {
        int i = this.getLevel(id);
        if (i != 0 && level == 0) {
            this.field_15808.add(id);
            this.field_15804.remove(id);
        }
        if (i == 0 && level != 0) {
            this.field_15808.remove(id);
            this.field_15797.remove(id);
        }
        if (i >= 2 && level != 2) {
            if (this.lightArraysToRemove.contains(id)) {
                this.lightArraysToRemove.remove(id);
            } else {
                ((ChunkToNibbleArrayMap)this.lightArrays).put(id, this.createLightArray(id));
                this.field_15802.add(id);
                this.onLightArrayCreated(id);
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        for (int l = -1; l <= 1; ++l) {
                            this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(id, k, l, j)));
                        }
                    }
                }
            }
        }
        if (i != 2 && level >= 2) {
            this.lightArraysToRemove.add(id);
        }
        this.hasLightUpdates = !this.lightArraysToRemove.isEmpty();
    }

    protected ChunkNibbleArray createLightArray(long pos) {
        ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(pos);
        if (chunkNibbleArray != null) {
            return chunkNibbleArray;
        }
        return new ChunkNibbleArray();
    }

    protected void removeChunkData(ChunkLightProvider<?, ?> storage, long blockChunkPos) {
        int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(blockChunkPos));
        int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(blockChunkPos));
        int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(blockChunkPos));
        for (int l = 0; l < 16; ++l) {
            for (int m = 0; m < 16; ++m) {
                for (int n = 0; n < 16; ++n) {
                    long o = BlockPos.asLong(i + l, j + m, k + n);
                    storage.removePendingUpdate(o);
                }
            }
        }
    }

    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }

    protected void updateLightArrays(ChunkLightProvider<M, ?> lightProvider, boolean doSkylight, boolean skipEdgeLightPropagation) {
        long m;
        ChunkNibbleArray chunkNibbleArray2;
        long l;
        if (!this.hasLightUpdates() && this.lightArraysToAdd.isEmpty()) {
            return;
        }
        LongIterator longIterator = this.lightArraysToRemove.iterator();
        while (longIterator.hasNext()) {
            l = (Long)longIterator.next();
            this.removeChunkData(lightProvider, l);
            ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.remove(l);
            chunkNibbleArray2 = ((ChunkToNibbleArrayMap)this.lightArrays).removeChunk(l);
            if (!this.field_19342.contains(ChunkSectionPos.withZeroZ(l))) continue;
            if (chunkNibbleArray != null) {
                this.lightArraysToAdd.put(l, (Object)chunkNibbleArray);
                continue;
            }
            if (chunkNibbleArray2 == null) continue;
            this.lightArraysToAdd.put(l, (Object)chunkNibbleArray2);
        }
        ((ChunkToNibbleArrayMap)this.lightArrays).clearCache();
        longIterator = this.lightArraysToRemove.iterator();
        while (longIterator.hasNext()) {
            l = (Long)longIterator.next();
            this.onChunkRemoved(l);
        }
        this.lightArraysToRemove.clear();
        this.hasLightUpdates = false;
        for (Long2ObjectMap.Entry entry : this.lightArraysToAdd.long2ObjectEntrySet()) {
            m = entry.getLongKey();
            if (!this.hasLight(m)) continue;
            chunkNibbleArray2 = (ChunkNibbleArray)entry.getValue();
            if (((ChunkToNibbleArrayMap)this.lightArrays).get(m) == chunkNibbleArray2) continue;
            this.removeChunkData(lightProvider, m);
            ((ChunkToNibbleArrayMap)this.lightArrays).put(m, chunkNibbleArray2);
            this.field_15802.add(m);
        }
        ((ChunkToNibbleArrayMap)this.lightArrays).clearCache();
        if (!skipEdgeLightPropagation) {
            longIterator = this.lightArraysToAdd.keySet().iterator();
            while (longIterator.hasNext()) {
                long l2 = (Long)longIterator.next();
                if (!this.hasLight(l2)) continue;
                int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l2));
                int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l2));
                int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l2));
                for (Direction direction : DIRECTIONS) {
                    long n = ChunkSectionPos.offset(l2, direction);
                    if (this.lightArraysToAdd.containsKey(n) || !this.hasLight(n)) continue;
                    for (int o = 0; o < 16; ++o) {
                        for (int p = 0; p < 16; ++p) {
                            long r;
                            long q;
                            switch (direction) {
                                case DOWN: {
                                    q = BlockPos.asLong(i + p, j, k + o);
                                    r = BlockPos.asLong(i + p, j - 1, k + o);
                                    break;
                                }
                                case UP: {
                                    q = BlockPos.asLong(i + p, j + 16 - 1, k + o);
                                    r = BlockPos.asLong(i + p, j + 16, k + o);
                                    break;
                                }
                                case NORTH: {
                                    q = BlockPos.asLong(i + o, j + p, k);
                                    r = BlockPos.asLong(i + o, j + p, k - 1);
                                    break;
                                }
                                case SOUTH: {
                                    q = BlockPos.asLong(i + o, j + p, k + 16 - 1);
                                    r = BlockPos.asLong(i + o, j + p, k + 16);
                                    break;
                                }
                                case WEST: {
                                    q = BlockPos.asLong(i, j + o, k + p);
                                    r = BlockPos.asLong(i - 1, j + o, k + p);
                                    break;
                                }
                                default: {
                                    q = BlockPos.asLong(i + 16 - 1, j + o, k + p);
                                    r = BlockPos.asLong(i + 16, j + o, k + p);
                                }
                            }
                            lightProvider.updateLevel(q, r, lightProvider.getPropagatedLevel(q, r, lightProvider.getLevel(q)), false);
                            lightProvider.updateLevel(r, q, lightProvider.getPropagatedLevel(r, q, lightProvider.getLevel(r)), false);
                        }
                    }
                }
            }
        }
        ObjectIterator objectIterator = this.lightArraysToAdd.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            m = entry.getLongKey();
            if (!this.hasLight(m)) continue;
            objectIterator.remove();
        }
    }

    protected void onLightArrayCreated(long blockPos) {
    }

    protected void onChunkRemoved(long l) {
    }

    protected void method_15535(long l, boolean bl) {
    }

    public void method_20600(long l, boolean bl) {
        if (bl) {
            this.field_19342.add(l);
        } else {
            this.field_19342.remove(l);
        }
    }

    protected void setLightArray(long pos, @Nullable ChunkNibbleArray array) {
        if (array != null) {
            this.lightArraysToAdd.put(pos, (Object)array);
        } else {
            this.lightArraysToAdd.remove(pos);
        }
    }

    protected void updateSectionStatus(long pos, boolean empty) {
        boolean bl = this.field_15808.contains(pos);
        if (!bl && !empty) {
            this.field_15804.add(pos);
            this.updateLevel(Long.MAX_VALUE, pos, 0, true);
        }
        if (bl && empty) {
            this.field_15797.add(pos);
            this.updateLevel(Long.MAX_VALUE, pos, 2, false);
        }
    }

    protected void updateAll() {
        if (this.hasPendingUpdates()) {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }

    protected void notifyChunkProvider() {
        if (!this.field_15802.isEmpty()) {
            Object chunkToNibbleArrayMap = ((ChunkToNibbleArrayMap)this.lightArrays).copy();
            ((ChunkToNibbleArrayMap)chunkToNibbleArrayMap).disableCache();
            this.uncachedLightArrays = chunkToNibbleArrayMap;
            this.field_15802.clear();
        }
        if (!this.dirtySections.isEmpty()) {
            LongIterator longIterator = this.dirtySections.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
            }
            this.dirtySections.clear();
        }
    }
}

