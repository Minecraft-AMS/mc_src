/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.FeatureUpdater;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.RegionBasedStorage;
import org.jetbrains.annotations.Nullable;

public class VersionedChunkStorage
extends RegionBasedStorage {
    protected final DataFixer dataFixer;
    @Nullable
    private FeatureUpdater featureUpdater;

    public VersionedChunkStorage(File file, DataFixer dataFixer) {
        super(file);
        this.dataFixer = dataFixer;
    }

    public CompoundTag updateChunkTag(DimensionType dimensionType, Supplier<PersistentStateManager> persistentStateManagerFactory, CompoundTag tag) {
        int i = VersionedChunkStorage.getDataVersion(tag);
        int j = 1493;
        if (i < 1493 && (tag = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, tag, i, 1493)).getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.featureUpdater == null) {
                this.featureUpdater = FeatureUpdater.create(dimensionType, persistentStateManagerFactory.get());
            }
            tag = this.featureUpdater.getUpdatedReferences(tag);
        }
        tag = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, tag, Math.max(1493, i));
        if (i < SharedConstants.getGameVersion().getWorldVersion()) {
            tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        }
        return tag;
    }

    public static int getDataVersion(CompoundTag tag) {
        return tag.contains("DataVersion", 99) ? tag.getInt("DataVersion") : -1;
    }

    @Override
    public void setTagAt(ChunkPos pos, CompoundTag tag) throws IOException {
        super.setTagAt(pos, tag);
        if (this.featureUpdater != null) {
            this.featureUpdater.markResolved(pos.toLong());
        }
    }
}

