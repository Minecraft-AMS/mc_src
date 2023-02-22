/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.options;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class HotbarStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File file;
    private final DataFixer dataFixer;
    private final HotbarStorageEntry[] entries = new HotbarStorageEntry[9];
    private boolean loaded;

    public HotbarStorage(File file, DataFixer dataFixer) {
        this.file = new File(file, "hotbar.nbt");
        this.dataFixer = dataFixer;
        for (int i = 0; i < 9; ++i) {
            this.entries[i] = new HotbarStorageEntry();
        }
    }

    private void load() {
        try {
            CompoundTag compoundTag = NbtIo.read(this.file);
            if (compoundTag == null) {
                return;
            }
            if (!compoundTag.contains("DataVersion", 99)) {
                compoundTag.putInt("DataVersion", 1343);
            }
            compoundTag = NbtHelper.update(this.dataFixer, DataFixTypes.HOTBAR, compoundTag, compoundTag.getInt("DataVersion"));
            for (int i = 0; i < 9; ++i) {
                this.entries[i].fromListTag(compoundTag.getList(String.valueOf(i), 10));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load creative mode options", (Throwable)exception);
        }
    }

    public void save() {
        try {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            for (int i = 0; i < 9; ++i) {
                compoundTag.put(String.valueOf(i), this.getSavedHotbar(i).toListTag());
            }
            NbtIo.write(compoundTag, this.file);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save creative mode options", (Throwable)exception);
        }
    }

    public HotbarStorageEntry getSavedHotbar(int i) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }
        return this.entries[i];
    }
}

