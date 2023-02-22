/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.StorageIoWorker;
import org.jetbrains.annotations.Nullable;

public class VersionedChunkStorage
implements AutoCloseable {
    public static final int field_36219 = 1493;
    private final StorageIoWorker worker;
    protected final DataFixer dataFixer;
    @Nullable
    private FeatureUpdater featureUpdater;

    public VersionedChunkStorage(Path directory, DataFixer dataFixer, boolean dsync) {
        this.dataFixer = dataFixer;
        this.worker = new StorageIoWorker(directory, dsync, "chunk");
    }

    public NbtCompound updateChunkNbt(RegistryKey<World> worldKey, Supplier<PersistentStateManager> persistentStateManagerFactory, NbtCompound nbt, Optional<RegistryKey<Codec<? extends ChunkGenerator>>> generatorCodecKey) {
        int i = VersionedChunkStorage.getDataVersion(nbt);
        if (i < 1493 && (nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, i, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.featureUpdater == null) {
                this.featureUpdater = FeatureUpdater.create(worldKey, persistentStateManagerFactory.get());
            }
            nbt = this.featureUpdater.getUpdatedReferences(nbt);
        }
        VersionedChunkStorage.saveContextToNbt(nbt, worldKey, generatorCodecKey);
        nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, Math.max(1493, i));
        if (i < SharedConstants.getGameVersion().getWorldVersion()) {
            nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        nbt.remove("__context");
        return nbt;
    }

    public static void saveContextToNbt(NbtCompound nbt, RegistryKey<World> worldKey, Optional<RegistryKey<Codec<? extends ChunkGenerator>>> generatorCodecKey) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("dimension", worldKey.getValue().toString());
        generatorCodecKey.ifPresent(key -> nbtCompound.putString("generator", key.getValue().toString()));
        nbt.put("__context", nbtCompound);
    }

    public static int getDataVersion(NbtCompound nbt) {
        return nbt.contains("DataVersion", 99) ? nbt.getInt("DataVersion") : -1;
    }

    @Nullable
    public NbtCompound getNbt(ChunkPos chunkPos) throws IOException {
        return this.worker.getNbt(chunkPos);
    }

    public void setNbt(ChunkPos chunkPos, NbtCompound nbt) {
        this.worker.setResult(chunkPos, nbt);
        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(chunkPos.toLong());
        }
    }

    public void completeAll() {
        this.worker.completeAll(true).join();
    }

    @Override
    public void close() throws IOException {
        this.worker.close();
    }

    public NbtScannable getWorker() {
        return this.worker;
    }
}

