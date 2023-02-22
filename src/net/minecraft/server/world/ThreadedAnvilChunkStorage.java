/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SimulationDistanceLevelPropagator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ThreadedAnvilChunkStorage
extends VersionedChunkStorage
implements ChunkHolder.PlayersWatchingChunkProvider {
    private static final byte PROTO_CHUNK = -1;
    private static final byte UNMARKED_CHUNK = 0;
    private static final byte LEVEL_CHUNK = 1;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29674 = 200;
    private static final int field_36291 = 20;
    private static final int field_36384 = 10000;
    private static final int field_29675 = 3;
    public static final int field_29669 = 33;
    public static final int MAX_LEVEL = 33 + ChunkStatus.getMaxDistanceFromFull();
    public static final int field_29670 = 31;
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders = new Long2ObjectLinkedOpenHashMap();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders = this.currentChunkHolders.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload = new Long2ObjectLinkedOpenHashMap();
    private final LongSet loadedChunks = new LongOpenHashSet();
    final ServerWorld world;
    private final ServerLightingProvider lightingProvider;
    private final ThreadExecutor<Runnable> mainThreadExecutor;
    private ChunkGenerator chunkGenerator;
    private final NoiseConfig noiseConfig;
    private final StructurePlacementCalculator structurePlacementCalculator;
    private final Supplier<PersistentStateManager> persistentStateManagerFactory;
    private final PointOfInterestStorage pointOfInterestStorage;
    final LongSet unloadedChunks = new LongOpenHashSet();
    private boolean chunkHolderListDirty;
    private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> worldGenExecutor;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> mainExecutor;
    private final WorldGenerationProgressListener worldGenerationProgressListener;
    private final ChunkStatusChangeListener chunkStatusChangeListener;
    private final TicketManager ticketManager;
    private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();
    private final StructureTemplateManager structureTemplateManager;
    private final String saveDir;
    private final PlayerChunkWatchingManager playerChunkWatchingManager = new PlayerChunkWatchingManager();
    private final Int2ObjectMap<EntityTracker> entityTrackers = new Int2ObjectOpenHashMap();
    private final Long2ByteMap chunkToType = new Long2ByteOpenHashMap();
    private final Long2LongMap chunkToNextSaveTimeMs = new Long2LongOpenHashMap();
    private final Queue<Runnable> unloadTaskQueue = Queues.newConcurrentLinkedQueue();
    int watchDistance;

    public ThreadedAnvilChunkStorage(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ThreadExecutor<Runnable> mainThreadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier<PersistentStateManager> persistentStateManagerFactory, int viewDistance, boolean dsync) {
        super(session.getWorldDirectory(world.getRegistryKey()).resolve("region"), dataFixer, dsync);
        this.structureTemplateManager = structureTemplateManager;
        Path path = session.getWorldDirectory(world.getRegistryKey());
        this.saveDir = path.getFileName().toString();
        this.world = world;
        this.chunkGenerator = chunkGenerator;
        DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
        long l = world.getSeed();
        if (chunkGenerator instanceof NoiseChunkGenerator) {
            NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)chunkGenerator;
            this.noiseConfig = NoiseConfig.create(noiseChunkGenerator.getSettings().value(), dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.NOISE_PARAMETERS), l);
        } else {
            this.noiseConfig = NoiseConfig.create(ChunkGeneratorSettings.createMissingSettings(), dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.NOISE_PARAMETERS), l);
        }
        this.structurePlacementCalculator = chunkGenerator.createStructurePlacementCalculator(dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET), this.noiseConfig, l);
        this.mainThreadExecutor = mainThreadExecutor;
        TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(executor, "worldgen");
        MessageListener<Runnable> messageListener = MessageListener.create("main", mainThreadExecutor::send);
        this.worldGenerationProgressListener = worldGenerationProgressListener;
        this.chunkStatusChangeListener = chunkStatusChangeListener;
        TaskExecutor<Runnable> taskExecutor2 = TaskExecutor.create(executor, "light");
        this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(taskExecutor, messageListener, taskExecutor2), executor, Integer.MAX_VALUE);
        this.worldGenExecutor = this.chunkTaskPrioritySystem.createExecutor(taskExecutor, false);
        this.mainExecutor = this.chunkTaskPrioritySystem.createExecutor(messageListener, false);
        this.lightingProvider = new ServerLightingProvider(chunkProvider, this, this.world.getDimension().hasSkyLight(), taskExecutor2, this.chunkTaskPrioritySystem.createExecutor(taskExecutor2, false));
        this.ticketManager = new TicketManager(executor, mainThreadExecutor);
        this.persistentStateManagerFactory = persistentStateManagerFactory;
        this.pointOfInterestStorage = new PointOfInterestStorage(path.resolve("poi"), dataFixer, dsync, dynamicRegistryManager, world);
        this.setViewDistance(viewDistance);
    }

    protected ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    protected StructurePlacementCalculator getStructurePlacementCalculator() {
        return this.structurePlacementCalculator;
    }

    protected NoiseConfig getNoiseConfig() {
        return this.noiseConfig;
    }

    public void verifyChunkGenerator() {
        DataResult dataResult = ChunkGenerator.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.chunkGenerator);
        DataResult dataResult2 = dataResult.flatMap(json -> ChunkGenerator.CODEC.parse((DynamicOps)JsonOps.INSTANCE, json));
        dataResult2.result().ifPresent(chunkGenerator -> {
            this.chunkGenerator = chunkGenerator;
        });
    }

    private static double getSquaredDistance(ChunkPos pos, Entity entity) {
        double d = ChunkSectionPos.getOffsetPos(pos.x, 8);
        double e = ChunkSectionPos.getOffsetPos(pos.z, 8);
        double f = d - entity.getX();
        double g = e - entity.getZ();
        return f * f + g * g;
    }

    public static boolean isWithinDistance(int x1, int z1, int x2, int z2, int distance) {
        int k;
        int o;
        int i = Math.max(0, Math.abs(x1 - x2) - 1);
        int j = Math.max(0, Math.abs(z1 - z2) - 1);
        long l = Math.max(0, Math.max(i, j) - 1);
        long m = Math.min(i, j);
        long n = m * m + l * l;
        return n <= (long)(o = (k = distance - 1) * k);
    }

    private static boolean isOnDistanceEdge(int x1, int z1, int x2, int z2, int distance) {
        if (!ThreadedAnvilChunkStorage.isWithinDistance(x1, z1, x2, z2, distance)) {
            return false;
        }
        if (!ThreadedAnvilChunkStorage.isWithinDistance(x1 + 1, z1, x2, z2, distance)) {
            return true;
        }
        if (!ThreadedAnvilChunkStorage.isWithinDistance(x1, z1 + 1, x2, z2, distance)) {
            return true;
        }
        if (!ThreadedAnvilChunkStorage.isWithinDistance(x1 - 1, z1, x2, z2, distance)) {
            return true;
        }
        return !ThreadedAnvilChunkStorage.isWithinDistance(x1, z1 - 1, x2, z2, distance);
    }

    protected ServerLightingProvider getLightingProvider() {
        return this.lightingProvider;
    }

    @Nullable
    protected ChunkHolder getCurrentChunkHolder(long pos) {
        return (ChunkHolder)this.currentChunkHolders.get(pos);
    }

    @Nullable
    protected ChunkHolder getChunkHolder(long pos) {
        return (ChunkHolder)this.chunkHolders.get(pos);
    }

    protected IntSupplier getCompletedLevelSupplier(long pos) {
        return () -> {
            ChunkHolder chunkHolder = this.getChunkHolder(pos);
            if (chunkHolder == null) {
                return LevelPrioritizedQueue.LEVEL_COUNT - 1;
            }
            return Math.min(chunkHolder.getCompletedLevel(), LevelPrioritizedQueue.LEVEL_COUNT - 1);
        };
    }

    public String getChunkLoadingDebugInfo(ChunkPos chunkPos) {
        ChunkHolder chunkHolder = this.getChunkHolder(chunkPos.toLong());
        if (chunkHolder == null) {
            return "null";
        }
        String string = chunkHolder.getLevel() + "\n";
        ChunkStatus chunkStatus = chunkHolder.getCurrentStatus();
        Chunk chunk = chunkHolder.getCurrentChunk();
        if (chunkStatus != null) {
            string = string + "St: \u00a7" + chunkStatus.getIndex() + chunkStatus + "\u00a7r\n";
        }
        if (chunk != null) {
            string = string + "Ch: \u00a7" + chunk.getStatus().getIndex() + chunk.getStatus() + "\u00a7r\n";
        }
        ChunkHolder.LevelType levelType = chunkHolder.getLevelType();
        string = string + "\u00a7" + levelType.ordinal() + levelType;
        return string + "\u00a7r";
    }

    private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> getRegion(ChunkPos centerChunk, final int margin, IntFunction<ChunkStatus> distanceToStatus) {
        ArrayList<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> list = new ArrayList<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>>();
        ArrayList<ChunkHolder> list2 = new ArrayList<ChunkHolder>();
        final int i = centerChunk.x;
        final int j = centerChunk.z;
        for (int k = -margin; k <= margin; ++k) {
            for (int l = -margin; l <= margin; ++l) {
                int m = Math.max(Math.abs(l), Math.abs(k));
                final ChunkPos chunkPos = new ChunkPos(i + l, j + k);
                long n = chunkPos.toLong();
                ChunkHolder chunkHolder = this.getCurrentChunkHolder(n);
                if (chunkHolder == null) {
                    return CompletableFuture.completedFuture(Either.right((Object)new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + chunkPos;
                        }
                    }));
                }
                ChunkStatus chunkStatus = distanceToStatus.apply(m);
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getChunkAt(chunkStatus, this);
                list2.add(chunkHolder);
                list.add(completableFuture);
            }
        }
        CompletableFuture completableFuture2 = Util.combineSafe(list);
        CompletionStage completableFuture3 = completableFuture2.thenApply(chunks -> {
            ArrayList list = Lists.newArrayList();
            int l = 0;
            for (final Either either : chunks) {
                if (either == null) {
                    throw this.crash(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
                }
                Optional optional = either.left();
                if (!optional.isPresent()) {
                    final int m = l;
                    return Either.right((Object)new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + new ChunkPos(i + m % (margin * 2 + 1), j + m / (margin * 2 + 1)) + " " + either.right().get();
                        }
                    });
                }
                list.add((Chunk)optional.get());
                ++l;
            }
            return Either.left((Object)list);
        });
        for (ChunkHolder chunkHolder2 : list2) {
            chunkHolder2.combineSavingFuture("getChunkRangeFuture " + centerChunk + " " + margin, (CompletableFuture<?>)completableFuture3);
        }
        return completableFuture3;
    }

    public CrashException crash(IllegalStateException exception, String details) {
        StringBuilder stringBuilder = new StringBuilder();
        Consumer<ChunkHolder> consumer = chunkHolder -> chunkHolder.collectFuturesByStatus().forEach(pair -> {
            ChunkStatus chunkStatus = (ChunkStatus)pair.getFirst();
            CompletableFuture completableFuture = (CompletableFuture)pair.getSecond();
            if (completableFuture != null && completableFuture.isDone() && completableFuture.join() == null) {
                stringBuilder.append(chunkHolder.getPos()).append(" - status: ").append(chunkStatus).append(" future: ").append(completableFuture).append(System.lineSeparator());
            }
        });
        stringBuilder.append("Updating:").append(System.lineSeparator());
        this.currentChunkHolders.values().forEach(consumer);
        stringBuilder.append("Visible:").append(System.lineSeparator());
        this.chunkHolders.values().forEach(consumer);
        CrashReport crashReport = CrashReport.create(exception, "Chunk loading");
        CrashReportSection crashReportSection = crashReport.addElement("Chunk loading");
        crashReportSection.add("Details", details);
        crashReportSection.add("Futures", stringBuilder);
        return new CrashException(crashReport);
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkEntitiesTickable(ChunkPos pos) {
        return this.getRegion(pos, 2, distance -> ChunkStatus.FULL).thenApplyAsync(either -> either.mapLeft(chunks -> (WorldChunk)chunks.get(chunks.size() / 2)), (Executor)this.mainThreadExecutor);
    }

    @Nullable
    ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
        if (i > MAX_LEVEL && level > MAX_LEVEL) {
            return holder;
        }
        if (holder != null) {
            holder.setLevel(level);
        }
        if (holder != null) {
            if (level > MAX_LEVEL) {
                this.unloadedChunks.add(pos);
            } else {
                this.unloadedChunks.remove(pos);
            }
        }
        if (level <= MAX_LEVEL && holder == null) {
            holder = (ChunkHolder)this.chunksToUnload.remove(pos);
            if (holder != null) {
                holder.setLevel(level);
            } else {
                holder = new ChunkHolder(new ChunkPos(pos), level, this.world, this.lightingProvider, this.chunkTaskPrioritySystem, this);
            }
            this.currentChunkHolders.put(pos, (Object)holder);
            this.chunkHolderListDirty = true;
        }
        return holder;
    }

    @Override
    public void close() throws IOException {
        try {
            this.chunkTaskPrioritySystem.close();
            this.pointOfInterestStorage.close();
        }
        finally {
            super.close();
        }
    }

    protected void save(boolean flush) {
        if (flush) {
            List list = this.chunkHolders.values().stream().filter(ChunkHolder::isAccessible).peek(ChunkHolder::updateAccessibleStatus).collect(Collectors.toList());
            MutableBoolean mutableBoolean = new MutableBoolean();
            do {
                mutableBoolean.setFalse();
                list.stream().map(chunkHolder -> {
                    CompletableFuture<Chunk> completableFuture;
                    do {
                        completableFuture = chunkHolder.getSavingFuture();
                        this.mainThreadExecutor.runTasks(completableFuture::isDone);
                    } while (completableFuture != chunkHolder.getSavingFuture());
                    return completableFuture.join();
                }).filter(chunk -> chunk instanceof ReadOnlyChunk || chunk instanceof WorldChunk).filter(this::save).forEach(chunk -> mutableBoolean.setTrue());
            } while (mutableBoolean.isTrue());
            this.unloadChunks(() -> true);
            this.completeAll();
        } else {
            this.chunkHolders.values().forEach(this::save);
        }
    }

    protected void tick(BooleanSupplier shouldKeepTicking) {
        Profiler profiler = this.world.getProfiler();
        profiler.push("poi");
        this.pointOfInterestStorage.tick(shouldKeepTicking);
        profiler.swap("chunk_unload");
        if (!this.world.isSavingDisabled()) {
            this.unloadChunks(shouldKeepTicking);
        }
        profiler.pop();
    }

    public boolean shouldDelayShutdown() {
        return this.lightingProvider.hasUpdates() || !this.chunksToUnload.isEmpty() || !this.currentChunkHolders.isEmpty() || this.pointOfInterestStorage.hasUnsavedElements() || !this.unloadedChunks.isEmpty() || !this.unloadTaskQueue.isEmpty() || this.chunkTaskPrioritySystem.shouldDelayShutdown() || this.ticketManager.shouldDelayShutdown();
    }

    private void unloadChunks(BooleanSupplier shouldKeepTicking) {
        Runnable runnable;
        LongIterator longIterator = this.unloadedChunks.iterator();
        int i = 0;
        while (longIterator.hasNext() && (shouldKeepTicking.getAsBoolean() || i < 200 || this.unloadedChunks.size() > 2000)) {
            long l = longIterator.nextLong();
            ChunkHolder chunkHolder = (ChunkHolder)this.currentChunkHolders.remove(l);
            if (chunkHolder != null) {
                this.chunksToUnload.put(l, (Object)chunkHolder);
                this.chunkHolderListDirty = true;
                ++i;
                this.tryUnloadChunk(l, chunkHolder);
            }
            longIterator.remove();
        }
        for (int j = Math.max(0, this.unloadTaskQueue.size() - 2000); (shouldKeepTicking.getAsBoolean() || j > 0) && (runnable = this.unloadTaskQueue.poll()) != null; --j) {
            runnable.run();
        }
        int k = 0;
        ObjectIterator objectIterator = this.chunkHolders.values().iterator();
        while (k < 20 && shouldKeepTicking.getAsBoolean() && objectIterator.hasNext()) {
            if (!this.save((ChunkHolder)objectIterator.next())) continue;
            ++k;
        }
    }

    private void tryUnloadChunk(long pos, ChunkHolder holder) {
        CompletableFuture<Chunk> completableFuture = holder.getSavingFuture();
        ((CompletableFuture)completableFuture.thenAcceptAsync(chunk -> {
            CompletableFuture<Chunk> completableFuture2 = holder.getSavingFuture();
            if (completableFuture2 != completableFuture) {
                this.tryUnloadChunk(pos, holder);
                return;
            }
            if (this.chunksToUnload.remove(pos, (Object)holder) && chunk != null) {
                if (chunk instanceof WorldChunk) {
                    ((WorldChunk)chunk).setLoadedToWorld(false);
                }
                this.save((Chunk)chunk);
                if (this.loadedChunks.remove(pos) && chunk instanceof WorldChunk) {
                    WorldChunk worldChunk = (WorldChunk)chunk;
                    this.world.unloadEntities(worldChunk);
                }
                this.lightingProvider.updateChunkStatus(chunk.getPos());
                this.lightingProvider.tick();
                this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);
                this.chunkToNextSaveTimeMs.remove(chunk.getPos().toLong());
            }
        }, this.unloadTaskQueue::add)).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk {}", (Object)holder.getPos(), throwable);
            }
        });
    }

    protected boolean updateHolderMap() {
        if (!this.chunkHolderListDirty) {
            return false;
        }
        this.chunkHolders = this.currentChunkHolders.clone();
        this.chunkHolderListDirty = false;
        return true;
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
        Optional optional;
        ChunkPos chunkPos = holder.getPos();
        if (requiredStatus == ChunkStatus.EMPTY) {
            return this.loadChunk(chunkPos);
        }
        if (requiredStatus == ChunkStatus.LIGHT) {
            this.ticketManager.addTicketWithLevel(ChunkTicketType.LIGHT, chunkPos, 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.LIGHT), chunkPos);
        }
        if ((optional = holder.getChunkAt(requiredStatus.getPrevious(), this).getNow(ChunkHolder.UNLOADED_CHUNK).left()).isPresent() && ((Chunk)optional.get()).getStatus().isAtLeast(requiredStatus)) {
            CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = requiredStatus.runLoadTask(this.world, this.structureTemplateManager, this.lightingProvider, chunk -> this.convertToFullChunk(holder), (Chunk)optional.get());
            this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
            return completableFuture;
        }
        return this.upgradeChunk(holder, requiredStatus);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
        return ((CompletableFuture)((CompletableFuture)this.getUpdatedChunkNbt(pos).thenApply(nbt -> nbt.filter(nbt2 -> {
            boolean bl = ThreadedAnvilChunkStorage.containsStatus(nbt2);
            if (!bl) {
                LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)pos);
            }
            return bl;
        }))).thenApplyAsync(nbt -> {
            this.world.getProfiler().visit("chunkLoad");
            if (nbt.isPresent()) {
                ProtoChunk chunk = ChunkSerializer.deserialize(this.world, this.pointOfInterestStorage, pos, (NbtCompound)nbt.get());
                this.mark(pos, ((Chunk)chunk).getStatus().getChunkType());
                return Either.left((Object)chunk);
            }
            return Either.left((Object)this.getProtoChunk(pos));
        }, (Executor)this.mainThreadExecutor)).exceptionallyAsync(throwable -> this.recoverFromException((Throwable)throwable, pos), (Executor)this.mainThreadExecutor);
    }

    private static boolean containsStatus(NbtCompound nbt) {
        return nbt.contains("Status", 8);
    }

    /*
     * Enabled aggressive block sorting
     */
    private Either<Chunk, ChunkHolder.Unloaded> recoverFromException(Throwable throwable, ChunkPos chunkPos) {
        if (!(throwable instanceof CrashException)) {
            if (!(throwable instanceof IOException)) return Either.left((Object)this.getProtoChunk(chunkPos));
            LOGGER.error("Couldn't load chunk {}", (Object)chunkPos, (Object)throwable);
            return Either.left((Object)this.getProtoChunk(chunkPos));
        }
        CrashException crashException = (CrashException)throwable;
        Throwable throwable2 = crashException.getCause();
        if (throwable2 instanceof IOException) {
            LOGGER.error("Couldn't load chunk {}", (Object)chunkPos, (Object)throwable2);
            return Either.left((Object)this.getProtoChunk(chunkPos));
        }
        this.markAsProtoChunk(chunkPos);
        throw crashException;
    }

    private Chunk getProtoChunk(ChunkPos chunkPos) {
        this.markAsProtoChunk(chunkPos);
        return new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA, this.world, this.world.getRegistryManager().get(RegistryKeys.BIOME), null);
    }

    private void markAsProtoChunk(ChunkPos pos) {
        this.chunkToType.put(pos.toLong(), (byte)-1);
    }

    private byte mark(ChunkPos pos, ChunkStatus.ChunkType type) {
        return this.chunkToType.put(pos.toLong(), type == ChunkStatus.ChunkType.PROTOCHUNK ? (byte)-1 : 1);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> upgradeChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
        ChunkPos chunkPos = holder.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getRegion(chunkPos, requiredStatus.getTaskMargin(), distance -> this.getRequiredStatusForGeneration(requiredStatus, distance));
        this.world.getProfiler().visit(() -> "chunkGenerate " + requiredStatus.getId());
        Executor executor = task -> this.worldGenExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task));
        return completableFuture.thenComposeAsync(either -> (CompletionStage)either.map(chunks -> {
            try {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = requiredStatus.runGenerationTask(executor, this.world, this.chunkGenerator, this.structureTemplateManager, this.lightingProvider, chunk -> this.convertToFullChunk(holder), (List<Chunk>)chunks, false);
                this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
                return completableFuture;
            }
            catch (Exception exception) {
                exception.getStackTrace();
                CrashReport crashReport = CrashReport.create(exception, "Exception generating new chunk");
                CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
                crashReportSection.add("Location", String.format(Locale.ROOT, "%d,%d", chunkPos.x, chunkPos.z));
                crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
                crashReportSection.add("Generator", this.chunkGenerator);
                this.mainThreadExecutor.execute(() -> {
                    throw new CrashException(crashReport);
                });
                throw new CrashException(crashReport);
            }
        }, unloaded -> {
            this.releaseLightTicket(chunkPos);
            return CompletableFuture.completedFuture(Either.right((Object)unloaded));
        }), executor);
    }

    protected void releaseLightTicket(ChunkPos pos) {
        this.mainThreadExecutor.send(Util.debugRunnable(() -> this.ticketManager.removeTicketWithLevel(ChunkTicketType.LIGHT, pos, 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.LIGHT), pos), () -> "release light ticket " + pos));
    }

    private ChunkStatus getRequiredStatusForGeneration(ChunkStatus centerChunkTargetStatus, int distance) {
        ChunkStatus chunkStatus = distance == 0 ? centerChunkTargetStatus.getPrevious() : ChunkStatus.byDistanceFromFull(ChunkStatus.getDistanceFromFull(centerChunkTargetStatus) + distance);
        return chunkStatus;
    }

    private static void addEntitiesFromNbt(ServerWorld world, List<NbtCompound> nbt) {
        if (!nbt.isEmpty()) {
            world.addEntities(EntityType.streamFromNbt(nbt, world));
        }
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> convertToFullChunk(ChunkHolder chunkHolder) {
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getFutureFor(ChunkStatus.FULL.getPrevious());
        return completableFuture.thenApplyAsync(either -> {
            ChunkStatus chunkStatus = ChunkHolder.getTargetStatusForLevel(chunkHolder.getLevel());
            if (!chunkStatus.isAtLeast(ChunkStatus.FULL)) {
                return ChunkHolder.UNLOADED_CHUNK;
            }
            return either.mapLeft(protoChunk -> {
                WorldChunk worldChunk;
                ChunkPos chunkPos = chunkHolder.getPos();
                ProtoChunk protoChunk2 = (ProtoChunk)protoChunk;
                if (protoChunk2 instanceof ReadOnlyChunk) {
                    worldChunk = ((ReadOnlyChunk)protoChunk2).getWrappedChunk();
                } else {
                    worldChunk = new WorldChunk(this.world, protoChunk2, chunk -> ThreadedAnvilChunkStorage.addEntitiesFromNbt(this.world, protoChunk2.getEntities()));
                    chunkHolder.setCompletedChunk(new ReadOnlyChunk(worldChunk, false));
                }
                worldChunk.setLevelTypeProvider(() -> ChunkHolder.getLevelType(chunkHolder.getLevel()));
                worldChunk.loadEntities();
                if (this.loadedChunks.add(chunkPos.toLong())) {
                    worldChunk.setLoadedToWorld(true);
                    worldChunk.updateAllBlockEntities();
                    worldChunk.addChunkTickSchedulers(this.world);
                }
                return worldChunk;
            });
        }, task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(task, chunkHolder.getPos().toLong(), chunkHolder::getLevel)));
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkTickable(ChunkHolder holder) {
        ChunkPos chunkPos = holder.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getRegion(chunkPos, 1, i -> ChunkStatus.FULL);
        CompletionStage completableFuture2 = ((CompletableFuture)completableFuture.thenApplyAsync(either -> either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2)), task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task)))).thenApplyAsync(either -> either.ifLeft(chunk -> {
            chunk.runPostProcessing();
            this.world.disableTickSchedulers((WorldChunk)chunk);
        }), (Executor)this.mainThreadExecutor);
        ((CompletableFuture)completableFuture2).thenAcceptAsync(either -> either.ifLeft(chunk -> {
            this.totalChunksLoadedCount.getAndIncrement();
            MutableObject mutableObject = new MutableObject();
            this.getPlayersWatchingChunk(chunkPos, false).forEach(player -> this.sendChunkDataPackets((ServerPlayerEntity)player, (MutableObject<ChunkDataS2CPacket>)mutableObject, (WorldChunk)chunk));
        }), task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task)));
        return completableFuture2;
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkAccessible(ChunkHolder holder) {
        return this.getRegion(holder.getPos(), 1, ChunkStatus::byDistanceFromFull).thenApplyAsync(either -> either.mapLeft(chunks -> {
            WorldChunk worldChunk = (WorldChunk)chunks.get(chunks.size() / 2);
            return worldChunk;
        }), task -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, task)));
    }

    public int getTotalChunksLoadedCount() {
        return this.totalChunksLoadedCount.get();
    }

    private boolean save(ChunkHolder chunkHolder) {
        if (!chunkHolder.isAccessible()) {
            return false;
        }
        Chunk chunk = chunkHolder.getSavingFuture().getNow(null);
        if (chunk instanceof ReadOnlyChunk || chunk instanceof WorldChunk) {
            long l = chunk.getPos().toLong();
            long m = this.chunkToNextSaveTimeMs.getOrDefault(l, -1L);
            long n = System.currentTimeMillis();
            if (n < m) {
                return false;
            }
            boolean bl = this.save(chunk);
            chunkHolder.updateAccessibleStatus();
            if (bl) {
                this.chunkToNextSaveTimeMs.put(l, n + 10000L);
            }
            return bl;
        }
        return false;
    }

    private boolean save(Chunk chunk) {
        this.pointOfInterestStorage.saveChunk(chunk.getPos());
        if (!chunk.needsSaving()) {
            return false;
        }
        chunk.setNeedsSaving(false);
        ChunkPos chunkPos = chunk.getPos();
        try {
            ChunkStatus chunkStatus = chunk.getStatus();
            if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
                if (this.isLevelChunk(chunkPos)) {
                    return false;
                }
                if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
                    return false;
                }
            }
            this.world.getProfiler().visit("chunkSave");
            NbtCompound nbtCompound = ChunkSerializer.serialize(this.world, chunk);
            this.setNbt(chunkPos, nbtCompound);
            this.mark(chunkPos, chunkStatus.getChunkType());
            return true;
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", new Object[]{chunkPos.x, chunkPos.z, exception});
            return false;
        }
    }

    private boolean isLevelChunk(ChunkPos pos) {
        NbtCompound nbtCompound;
        byte b = this.chunkToType.get(pos.toLong());
        if (b != 0) {
            return b == 1;
        }
        try {
            nbtCompound = this.getUpdatedChunkNbt(pos).join().orElse(null);
            if (nbtCompound == null) {
                this.markAsProtoChunk(pos);
                return false;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to read chunk {}", (Object)pos, (Object)exception);
            this.markAsProtoChunk(pos);
            return false;
        }
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkType(nbtCompound);
        return this.mark(pos, chunkType) == 1;
    }

    protected void setViewDistance(int watchDistance) {
        int i = MathHelper.clamp(watchDistance + 1, 3, 33);
        if (i != this.watchDistance) {
            int j = this.watchDistance;
            this.watchDistance = i;
            this.ticketManager.setWatchDistance(this.watchDistance + 1);
            for (ChunkHolder chunkHolder : this.currentChunkHolders.values()) {
                ChunkPos chunkPos = chunkHolder.getPos();
                MutableObject mutableObject = new MutableObject();
                this.getPlayersWatchingChunk(chunkPos, false).forEach(player -> {
                    ChunkSectionPos chunkSectionPos = player.getWatchedSection();
                    boolean bl = ThreadedAnvilChunkStorage.isWithinDistance(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), j);
                    boolean bl2 = ThreadedAnvilChunkStorage.isWithinDistance(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), this.watchDistance);
                    this.sendWatchPackets((ServerPlayerEntity)player, chunkPos, (MutableObject<ChunkDataS2CPacket>)mutableObject, bl, bl2);
                });
            }
        }
    }

    protected void sendWatchPackets(ServerPlayerEntity player, ChunkPos pos, MutableObject<ChunkDataS2CPacket> packet, boolean oldWithinViewDistance, boolean newWithinViewDistance) {
        ChunkHolder chunkHolder;
        if (player.world != this.world) {
            return;
        }
        if (newWithinViewDistance && !oldWithinViewDistance && (chunkHolder = this.getChunkHolder(pos.toLong())) != null) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk != null) {
                this.sendChunkDataPackets(player, packet, worldChunk);
            }
            DebugInfoSender.sendChunkWatchingChange(this.world, pos);
        }
        if (!newWithinViewDistance && oldWithinViewDistance) {
            player.sendUnloadChunkPacket(pos);
        }
    }

    public int getLoadedChunkCount() {
        return this.chunkHolders.size();
    }

    public ChunkTicketManager getTicketManager() {
        return this.ticketManager;
    }

    protected Iterable<ChunkHolder> entryIterator() {
        return Iterables.unmodifiableIterable((Iterable)this.chunkHolders.values());
    }

    void dump(Writer writer) throws IOException {
        CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("block_entity_count").addColumn("ticking_ticket").addColumn("ticking_level").addColumn("block_ticks").addColumn("fluid_ticks").startBody(writer);
        SimulationDistanceLevelPropagator simulationDistanceLevelPropagator = this.ticketManager.getSimulationDistanceTracker();
        for (Long2ObjectMap.Entry entry : this.chunkHolders.long2ObjectEntrySet()) {
            long l = entry.getLongKey();
            ChunkPos chunkPos = new ChunkPos(l);
            ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
            Optional<Chunk> optional = Optional.ofNullable(chunkHolder.getCurrentChunk());
            Optional<Object> optional2 = optional.flatMap(chunk -> chunk instanceof WorldChunk ? Optional.of((WorldChunk)chunk) : Optional.empty());
            csvWriter.printRow(chunkPos.x, chunkPos.z, chunkHolder.getLevel(), optional.isPresent(), optional.map(Chunk::getStatus).orElse(null), optional2.map(WorldChunk::getLevelType).orElse(null), ThreadedAnvilChunkStorage.getFutureStatus(chunkHolder.getAccessibleFuture()), ThreadedAnvilChunkStorage.getFutureStatus(chunkHolder.getTickingFuture()), ThreadedAnvilChunkStorage.getFutureStatus(chunkHolder.getEntityTickingFuture()), this.ticketManager.getTicket(l), this.shouldTick(chunkPos), optional2.map(chunk -> chunk.getBlockEntities().size()).orElse(0), simulationDistanceLevelPropagator.getTickingTicket(l), simulationDistanceLevelPropagator.getLevel(l), optional2.map(chunk -> chunk.getBlockTickScheduler().getTickCount()).orElse(0), optional2.map(chunk -> chunk.getFluidTickScheduler().getTickCount()).orElse(0));
        }
    }

    private static String getFutureStatus(CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> future) {
        try {
            Either either = future.getNow(null);
            if (either != null) {
                return (String)either.map(chunk -> "done", unloaded -> "unloaded");
            }
            return "not completed";
        }
        catch (CompletionException completionException) {
            return "failed " + completionException.getCause().getMessage();
        }
        catch (CancellationException cancellationException) {
            return "cancelled";
        }
    }

    private CompletableFuture<Optional<NbtCompound>> getUpdatedChunkNbt(ChunkPos chunkPos) {
        return this.getNbt(chunkPos).thenApplyAsync(nbt -> nbt.map(this::updateChunkNbt), (Executor)Util.getMainWorkerExecutor());
    }

    private NbtCompound updateChunkNbt(NbtCompound nbt) {
        return this.updateChunkNbt(this.world.getRegistryKey(), this.persistentStateManagerFactory, nbt, this.chunkGenerator.getCodecKey());
    }

    boolean shouldTick(ChunkPos pos) {
        long l = pos.toLong();
        if (!this.ticketManager.shouldTick(l)) {
            return false;
        }
        for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk(l)) {
            if (!this.canTickChunk(serverPlayerEntity, pos)) continue;
            return true;
        }
        return false;
    }

    public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos pos) {
        long l = pos.toLong();
        if (!this.ticketManager.shouldTick(l)) {
            return List.of();
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (ServerPlayerEntity serverPlayerEntity : this.playerChunkWatchingManager.getPlayersWatchingChunk(l)) {
            if (!this.canTickChunk(serverPlayerEntity, pos)) continue;
            builder.add((Object)serverPlayerEntity);
        }
        return builder.build();
    }

    private boolean canTickChunk(ServerPlayerEntity player, ChunkPos pos) {
        if (player.isSpectator()) {
            return false;
        }
        double d = ThreadedAnvilChunkStorage.getSquaredDistance(pos, player);
        return d < 16384.0;
    }

    private boolean doesNotGenerateChunks(ServerPlayerEntity player) {
        return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
    }

    void handlePlayerAddedOrRemoved(ServerPlayerEntity player, boolean added) {
        boolean bl = this.doesNotGenerateChunks(player);
        boolean bl2 = this.playerChunkWatchingManager.isWatchInactive(player);
        int i = ChunkSectionPos.getSectionCoord(player.getBlockX());
        int j = ChunkSectionPos.getSectionCoord(player.getBlockZ());
        if (added) {
            this.playerChunkWatchingManager.add(ChunkPos.toLong(i, j), player, bl);
            this.updateWatchedSection(player);
            if (!bl) {
                this.ticketManager.handleChunkEnter(ChunkSectionPos.from(player), player);
            }
        } else {
            ChunkSectionPos chunkSectionPos = player.getWatchedSection();
            this.playerChunkWatchingManager.remove(chunkSectionPos.toChunkPos().toLong(), player);
            if (!bl2) {
                this.ticketManager.handleChunkLeave(chunkSectionPos, player);
            }
        }
        for (int k = i - this.watchDistance - 1; k <= i + this.watchDistance + 1; ++k) {
            for (int l = j - this.watchDistance - 1; l <= j + this.watchDistance + 1; ++l) {
                if (!ThreadedAnvilChunkStorage.isWithinDistance(k, l, i, j, this.watchDistance)) continue;
                ChunkPos chunkPos = new ChunkPos(k, l);
                this.sendWatchPackets(player, chunkPos, (MutableObject<ChunkDataS2CPacket>)new MutableObject(), !added, added);
            }
        }
    }

    private ChunkSectionPos updateWatchedSection(ServerPlayerEntity player) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(player);
        player.setWatchedSection(chunkSectionPos);
        player.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ()));
        return chunkSectionPos;
    }

    public void updatePosition(ServerPlayerEntity player) {
        boolean bl3;
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            if (entityTracker.entity == player) {
                entityTracker.updateTrackedStatus(this.world.getPlayers());
                continue;
            }
            entityTracker.updateTrackedStatus(player);
        }
        int i = ChunkSectionPos.getSectionCoord(player.getBlockX());
        int j = ChunkSectionPos.getSectionCoord(player.getBlockZ());
        ChunkSectionPos chunkSectionPos = player.getWatchedSection();
        ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(player);
        long l = chunkSectionPos.toChunkPos().toLong();
        long m = chunkSectionPos2.toChunkPos().toLong();
        boolean bl = this.playerChunkWatchingManager.isWatchDisabled(player);
        boolean bl2 = this.doesNotGenerateChunks(player);
        boolean bl4 = bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();
        if (bl3 || bl != bl2) {
            this.updateWatchedSection(player);
            if (!bl) {
                this.ticketManager.handleChunkLeave(chunkSectionPos, player);
            }
            if (!bl2) {
                this.ticketManager.handleChunkEnter(chunkSectionPos2, player);
            }
            if (!bl && bl2) {
                this.playerChunkWatchingManager.disableWatch(player);
            }
            if (bl && !bl2) {
                this.playerChunkWatchingManager.enableWatch(player);
            }
            if (l != m) {
                this.playerChunkWatchingManager.movePlayer(l, m, player);
            }
        }
        int k = chunkSectionPos.getSectionX();
        int n = chunkSectionPos.getSectionZ();
        if (Math.abs(k - i) <= this.watchDistance * 2 && Math.abs(n - j) <= this.watchDistance * 2) {
            int o = Math.min(i, k) - this.watchDistance - 1;
            int p = Math.min(j, n) - this.watchDistance - 1;
            int q = Math.max(i, k) + this.watchDistance + 1;
            int r = Math.max(j, n) + this.watchDistance + 1;
            for (int s = o; s <= q; ++s) {
                for (int t = p; t <= r; ++t) {
                    boolean bl42 = ThreadedAnvilChunkStorage.isWithinDistance(s, t, k, n, this.watchDistance);
                    boolean bl5 = ThreadedAnvilChunkStorage.isWithinDistance(s, t, i, j, this.watchDistance);
                    this.sendWatchPackets(player, new ChunkPos(s, t), (MutableObject<ChunkDataS2CPacket>)new MutableObject(), bl42, bl5);
                }
            }
        } else {
            boolean bl7;
            boolean bl6;
            int p;
            int o;
            for (o = k - this.watchDistance - 1; o <= k + this.watchDistance + 1; ++o) {
                for (p = n - this.watchDistance - 1; p <= n + this.watchDistance + 1; ++p) {
                    if (!ThreadedAnvilChunkStorage.isWithinDistance(o, p, k, n, this.watchDistance)) continue;
                    bl6 = true;
                    bl7 = false;
                    this.sendWatchPackets(player, new ChunkPos(o, p), (MutableObject<ChunkDataS2CPacket>)new MutableObject(), true, false);
                }
            }
            for (o = i - this.watchDistance - 1; o <= i + this.watchDistance + 1; ++o) {
                for (p = j - this.watchDistance - 1; p <= j + this.watchDistance + 1; ++p) {
                    if (!ThreadedAnvilChunkStorage.isWithinDistance(o, p, i, j, this.watchDistance)) continue;
                    bl6 = false;
                    bl7 = true;
                    this.sendWatchPackets(player, new ChunkPos(o, p), (MutableObject<ChunkDataS2CPacket>)new MutableObject(), false, true);
                }
            }
        }
    }

    @Override
    public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
        Set<ServerPlayerEntity> set = this.playerChunkWatchingManager.getPlayersWatchingChunk(chunkPos.toLong());
        ImmutableList.Builder builder = ImmutableList.builder();
        for (ServerPlayerEntity serverPlayerEntity : set) {
            ChunkSectionPos chunkSectionPos = serverPlayerEntity.getWatchedSection();
            if ((!onlyOnWatchDistanceEdge || !ThreadedAnvilChunkStorage.isOnDistanceEdge(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), this.watchDistance)) && (onlyOnWatchDistanceEdge || !ThreadedAnvilChunkStorage.isWithinDistance(chunkPos.x, chunkPos.z, chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), this.watchDistance))) continue;
            builder.add((Object)serverPlayerEntity);
        }
        return builder.build();
    }

    protected void loadEntity(Entity entity) {
        if (entity instanceof EnderDragonPart) {
            return;
        }
        EntityType<?> entityType = entity.getType();
        int i = entityType.getMaxTrackDistance() * 16;
        if (i == 0) {
            return;
        }
        int j = entityType.getTrackTickInterval();
        if (this.entityTrackers.containsKey(entity.getId())) {
            throw Util.throwOrPause(new IllegalStateException("Entity is already tracked!"));
        }
        EntityTracker entityTracker = new EntityTracker(entity, i, j, entityType.alwaysUpdateVelocity());
        this.entityTrackers.put(entity.getId(), (Object)entityTracker);
        entityTracker.updateTrackedStatus(this.world.getPlayers());
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.handlePlayerAddedOrRemoved(serverPlayerEntity, true);
            for (EntityTracker entityTracker2 : this.entityTrackers.values()) {
                if (entityTracker2.entity == serverPlayerEntity) continue;
                entityTracker2.updateTrackedStatus(serverPlayerEntity);
            }
        }
    }

    protected void unloadEntity(Entity entity) {
        EntityTracker entityTracker2;
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.handlePlayerAddedOrRemoved(serverPlayerEntity, false);
            for (EntityTracker entityTracker : this.entityTrackers.values()) {
                entityTracker.stopTracking(serverPlayerEntity);
            }
        }
        if ((entityTracker2 = (EntityTracker)this.entityTrackers.remove(entity.getId())) != null) {
            entityTracker2.stopTracking();
        }
    }

    protected void tickEntityMovement() {
        ArrayList list = Lists.newArrayList();
        List<ServerPlayerEntity> list2 = this.world.getPlayers();
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            boolean bl;
            ChunkSectionPos chunkSectionPos = entityTracker.trackedSection;
            ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(entityTracker.entity);
            boolean bl2 = bl = !Objects.equals(chunkSectionPos, chunkSectionPos2);
            if (bl) {
                entityTracker.updateTrackedStatus(list2);
                Entity entity = entityTracker.entity;
                if (entity instanceof ServerPlayerEntity) {
                    list.add((ServerPlayerEntity)entity);
                }
                entityTracker.trackedSection = chunkSectionPos2;
            }
            if (!bl && !this.ticketManager.shouldTickEntities(chunkSectionPos2.toChunkPos().toLong())) continue;
            entityTracker.entry.tick();
        }
        if (!list.isEmpty()) {
            for (EntityTracker entityTracker : this.entityTrackers.values()) {
                entityTracker.updateTrackedStatus(list);
            }
        }
    }

    public void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getId());
        if (entityTracker != null) {
            entityTracker.sendToOtherNearbyPlayers(packet);
        }
    }

    protected void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getId());
        if (entityTracker != null) {
            entityTracker.sendToNearbyPlayers(packet);
        }
    }

    public void sendChunkPacketToWatchingPlayers(Chunk chunk) {
        WorldChunk worldChunk;
        ChunkPos chunkPos = chunk.getPos();
        WorldChunk worldChunk2 = chunk instanceof WorldChunk ? (worldChunk = (WorldChunk)chunk) : this.world.getChunk(chunkPos.x, chunkPos.z);
        MutableObject mutableObject = new MutableObject();
        for (ServerPlayerEntity serverPlayerEntity : this.getPlayersWatchingChunk(chunkPos, false)) {
            if (mutableObject.getValue() == null) {
                mutableObject.setValue((Object)new ChunkDataS2CPacket(worldChunk2, this.lightingProvider, null, null, true));
            }
            serverPlayerEntity.sendChunkPacket(chunkPos, (Packet)mutableObject.getValue());
        }
    }

    private void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> cachedDataPacket, WorldChunk chunk) {
        if (cachedDataPacket.getValue() == null) {
            cachedDataPacket.setValue((Object)new ChunkDataS2CPacket(chunk, this.lightingProvider, null, null, true));
        }
        player.sendChunkPacket(chunk.getPos(), (Packet)cachedDataPacket.getValue());
        DebugInfoSender.sendChunkWatchingChange(this.world, chunk.getPos());
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            Entity entity = entityTracker.entity;
            if (entity == player || !entity.getChunkPos().equals(chunk.getPos())) continue;
            entityTracker.updateTrackedStatus(player);
            if (entity instanceof MobEntity && ((MobEntity)entity).getHoldingEntity() != null) {
                list.add(entity);
            }
            if (entity.getPassengerList().isEmpty()) continue;
            list2.add(entity);
        }
        if (!list.isEmpty()) {
            for (Entity entity2 : list) {
                player.networkHandler.sendPacket(new EntityAttachS2CPacket(entity2, ((MobEntity)entity2).getHoldingEntity()));
            }
        }
        if (!list2.isEmpty()) {
            for (Entity entity2 : list2) {
                player.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity2));
            }
        }
    }

    protected PointOfInterestStorage getPointOfInterestStorage() {
        return this.pointOfInterestStorage;
    }

    public String getSaveDir() {
        return this.saveDir;
    }

    void onChunkStatusChange(ChunkPos chunkPos, ChunkHolder.LevelType levelType) {
        this.chunkStatusChangeListener.onChunkStatusChange(chunkPos, levelType);
    }

    class TicketManager
    extends ChunkTicketManager {
        protected TicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
            super(workerExecutor, mainThreadExecutor);
        }

        @Override
        protected boolean isUnloaded(long pos) {
            return ThreadedAnvilChunkStorage.this.unloadedChunks.contains(pos);
        }

        @Override
        @Nullable
        protected ChunkHolder getChunkHolder(long pos) {
            return ThreadedAnvilChunkStorage.this.getCurrentChunkHolder(pos);
        }

        @Override
        @Nullable
        protected ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
            return ThreadedAnvilChunkStorage.this.setLevel(pos, level, holder, i);
        }
    }

    class EntityTracker {
        final EntityTrackerEntry entry;
        final Entity entity;
        private final int maxDistance;
        ChunkSectionPos trackedSection;
        private final Set<EntityTrackingListener> listeners = Sets.newIdentityHashSet();

        public EntityTracker(Entity entity, int maxDistance, int tickInterval, boolean alwaysUpdateVelocity) {
            this.entry = new EntityTrackerEntry(ThreadedAnvilChunkStorage.this.world, entity, tickInterval, alwaysUpdateVelocity, this::sendToOtherNearbyPlayers);
            this.entity = entity;
            this.maxDistance = maxDistance;
            this.trackedSection = ChunkSectionPos.from(entity);
        }

        public boolean equals(Object o) {
            if (o instanceof EntityTracker) {
                return ((EntityTracker)o).entity.getId() == this.entity.getId();
            }
            return false;
        }

        public int hashCode() {
            return this.entity.getId();
        }

        public void sendToOtherNearbyPlayers(Packet<?> packet) {
            for (EntityTrackingListener entityTrackingListener : this.listeners) {
                entityTrackingListener.sendPacket(packet);
            }
        }

        public void sendToNearbyPlayers(Packet<?> packet) {
            this.sendToOtherNearbyPlayers(packet);
            if (this.entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
            }
        }

        public void stopTracking() {
            for (EntityTrackingListener entityTrackingListener : this.listeners) {
                this.entry.stopTracking(entityTrackingListener.getPlayer());
            }
        }

        public void stopTracking(ServerPlayerEntity player) {
            if (this.listeners.remove(player.networkHandler)) {
                this.entry.stopTracking(player);
            }
        }

        public void updateTrackedStatus(ServerPlayerEntity player) {
            boolean bl;
            if (player == this.entity) {
                return;
            }
            Vec3d vec3d = player.getPos().subtract(this.entity.getPos());
            double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            double d = Math.min(this.getMaxTrackDistance(), (ThreadedAnvilChunkStorage.this.watchDistance - 1) * 16);
            double f = d * d;
            boolean bl2 = bl = e <= f && this.entity.canBeSpectated(player);
            if (bl) {
                if (this.listeners.add(player.networkHandler)) {
                    this.entry.startTracking(player);
                }
            } else if (this.listeners.remove(player.networkHandler)) {
                this.entry.stopTracking(player);
            }
        }

        private int adjustTrackingDistance(int initialDistance) {
            return ThreadedAnvilChunkStorage.this.world.getServer().adjustTrackingDistance(initialDistance);
        }

        private int getMaxTrackDistance() {
            int i = this.maxDistance;
            for (Entity entity : this.entity.getPassengersDeep()) {
                int j = entity.getType().getMaxTrackDistance() * 16;
                if (j <= i) continue;
                i = j;
            }
            return this.adjustTrackingDistance(i);
        }

        public void updateTrackedStatus(List<ServerPlayerEntity> players) {
            for (ServerPlayerEntity serverPlayerEntity : players) {
                this.updateTrackedStatus(serverPlayerEntity);
            }
        }
    }
}

