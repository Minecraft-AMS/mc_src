/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

public abstract class PersistentState {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean dirty;

    public abstract NbtCompound writeNbt(NbtCompound var1);

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void save(File file) {
        if (!this.isDirty()) {
            return;
        }
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("data", this.writeNbt(new NbtCompound()));
        nbtCompound.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        try {
            NbtIo.writeCompressed(nbtCompound, file);
        }
        catch (IOException iOException) {
            LOGGER.error("Could not save data {}", (Object)this, (Object)iOException);
        }
        this.setDirty(false);
    }
}

