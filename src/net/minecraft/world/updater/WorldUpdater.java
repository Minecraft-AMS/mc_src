/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatMaps
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.updater;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpdater {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private final String levelName;
    private final boolean eraseCache;
    private final WorldSaveHandler worldSaveHandler;
    private final Thread updateThread;
    private final File worldDirectory;
    private volatile boolean keepUpgradingChunks = true;
    private volatile boolean isDone;
    private volatile float progress;
    private volatile int totalChunkCount;
    private volatile int upgradedChunkCount;
    private volatile int skippedChunkCount;
    private final Object2FloatMap<DimensionType> dimensionProgress = Object2FloatMaps.synchronize((Object2FloatMap)new Object2FloatOpenCustomHashMap(Util.identityHashStrategy()));
    private volatile Text status = new TranslatableText("optimizeWorld.stage.counting", new Object[0]);
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final PersistentStateManager persistentStateManager;

    public WorldUpdater(String string, LevelStorage levelStorage, LevelProperties levelProperties, boolean eraseCache) {
        this.levelName = levelProperties.getLevelName();
        this.eraseCache = eraseCache;
        this.worldSaveHandler = levelStorage.createSaveHandler(string, null);
        this.worldSaveHandler.saveWorld(levelProperties);
        this.persistentStateManager = new PersistentStateManager(new File(DimensionType.OVERWORLD.getSaveDirectory(this.worldSaveHandler.getWorldDir()), "data"), this.worldSaveHandler.getDataFixer());
        this.worldDirectory = this.worldSaveHandler.getWorldDir();
        this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
        this.updateThread.setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Error upgrading world", throwable);
            this.status = new TranslatableText("optimizeWorld.stage.failed", new Object[0]);
            this.isDone = true;
        });
        this.updateThread.start();
    }

    public void cancel() {
        this.keepUpgradingChunks = false;
        try {
            this.updateThread.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void updateWorld() {
        File file = this.worldSaveHandler.getWorldDir();
        this.totalChunkCount = 0;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (DimensionType dimensionType : DimensionType.getAll()) {
            List<ChunkPos> list = this.getChunkPositions(dimensionType);
            builder.put((Object)dimensionType, list.listIterator());
            this.totalChunkCount += list.size();
        }
        if (this.totalChunkCount == 0) {
            this.isDone = true;
            return;
        }
        float f = this.totalChunkCount;
        ImmutableMap immutableMap = builder.build();
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        for (DimensionType dimensionType2 : DimensionType.getAll()) {
            File file2 = dimensionType2.getSaveDirectory(file);
            builder2.put((Object)dimensionType2, (Object)new VersionedChunkStorage(new File(file2, "region"), this.worldSaveHandler.getDataFixer()));
        }
        ImmutableMap immutableMap2 = builder2.build();
        long l = Util.getMeasuringTimeMs();
        this.status = new TranslatableText("optimizeWorld.stage.upgrading", new Object[0]);
        while (this.keepUpgradingChunks) {
            boolean bl = false;
            float g = 0.0f;
            for (DimensionType dimensionType3 : DimensionType.getAll()) {
                ListIterator listIterator = (ListIterator)immutableMap.get((Object)dimensionType3);
                VersionedChunkStorage versionedChunkStorage = (VersionedChunkStorage)immutableMap2.get((Object)dimensionType3);
                if (listIterator.hasNext()) {
                    ChunkPos chunkPos = (ChunkPos)listIterator.next();
                    boolean bl2 = false;
                    try {
                        CompoundTag compoundTag = versionedChunkStorage.getNbt(chunkPos);
                        if (compoundTag != null) {
                            boolean bl3;
                            int i = VersionedChunkStorage.getDataVersion(compoundTag);
                            CompoundTag compoundTag2 = versionedChunkStorage.updateChunkTag(dimensionType3, () -> this.persistentStateManager, compoundTag);
                            CompoundTag compoundTag3 = compoundTag2.getCompound("Level");
                            ChunkPos chunkPos2 = new ChunkPos(compoundTag3.getInt("xPos"), compoundTag3.getInt("zPos"));
                            if (!chunkPos2.equals(chunkPos)) {
                                LOGGER.warn("Chunk {} has invalid position {}", (Object)chunkPos, (Object)chunkPos2);
                            }
                            boolean bl4 = bl3 = i < SharedConstants.getGameVersion().getWorldVersion();
                            if (this.eraseCache) {
                                bl3 = bl3 || compoundTag3.contains("Heightmaps");
                                compoundTag3.remove("Heightmaps");
                                bl3 = bl3 || compoundTag3.contains("isLightOn");
                                compoundTag3.remove("isLightOn");
                            }
                            if (bl3) {
                                versionedChunkStorage.setTagAt(chunkPos, compoundTag2);
                                bl2 = true;
                            }
                        }
                    }
                    catch (CrashException crashException) {
                        Throwable throwable = crashException.getCause();
                        if (throwable instanceof IOException) {
                            LOGGER.error("Error upgrading chunk {}", (Object)chunkPos, (Object)throwable);
                        }
                        throw crashException;
                    }
                    catch (IOException iOException) {
                        LOGGER.error("Error upgrading chunk {}", (Object)chunkPos, (Object)iOException);
                    }
                    if (bl2) {
                        ++this.upgradedChunkCount;
                    } else {
                        ++this.skippedChunkCount;
                    }
                    bl = true;
                }
                float h = (float)listIterator.nextIndex() / f;
                this.dimensionProgress.put((Object)dimensionType3, h);
                g += h;
            }
            this.progress = g;
            if (bl) continue;
            this.keepUpgradingChunks = false;
        }
        this.status = new TranslatableText("optimizeWorld.stage.finished", new Object[0]);
        for (VersionedChunkStorage versionedChunkStorage2 : immutableMap2.values()) {
            try {
                versionedChunkStorage2.close();
            }
            catch (IOException iOException2) {
                LOGGER.error("Error upgrading chunk", (Throwable)iOException2);
            }
        }
        this.persistentStateManager.save();
        l = Util.getMeasuringTimeMs() - l;
        LOGGER.info("World optimizaton finished after {} ms", (Object)l);
        this.isDone = true;
    }

    private List<ChunkPos> getChunkPositions(DimensionType dimensionType) {
        File file2 = dimensionType.getSaveDirectory(this.worldDirectory);
        File file22 = new File(file2, "region");
        File[] files = file22.listFiles((file, string) -> string.endsWith(".mca"));
        if (files == null) {
            return ImmutableList.of();
        }
        ArrayList list = Lists.newArrayList();
        for (File file3 : files) {
            Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
            if (!matcher.matches()) continue;
            int i = Integer.parseInt(matcher.group(1)) << 5;
            int j = Integer.parseInt(matcher.group(2)) << 5;
            try (RegionFile regionFile = new RegionFile(file3, file22);){
                for (int k = 0; k < 32; ++k) {
                    for (int l = 0; l < 32; ++l) {
                        ChunkPos chunkPos = new ChunkPos(k + i, l + j);
                        if (!regionFile.isChunkValid(chunkPos)) continue;
                        list.add(chunkPos);
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return list;
    }

    public boolean isDone() {
        return this.isDone;
    }

    @Environment(value=EnvType.CLIENT)
    public float getProgress(DimensionType dimensionType) {
        return this.dimensionProgress.getFloat((Object)dimensionType);
    }

    @Environment(value=EnvType.CLIENT)
    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunkCount() {
        return this.totalChunkCount;
    }

    public int getUpgradedChunkCount() {
        return this.upgradedChunkCount;
    }

    public int getSkippedChunkCount() {
        return this.skippedChunkCount;
    }

    public Text getStatus() {
        return this.status;
    }
}

