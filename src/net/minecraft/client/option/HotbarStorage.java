/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class HotbarStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_32135 = 9;
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
            NbtCompound nbtCompound = NbtIo.read(this.file);
            if (nbtCompound == null) {
                return;
            }
            if (!nbtCompound.contains("DataVersion", 99)) {
                nbtCompound.putInt("DataVersion", 1343);
            }
            nbtCompound = NbtHelper.update(this.dataFixer, DataFixTypes.HOTBAR, nbtCompound, nbtCompound.getInt("DataVersion"));
            for (int i = 0; i < 9; ++i) {
                this.entries[i].readNbtList(nbtCompound.getList(String.valueOf(i), 10));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load creative mode options", (Throwable)exception);
        }
    }

    public void save() {
        try {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
            for (int i = 0; i < 9; ++i) {
                nbtCompound.put(String.valueOf(i), this.getSavedHotbar(i).toNbtList());
            }
            NbtIo.write(nbtCompound, this.file);
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

