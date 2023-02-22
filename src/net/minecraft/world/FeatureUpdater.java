/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkUpdateState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class FeatureUpdater {
    private static final Map<String, String> OLD_TO_NEW = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Village", "Village");
        hashMap.put("Mineshaft", "Mineshaft");
        hashMap.put("Mansion", "Mansion");
        hashMap.put("Igloo", "Temple");
        hashMap.put("Desert_Pyramid", "Temple");
        hashMap.put("Jungle_Pyramid", "Temple");
        hashMap.put("Swamp_Hut", "Temple");
        hashMap.put("Stronghold", "Stronghold");
        hashMap.put("Monument", "Monument");
        hashMap.put("Fortress", "Fortress");
        hashMap.put("EndCity", "EndCity");
    });
    private static final Map<String, String> ANCIENT_TO_OLD = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Iglu", "Igloo");
        hashMap.put("TeDP", "Desert_Pyramid");
        hashMap.put("TeJP", "Jungle_Pyramid");
        hashMap.put("TeSH", "Swamp_Hut");
    });
    private final boolean needsUpdate;
    private final Map<String, Long2ObjectMap<NbtCompound>> featureIdToChunkNbt = Maps.newHashMap();
    private final Map<String, ChunkUpdateState> updateStates = Maps.newHashMap();
    private final List<String> field_17658;
    private final List<String> field_17659;

    public FeatureUpdater(@Nullable PersistentStateManager persistentStateManager, List<String> list, List<String> list2) {
        this.field_17658 = list;
        this.field_17659 = list2;
        this.init(persistentStateManager);
        boolean bl = false;
        for (String string : this.field_17659) {
            bl |= this.featureIdToChunkNbt.get(string) != null;
        }
        this.needsUpdate = bl;
    }

    public void markResolved(long l) {
        for (String string : this.field_17658) {
            ChunkUpdateState chunkUpdateState = this.updateStates.get(string);
            if (chunkUpdateState == null || !chunkUpdateState.isRemaining(l)) continue;
            chunkUpdateState.markResolved(l);
            chunkUpdateState.markDirty();
        }
    }

    public NbtCompound getUpdatedReferences(NbtCompound nbt) {
        NbtCompound nbtCompound = nbt.getCompound("Level");
        ChunkPos chunkPos = new ChunkPos(nbtCompound.getInt("xPos"), nbtCompound.getInt("zPos"));
        if (this.needsUpdate(chunkPos.x, chunkPos.z)) {
            nbt = this.getUpdatedStarts(nbt, chunkPos);
        }
        NbtCompound nbtCompound2 = nbtCompound.getCompound("Structures");
        NbtCompound nbtCompound3 = nbtCompound2.getCompound("References");
        for (String string : this.field_17659) {
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get((Object)string.toLowerCase(Locale.ROOT));
            if (nbtCompound3.contains(string, 12) || structureFeature == null) continue;
            int i = 8;
            LongArrayList longList = new LongArrayList();
            for (int j = chunkPos.x - 8; j <= chunkPos.x + 8; ++j) {
                for (int k = chunkPos.z - 8; k <= chunkPos.z + 8; ++k) {
                    if (!this.needsUpdate(j, k, string)) continue;
                    longList.add(ChunkPos.toLong(j, k));
                }
            }
            nbtCompound3.putLongArray(string, (List<Long>)longList);
        }
        nbtCompound2.put("References", nbtCompound3);
        nbtCompound.put("Structures", nbtCompound2);
        nbt.put("Level", nbtCompound);
        return nbt;
    }

    private boolean needsUpdate(int chunkX, int chunkZ, String id) {
        if (!this.needsUpdate) {
            return false;
        }
        return this.featureIdToChunkNbt.get(id) != null && this.updateStates.get(OLD_TO_NEW.get(id)).contains(ChunkPos.toLong(chunkX, chunkZ));
    }

    private boolean needsUpdate(int chunkX, int chunkZ) {
        if (!this.needsUpdate) {
            return false;
        }
        for (String string : this.field_17659) {
            if (this.featureIdToChunkNbt.get(string) == null || !this.updateStates.get(OLD_TO_NEW.get(string)).isRemaining(ChunkPos.toLong(chunkX, chunkZ))) continue;
            return true;
        }
        return false;
    }

    private NbtCompound getUpdatedStarts(NbtCompound nbt, ChunkPos pos) {
        NbtCompound nbtCompound = nbt.getCompound("Level");
        NbtCompound nbtCompound2 = nbtCompound.getCompound("Structures");
        NbtCompound nbtCompound3 = nbtCompound2.getCompound("Starts");
        for (String string : this.field_17659) {
            NbtCompound nbtCompound4;
            Long2ObjectMap<NbtCompound> long2ObjectMap = this.featureIdToChunkNbt.get(string);
            if (long2ObjectMap == null) continue;
            long l = pos.toLong();
            if (!this.updateStates.get(OLD_TO_NEW.get(string)).isRemaining(l) || (nbtCompound4 = (NbtCompound)long2ObjectMap.get(l)) == null) continue;
            nbtCompound3.put(string, nbtCompound4);
        }
        nbtCompound2.put("Starts", nbtCompound3);
        nbtCompound.put("Structures", nbtCompound2);
        nbt.put("Level", nbtCompound);
        return nbt;
    }

    private void init(@Nullable PersistentStateManager persistentStateManager) {
        if (persistentStateManager == null) {
            return;
        }
        for (String string2 : this.field_17658) {
            NbtCompound nbtCompound = new NbtCompound();
            try {
                nbtCompound = persistentStateManager.readNbt(string2, 1493).getCompound("data").getCompound("Features");
                if (nbtCompound.isEmpty()) {
                    continue;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            for (String string22 : nbtCompound.getKeys()) {
                String string3;
                String string4;
                NbtCompound nbtCompound2 = nbtCompound.getCompound(string22);
                long l = ChunkPos.toLong(nbtCompound2.getInt("ChunkX"), nbtCompound2.getInt("ChunkZ"));
                NbtList nbtList = nbtCompound2.getList("Children", 10);
                if (!nbtList.isEmpty() && (string4 = ANCIENT_TO_OLD.get(string3 = nbtList.getCompound(0).getString("id"))) != null) {
                    nbtCompound2.putString("id", string4);
                }
                string3 = nbtCompound2.getString("id");
                this.featureIdToChunkNbt.computeIfAbsent(string3, string -> new Long2ObjectOpenHashMap()).put(l, (Object)nbtCompound2);
            }
            String string5 = string2 + "_index";
            ChunkUpdateState chunkUpdateState = persistentStateManager.getOrCreate(() -> new ChunkUpdateState(string5), string5);
            if (chunkUpdateState.getAll().isEmpty()) {
                ChunkUpdateState chunkUpdateState2 = new ChunkUpdateState(string5);
                this.updateStates.put(string2, chunkUpdateState2);
                for (String string6 : nbtCompound.getKeys()) {
                    NbtCompound nbtCompound3 = nbtCompound.getCompound(string6);
                    chunkUpdateState2.add(ChunkPos.toLong(nbtCompound3.getInt("ChunkX"), nbtCompound3.getInt("ChunkZ")));
                }
                chunkUpdateState2.markDirty();
                continue;
            }
            this.updateStates.put(string2, chunkUpdateState);
        }
    }

    public static FeatureUpdater create(RegistryKey<World> world, @Nullable PersistentStateManager persistentStateManager) {
        if (world == World.OVERWORLD) {
            return new FeatureUpdater(persistentStateManager, (List<String>)ImmutableList.of((Object)"Monument", (Object)"Stronghold", (Object)"Village", (Object)"Mineshaft", (Object)"Temple", (Object)"Mansion"), (List<String>)ImmutableList.of((Object)"Village", (Object)"Mineshaft", (Object)"Mansion", (Object)"Igloo", (Object)"Desert_Pyramid", (Object)"Jungle_Pyramid", (Object)"Swamp_Hut", (Object)"Stronghold", (Object)"Monument"));
        }
        if (world == World.NETHER) {
            ImmutableList list = ImmutableList.of((Object)"Fortress");
            return new FeatureUpdater(persistentStateManager, (List<String>)list, (List<String>)list);
        }
        if (world == World.END) {
            ImmutableList list = ImmutableList.of((Object)"EndCity");
            return new FeatureUpdater(persistentStateManager, (List<String>)list, (List<String>)list);
        }
        throw new RuntimeException(String.format("Unknown dimension type : %s", world));
    }
}

