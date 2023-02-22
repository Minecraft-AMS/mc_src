/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Util;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class WorldSaveHandler
implements PlayerSaveHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File worldDir;
    private final File playerDataDir;
    private final long saveStartTime = Util.getMeasuringTimeMs();
    private final String worldName;
    private final StructureManager structureManager;
    protected final DataFixer dataFixer;

    public WorldSaveHandler(File worldsDirectory, String worldName, @Nullable MinecraftServer server, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.worldDir = new File(worldsDirectory, worldName);
        this.worldDir.mkdirs();
        this.playerDataDir = new File(this.worldDir, "playerdata");
        this.worldName = worldName;
        if (server != null) {
            this.playerDataDir.mkdirs();
            this.structureManager = new StructureManager(server, this.worldDir, dataFixer);
        } else {
            this.structureManager = null;
        }
        this.writeSessionLock();
    }

    public void saveWorld(LevelProperties levelProperties, @Nullable CompoundTag compoundTag) {
        levelProperties.setVersion(19133);
        CompoundTag compoundTag2 = levelProperties.cloneWorldTag(compoundTag);
        CompoundTag compoundTag3 = new CompoundTag();
        compoundTag3.put("Data", compoundTag2);
        try {
            File file = new File(this.worldDir, "level.dat_new");
            File file2 = new File(this.worldDir, "level.dat_old");
            File file3 = new File(this.worldDir, "level.dat");
            NbtIo.writeCompressed(compoundTag3, new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }
            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }
            file.renameTo(file3);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void writeSessionLock() {
        try {
            File file = new File(this.worldDir, "session.lock");
            try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
                dataOutputStream.writeLong(this.saveStartTime);
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    public File getWorldDir() {
        return this.worldDir;
    }

    public void checkSessionLock() throws SessionLockException {
        try {
            File file = new File(this.worldDir, "session.lock");
            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
                if (dataInputStream.readLong() != this.saveStartTime) {
                    throw new SessionLockException("The save is being accessed from another location, aborting");
                }
            }
        }
        catch (IOException iOException) {
            throw new SessionLockException("Failed to check session lock, aborting");
        }
    }

    @Nullable
    public LevelProperties readProperties() {
        LevelProperties levelProperties;
        File file = new File(this.worldDir, "level.dat");
        if (file.exists() && (levelProperties = LevelStorage.readLevelProperties(file, this.dataFixer)) != null) {
            return levelProperties;
        }
        file = new File(this.worldDir, "level.dat_old");
        if (file.exists()) {
            return LevelStorage.readLevelProperties(file, this.dataFixer);
        }
        return null;
    }

    public void saveWorld(LevelProperties levelProperties) {
        this.saveWorld(levelProperties, null);
    }

    @Override
    public void savePlayerData(PlayerEntity playerEntity) {
        try {
            CompoundTag compoundTag = playerEntity.toTag(new CompoundTag());
            File file = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat.tmp");
            File file2 = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat");
            NbtIo.writeCompressed(compoundTag, new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save player data for {}", (Object)playerEntity.getName().getString());
        }
    }

    @Override
    @Nullable
    public CompoundTag loadPlayerData(PlayerEntity playerEntity) {
        CompoundTag compoundTag = null;
        try {
            File file = new File(this.playerDataDir, playerEntity.getUuidAsString() + ".dat");
            if (file.exists() && file.isFile()) {
                compoundTag = NbtIo.readCompressed(new FileInputStream(file));
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load player data for {}", (Object)playerEntity.getName().getString());
        }
        if (compoundTag != null) {
            int i = compoundTag.contains("DataVersion", 3) ? compoundTag.getInt("DataVersion") : -1;
            playerEntity.fromTag(NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, compoundTag, i));
        }
        return compoundTag;
    }

    public String[] getSavedPlayerIds() {
        String[] strings = this.playerDataDir.list();
        if (strings == null) {
            strings = new String[]{};
        }
        for (int i = 0; i < strings.length; ++i) {
            if (!strings[i].endsWith(".dat")) continue;
            strings[i] = strings[i].substring(0, strings[i].length() - 4);
        }
        return strings;
    }

    public StructureManager getStructureManager() {
        return this.structureManager;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }
}

