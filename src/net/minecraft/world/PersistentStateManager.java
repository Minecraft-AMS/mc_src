/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class PersistentStateManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, PersistentState> loadedStates = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final File directory;

    public PersistentStateManager(File directory, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.directory = directory;
    }

    private File getFile(String id) {
        return new File(this.directory, id + ".dat");
    }

    public <T extends PersistentState> T getOrCreate(Supplier<T> factory, String id) {
        T persistentState = this.method_20786(factory, id);
        if (persistentState != null) {
            return persistentState;
        }
        PersistentState persistentState2 = (PersistentState)factory.get();
        this.set(persistentState2);
        return (T)persistentState2;
    }

    @Nullable
    public <T extends PersistentState> T method_20786(Supplier<T> supplier, String string) {
        PersistentState persistentState = this.loadedStates.get(string);
        if (persistentState == null && !this.loadedStates.containsKey(string)) {
            persistentState = this.readFromFile(supplier, string);
            this.loadedStates.put(string, persistentState);
        }
        return (T)persistentState;
    }

    @Nullable
    private <T extends PersistentState> T readFromFile(Supplier<T> factory, String id) {
        try {
            File file = this.getFile(id);
            if (file.exists()) {
                PersistentState persistentState = (PersistentState)factory.get();
                CompoundTag compoundTag = this.method_17923(id, SharedConstants.getGameVersion().getWorldVersion());
                persistentState.fromTag(compoundTag.getCompound("data"));
                return (T)persistentState;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", (Object)id, (Object)exception);
        }
        return null;
    }

    public void set(PersistentState state) {
        this.loadedStates.put(state.getId(), state);
    }

    public CompoundTag method_17923(String string, int i) throws IOException {
        File file = this.getFile(string);
        try (PushbackInputStream pushbackInputStream = new PushbackInputStream(new FileInputStream(file), 2);){
            Object object;
            CompoundTag compoundTag;
            if (this.method_17921(pushbackInputStream)) {
                compoundTag = NbtIo.readCompressed(pushbackInputStream);
            } else {
                DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
                object = null;
                try {
                    compoundTag = NbtIo.read(dataInputStream);
                }
                catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                }
                finally {
                    if (dataInputStream != null) {
                        if (object != null) {
                            try {
                                dataInputStream.close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            dataInputStream.close();
                        }
                    }
                }
            }
            int j = compoundTag.contains("DataVersion", 99) ? compoundTag.getInt("DataVersion") : 1343;
            object = NbtHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, compoundTag, j, i);
            return object;
        }
    }

    private boolean method_17921(PushbackInputStream pushbackInputStream) throws IOException {
        int j;
        byte[] bs = new byte[2];
        boolean bl = false;
        int i = pushbackInputStream.read(bs, 0, 2);
        if (i == 2 && (j = (bs[1] & 0xFF) << 8 | bs[0] & 0xFF) == 35615) {
            bl = true;
        }
        if (i != 0) {
            pushbackInputStream.unread(bs, 0, i);
        }
        return bl;
    }

    public void save() {
        for (PersistentState persistentState : this.loadedStates.values()) {
            if (persistentState == null) continue;
            persistentState.save(this.getFile(persistentState.getId()));
        }
    }
}

