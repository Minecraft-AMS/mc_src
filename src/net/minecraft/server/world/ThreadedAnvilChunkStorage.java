/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ThreadedAnvilChunkStorage
extends VersionedChunkStorage
implements ChunkHolder.PlayersWatchingChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_LEVEL = 33 + ChunkStatus.getMaxDistanceFromFull();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders = new Long2ObjectLinkedOpenHashMap();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders = this.currentChunkHolders.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload = new Long2ObjectLinkedOpenHashMap();
    private final LongSet loadedChunks = new LongOpenHashSet();
    private final ServerWorld world;
    private final ServerLightingProvider serverLightingProvider;
    private final ThreadExecutor<Runnable> mainThreadExecutor;
    private final ChunkGenerator chunkGenerator;
    private final Supplier<PersistentStateManager> persistentStateManagerFactory;
    private final PointOfInterestStorage pointOfInterestStorage;
    private final LongSet unloadedChunks = new LongOpenHashSet();
    private boolean chunkHolderListDirty;
    private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> worldGenExecutor;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> mainExecutor;
    private final WorldGenerationProgressListener worldGenerationProgressListener;
    private final TicketManager ticketManager;
    private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();
    private final StructureManager structureManager;
    private final File saveDir;
    private final PlayerChunkWatchingManager playerChunkWatchingManager = new PlayerChunkWatchingManager();
    private final Int2ObjectMap<EntityTracker> entityTrackers = new Int2ObjectOpenHashMap();
    private final Long2ByteMap chunkToType = new Long2ByteOpenHashMap();
    private final Queue<Runnable> unloadTaskQueue = Queues.newConcurrentLinkedQueue();
    private int watchDistance;

    public ThreadedAnvilChunkStorage(ServerWorld serverWorld, LevelStorage.Session session, DataFixer dataFixer, StructureManager structureManager, Executor workerExecutor, ThreadExecutor<Runnable> mainThreadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, Supplier<PersistentStateManager> supplier, int i, boolean bl) {
        super(new File(session.getWorldDirectory(serverWorld.getRegistryKey()), "region"), dataFixer, bl);
        this.structureManager = structureManager;
        this.saveDir = session.getWorldDirectory(serverWorld.getRegistryKey());
        this.world = serverWorld;
        this.chunkGenerator = chunkGenerator;
        this.mainThreadExecutor = mainThreadExecutor;
        TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(workerExecutor, "worldgen");
        MessageListener<Runnable> messageListener = MessageListener.create("main", mainThreadExecutor::send);
        this.worldGenerationProgressListener = worldGenerationProgressListener;
        TaskExecutor<Runnable> taskExecutor2 = TaskExecutor.create(workerExecutor, "light");
        this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(taskExecutor, messageListener, taskExecutor2), workerExecutor, Integer.MAX_VALUE);
        this.worldGenExecutor = this.chunkTaskPrioritySystem.createExecutor(taskExecutor, false);
        this.mainExecutor = this.chunkTaskPrioritySystem.createExecutor(messageListener, false);
        this.serverLightingProvider = new ServerLightingProvider(chunkProvider, this, this.world.getDimension().hasSkyLight(), taskExecutor2, this.chunkTaskPrioritySystem.createExecutor(taskExecutor2, false));
        this.ticketManager = new TicketManager(workerExecutor, mainThreadExecutor);
        this.persistentStateManagerFactory = supplier;
        this.pointOfInterestStorage = new PointOfInterestStorage(new File(this.saveDir, "poi"), dataFixer, bl);
        this.setViewDistance(i);
    }

    private static double getSquaredDistance(ChunkPos pos, Entity entity) {
        double d = pos.x * 16 + 8;
        double e = pos.z * 16 + 8;
        double f = d - entity.getX();
        double g = e - entity.getZ();
        return f * f + g * g;
    }

    private static int getChebyshevDistance(ChunkPos pos, ServerPlayerEntity player, boolean useWatchedPosition) {
        int j;
        int i;
        if (useWatchedPosition) {
            ChunkSectionPos chunkSectionPos = player.getWatchedSection();
            i = chunkSectionPos.getSectionX();
            j = chunkSectionPos.getSectionZ();
        } else {
            i = MathHelper.floor(player.getX() / 16.0);
            j = MathHelper.floor(player.getZ() / 16.0);
        }
        return ThreadedAnvilChunkStorage.getChebyshevDistance(pos, i, j);
    }

    private static int getChebyshevDistance(ChunkPos pos, int x, int z) {
        int i = pos.x - x;
        int j = pos.z - z;
        return Math.max(Math.abs(i), Math.abs(j));
    }

    protected ServerLightingProvider getLightProvider() {
        return this.serverLightingProvider;
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

    @Environment(value=EnvType.CLIENT)
    public String getChunkLoadingDebugInfo(ChunkPos chunkPos) {
        ChunkHolder chunkHolder = this.getChunkHolder(chunkPos.toLong());
        if (chunkHolder == null) {
            return "null";
        }
        String string = chunkHolder.getLevel() + "\n";
        ChunkStatus chunkStatus = chunkHolder.getCurrentStatus();
        Chunk chunk = chunkHolder.getCurrentChunk();
        if (chunkStatus != null) {
            string = string + "St: \u00a7" + chunkStatus.getIndex() + chunkStatus + '\u00a7' + "r\n";
        }
        if (chunk != null) {
            string = string + "Ch: \u00a7" + chunk.getStatus().getIndex() + chunk.getStatus() + '\u00a7' + "r\n";
        }
        ChunkHolder.LevelType levelType = chunkHolder.getLevelType();
        string = string + "\u00a7" + levelType.ordinal() + (Object)((Object)levelType);
        return string + '\u00a7' + "r";
    }

    private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> getRegion(ChunkPos centerChunk, final int margin, IntFunction<ChunkStatus> distanceToStatus) {
        ArrayList list2 = Lists.newArrayList();
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
                            return "Unloaded " + chunkPos.toString();
                        }
                    }));
                }
                ChunkStatus chunkStatus = distanceToStatus.apply(m);
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getChunkAt(chunkStatus, this);
                list2.add(completableFuture);
            }
        }
        CompletableFuture completableFuture2 = Util.combine(list2);
        return completableFuture2.thenApply(list -> {
            ArrayList list2 = Lists.newArrayList();
            int l = 0;
            for (final Either either : list) {
                Optional optional = either.left();
                if (!optional.isPresent()) {
                    final int m = l;
                    return Either.right((Object)new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + new ChunkPos(i + m % (margin * 2 + 1), j + m / (margin * 2 + 1)) + " " + ((ChunkHolder.Unloaded)either.right().get()).toString();
                        }
                    });
                }
                list2.add(optional.get());
                ++l;
            }
            return Either.left((Object)list2);
        });
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkEntitiesTickable(ChunkPos pos) {
        return this.getRegion(pos, 2, i -> ChunkStatus.FULL).thenApplyAsync(either -> either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2)), (Executor)this.mainThreadExecutor);
    }

    @Nullable
    private ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
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
                holder = new ChunkHolder(new ChunkPos(pos), level, this.serverLightingProvider, this.chunkTaskPrioritySystem, this);
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
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.saveDir.getName());
        } else {
            this.chunkHolders.values().stream().filter(ChunkHolder::isAccessible).forEach(chunkHolder -> {
                Chunk chunk = chunkHolder.getSavingFuture().getNow(null);
                if (chunk instanceof ReadOnlyChunk || chunk instanceof WorldChunk) {
                    this.save(chunk);
                    chunkHolder.updateAccessibleStatus();
                }
            });
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
        while ((shouldKeepTicking.getAsBoolean() || this.unloadTaskQueue.size() > 2000) && (runnable = this.unloadTaskQueue.poll()) != null) {
            runnable.run();
        }
    }

    private void tryUnloadChunk(long pos, ChunkHolder chunkHolder) {
        CompletableFuture<Chunk> completableFuture = chunkHolder.getSavingFuture();
        ((CompletableFuture)completableFuture.thenAcceptAsync(chunk -> {
            CompletableFuture<Chunk> completableFuture2 = chunkHolder.getSavingFuture();
            if (completableFuture2 != completableFuture) {
                this.tryUnloadChunk(pos, chunkHolder);
                return;
            }
            if (this.chunksToUnload.remove(pos, (Object)chunkHolder) && chunk != null) {
                if (chunk instanceof WorldChunk) {
                    ((WorldChunk)chunk).setLoadedToWorld(false);
                }
                this.save((Chunk)chunk);
                if (this.loadedChunks.remove(pos) && chunk instanceof WorldChunk) {
                    WorldChunk worldChunk = (WorldChunk)chunk;
                    this.world.unloadEntities(worldChunk);
                }
                this.serverLightingProvider.updateChunkStatus(chunk.getPos());
                this.serverLightingProvider.tick();
                this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);
            }
        }, this.unloadTaskQueue::add)).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk " + chunkHolder.getPos(), throwable);
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
        ChunkPos chunkPos = holder.getPos();
        if (requiredStatus == ChunkStatus.EMPTY) {
            return this.loadChunk(chunkPos);
        }
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = holder.getChunkAt(requiredStatus.getPrevious(), this);
        return completableFuture.thenComposeAsync(either -> {
            Chunk chunk2;
            Optional optional = either.left();
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(either);
            }
            if (requiredStatus == ChunkStatus.LIGHT) {
                this.ticketManager.addTicketWithLevel(ChunkTicketType.LIGHT, chunkPos, 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.FEATURES), chunkPos);
            }
            if ((chunk2 = (Chunk)optional.get()).getStatus().isAtLeast(requiredStatus)) {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = requiredStatus == ChunkStatus.LIGHT ? this.upgradeChunk(holder, requiredStatus) : requiredStatus.runLoadTask(this.world, this.structureManager, this.serverLightingProvider, chunk -> this.convertToFullChunk(holder), chunk2);
                this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
                return completableFuture;
            }
            return this.upgradeChunk(holder, requiredStatus);
        }, (Executor)this.mainThreadExecutor);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> loadChunk(ChunkPos pos) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.world.getProfiler().visit("chunkLoad");
                NbtCompound nbtCompound = this.getUpdatedChunkNbt(pos);
                if (nbtCompound != null) {
                    boolean bl;
                    boolean bl2 = bl = nbtCompound.contains("Level", 10) && nbtCompound.getCompound("Level").contains("Status", 8);
                    if (bl) {
                        ProtoChunk chunk = ChunkSerializer.deserialize(this.world, this.structureManager, this.pointOfInterestStorage, pos, nbtCompound);
                        chunk.setLastSaveTime(this.world.getTime());
                        this.method_27053(pos, chunk.getStatus().getChunkType());
                        return Either.left((Object)chunk);
                    }
                    LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)pos);
                }
            }
            catch (CrashException crashException) {
                Throwable throwable = crashException.getCause();
                if (throwable instanceof IOException) {
                    LOGGER.error("Couldn't load chunk {}", (Object)pos, (Object)throwable);
                }
                this.method_27054(pos);
                throw crashException;
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't load chunk {}", (Object)pos, (Object)exception);
            }
            this.method_27054(pos);
            return Either.left((Object)new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA));
        }, this.mainThreadExecutor);
    }

    private void method_27054(ChunkPos chunkPos) {
        this.chunkToType.put(chunkPos.toLong(), (byte)-1);
    }

    private byte method_27053(ChunkPos chunkPos, ChunkStatus.ChunkType chunkType) {
        return this.chunkToType.put(chunkPos.toLong(), chunkType == ChunkStatus.ChunkType.field_12808 ? (byte)-1 : 1);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> upgradeChunk(ChunkHolder holder, ChunkStatus requiredStatus) {
        ChunkPos chunkPos = holder.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getRegion(chunkPos, requiredStatus.getTaskMargin(), i -> this.getRequiredStatusForGeneration(requiredStatus, i));
        this.world.getProfiler().visit(() -> "chunkGenerate " + requiredStatus.getId());
        return completableFuture.thenComposeAsync(either -> (CompletableFuture)either.map(list -> {
            try {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = requiredStatus.runGenerationTask(this.world, this.chunkGenerator, this.structureManager, this.serverLightingProvider, chunk -> this.convertToFullChunk(holder), (List<Chunk>)list);
                this.worldGenerationProgressListener.setChunkStatus(chunkPos, requiredStatus);
                return completableFuture;
            }
            catch (Exception exception) {
                CrashReport crashReport = CrashReport.create(exception, "Exception generating new chunk");
                CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
                crashReportSection.add("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
                crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
                crashReportSection.add("Generator", this.chunkGenerator);
                throw new CrashException(crashReport);
            }
        }, unloaded -> {
            this.releaseLightTicket(chunkPos);
            return CompletableFuture.completedFuture(Either.right((Object)unloaded));
        }), runnable -> this.worldGenExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, runnable)));
    }

    protected void releaseLightTicket(ChunkPos pos) {
        this.mainThreadExecutor.send(Util.debugRunnable(() -> this.ticketManager.removeTicketWithLevel(ChunkTicketType.LIGHT, pos, 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.FEATURES), pos), () -> "release light ticket " + pos));
    }

    private ChunkStatus getRequiredStatusForGeneration(ChunkStatus centerChunkTargetStatus, int distance) {
        ChunkStatus chunkStatus = distance == 0 ? centerChunkTargetStatus.getPrevious() : ChunkStatus.byDistanceFromFull(ChunkStatus.getDistanceFromFull(centerChunkTargetStatus) + distance);
        return chunkStatus;
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> convertToFullChunk(ChunkHolder chunkHolder) {
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getFutureFor(ChunkStatus.FULL.getPrevious());
        return completableFuture.thenApplyAsync(either -> {
            ChunkStatus chunkStatus = ChunkHolder.getTargetStatusForLevel(chunkHolder.getLevel());
            if (!chunkStatus.isAtLeast(ChunkStatus.FULL)) {
                return ChunkHolder.UNLOADED_CHUNK;
            }
            return either.mapLeft(chunk -> {
                WorldChunk worldChunk;
                ChunkPos chunkPos = chunkHolder.getPos();
                if (chunk instanceof ReadOnlyChunk) {
                    worldChunk = ((ReadOnlyChunk)chunk).getWrappedChunk();
                } else {
                    worldChunk = new WorldChunk(this.world, (ProtoChunk)chunk);
                    chunkHolder.setCompletedChunk(new ReadOnlyChunk(worldChunk));
                }
                worldChunk.setLevelTypeProvider(() -> ChunkHolder.getLevelType(chunkHolder.getLevel()));
                worldChunk.loadToWorld();
                if (this.loadedChunks.add(chunkPos.toLong())) {
                    worldChunk.setLoadedToWorld(true);
                    this.world.addBlockEntities(worldChunk.getBlockEntities().values());
                    Iterable list = null;
                    for (TypeFilterableList<Entity> typeFilterableList : worldChunk.getEntitySectionArray()) {
                        for (Entity entity : typeFilterableList) {
                            if (entity instanceof PlayerEntity || this.world.loadEntity(entity)) continue;
                            if (list == null) {
                                list = Lists.newArrayList((Object[])new Entity[]{entity});
                                continue;
                            }
                            list.add(entity);
                        }
                    }
                    if (list != null) {
                        list.forEach(worldChunk::remove);
                    }
                }
                return worldChunk;
            });
        }, runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(runnable, chunkHolder.getPos().toLong(), chunkHolder::getLevel)));
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkTickable(ChunkHolder holder) {
        ChunkPos chunkPos = holder.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.getRegion(chunkPos, 1, i -> ChunkStatus.FULL);
        CompletionStage completableFuture2 = completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
            WorldChunk worldChunk = (WorldChunk)list.get(list.size() / 2);
            worldChunk.runPostProcessing();
            return Either.left((Object)worldChunk);
        }), runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, runnable)));
        ((CompletableFuture)completableFuture2).thenAcceptAsync(either -> either.mapLeft(worldChunk -> {
            this.totalChunksLoadedCount.getAndIncrement();
            Packet[] packets = new Packet[2];
            this.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> this.sendChunkDataPackets((ServerPlayerEntity)serverPlayerEntity, packets, (WorldChunk)worldChunk));
            return Either.left((Object)worldChunk);
        }), runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, runnable)));
        return completableFuture2;
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> makeChunkAccessible(ChunkHolder holder) {
        return holder.getChunkAt(ChunkStatus.FULL, this).thenApplyAsync(either -> either.mapLeft(chunk -> {
            WorldChunk worldChunk = (WorldChunk)chunk;
            worldChunk.disableTickSchedulers();
            return worldChunk;
        }), runnable -> this.mainExecutor.send(ChunkTaskPrioritySystem.createMessage(holder, runnable)));
    }

    public int getTotalChunksLoadedCount() {
        return this.totalChunksLoadedCount.get();
    }

    private boolean save(Chunk chunk) {
        this.pointOfInterestStorage.method_20436(chunk.getPos());
        if (!chunk.needsSaving()) {
            return false;
        }
        chunk.setLastSaveTime(this.world.getTime());
        chunk.setShouldSave(false);
        ChunkPos chunkPos = chunk.getPos();
        try {
            ChunkStatus chunkStatus = chunk.getStatus();
            if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.field_12807) {
                if (this.method_27055(chunkPos)) {
                    return false;
                }
                if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
                    return false;
                }
            }
            this.world.getProfiler().visit("chunkSave");
            NbtCompound nbtCompound = ChunkSerializer.serialize(this.world, chunk);
            this.setNbt(chunkPos, nbtCompound);
            this.method_27053(chunkPos, chunkStatus.getChunkType());
            return true;
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", (Object)chunkPos.x, (Object)chunkPos.z, (Object)exception);
            return false;
        }
    }

    private boolean method_27055(ChunkPos chunkPos) {
        NbtCompound nbtCompound;
        byte b = this.chunkToType.get(chunkPos.toLong());
        if (b != 0) {
            return b == 1;
        }
        try {
            nbtCompound = this.getUpdatedChunkNbt(chunkPos);
            if (nbtCompound == null) {
                this.method_27054(chunkPos);
                return false;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to read chunk {}", (Object)chunkPos, (Object)exception);
            this.method_27054(chunkPos);
            return false;
        }
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkType(nbtCompound);
        return this.method_27053(chunkPos, chunkType) == 1;
    }

    protected void setViewDistance(int watchDistance) {
        int i = MathHelper.clamp(watchDistance + 1, 3, 33);
        if (i != this.watchDistance) {
            int j = this.watchDistance;
            this.watchDistance = i;
            this.ticketManager.setWatchDistance(this.watchDistance);
            for (ChunkHolder chunkHolder : this.currentChunkHolders.values()) {
                ChunkPos chunkPos = chunkHolder.getPos();
                Packet[] packets = new Packet[2];
                this.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> {
                    int j = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, serverPlayerEntity, true);
                    boolean bl = j <= j;
                    boolean bl2 = j <= this.watchDistance;
                    this.sendWatchPackets((ServerPlayerEntity)serverPlayerEntity, chunkPos, packets, bl, bl2);
                });
            }
        }
    }

    protected void sendWatchPackets(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance) {
        ChunkHolder chunkHolder;
        if (player.world != this.world) {
            return;
        }
        if (withinViewDistance && !withinMaxWatchDistance && (chunkHolder = this.getChunkHolder(pos.toLong())) != null) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk != null) {
                this.sendChunkDataPackets(player, packets, worldChunk);
            }
            DebugInfoSender.sendChunkWatchingChange(this.world, pos);
        }
        if (!withinViewDistance && withinMaxWatchDistance) {
            player.sendUnloadChunkPacket(pos);
        }
    }

    public int getLoadedChunkCount() {
        return this.chunkHolders.size();
    }

    protected TicketManager getTicketManager() {
        return this.ticketManager;
    }

    protected Iterable<ChunkHolder> entryIterator() {
        return Iterables.unmodifiableIterable((Iterable)this.chunkHolders.values());
    }

    void dump(Writer writer) throws IOException {
        CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("entity_count").addColumn("block_entity_count").startBody(writer);
        for (Long2ObjectMap.Entry entry : this.chunkHolders.long2ObjectEntrySet()) {
            ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
            ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
            Optional<Chunk> optional = Optional.ofNullable(chunkHolder.getCurrentChunk());
            Optional<Object> optional2 = optional.flatMap(chunk -> chunk instanceof WorldChunk ? Optional.of((WorldChunk)chunk) : Optional.empty());
            csvWriter.printRow(chunkPos.x, chunkPos.z, chunkHolder.getLevel(), optional.isPresent(), optional.map(Chunk::getStatus).orElse(null), optional2.map(WorldChunk::getLevelType).orElse(null), ThreadedAnvilChunkStorage.getFutureStatus(chunkHolder.getAccessibleFuture()), ThreadedAnvilChunkStorage.getFutureStatus(chunkHolder.getTickingFuture()), ThreadedAnvilChunkStorage.getFutureStatus(chunkHolder.getEntityTickingFuture()), this.ticketManager.getTicket(entry.getLongKey()), !this.isTooFarFromPlayersToSpawnMobs(chunkPos), optional2.map(worldChunk -> Stream.of(worldChunk.getEntitySectionArray()).mapToInt(TypeFilterableList::size).sum()).orElse(0), optional2.map(worldChunk -> worldChunk.getBlockEntities().size()).orElse(0));
        }
    }

    private static String getFutureStatus(CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture) {
        try {
            Either either = completableFuture.getNow(null);
            if (either != null) {
                return (String)either.map(worldChunk -> "done", unloaded -> "unloaded");
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

    @Nullable
    private NbtCompound getUpdatedChunkNbt(ChunkPos pos) throws IOException {
        NbtCompound nbtCompound = this.getNbt(pos);
        if (nbtCompound == null) {
            return null;
        }
        return this.updateChunkNbt(this.world.getRegistryKey(), this.persistentStateManagerFactory, nbtCompound);
    }

    boolean isTooFarFromPlayersToSpawnMobs(ChunkPos chunkPos) {
        long l = chunkPos.toLong();
        if (!this.ticketManager.method_20800(l)) {
            return true;
        }
        return this.playerChunkWatchingManager.getPlayersWatchingChunk(l).noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && ThreadedAnvilChunkStorage.getSquaredDistance(chunkPos, serverPlayerEntity) < 16384.0);
    }

    private boolean doesNotGenerateChunks(ServerPlayerEntity player) {
        return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
    }

    void handlePlayerAddedOrRemoved(ServerPlayerEntity player, boolean added) {
        boolean bl = this.doesNotGenerateChunks(player);
        boolean bl2 = this.playerChunkWatchingManager.method_21715(player);
        int i = MathHelper.floor(player.getX()) >> 4;
        int j = MathHelper.floor(player.getZ()) >> 4;
        if (added) {
            this.playerChunkWatchingManager.add(ChunkPos.toLong(i, j), player, bl);
            this.method_20726(player);
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
        for (int k = i - this.watchDistance; k <= i + this.watchDistance; ++k) {
            for (int l = j - this.watchDistance; l <= j + this.watchDistance; ++l) {
                ChunkPos chunkPos = new ChunkPos(k, l);
                this.sendWatchPackets(player, chunkPos, new Packet[2], !added, added);
            }
        }
    }

    private ChunkSectionPos method_20726(ServerPlayerEntity serverPlayerEntity) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(serverPlayerEntity);
        serverPlayerEntity.setWatchedSection(chunkSectionPos);
        serverPlayerEntity.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ()));
        return chunkSectionPos;
    }

    public void updatePosition(ServerPlayerEntity player) {
        boolean bl3;
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            if (entityTracker.entity == player) {
                entityTracker.updateCameraPosition(this.world.getPlayers());
                continue;
            }
            entityTracker.updateCameraPosition(player);
        }
        int i = MathHelper.floor(player.getX()) >> 4;
        int j = MathHelper.floor(player.getZ()) >> 4;
        ChunkSectionPos chunkSectionPos = player.getWatchedSection();
        ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(player);
        long l = chunkSectionPos.toChunkPos().toLong();
        long m = chunkSectionPos2.toChunkPos().toLong();
        boolean bl = this.playerChunkWatchingManager.isWatchDisabled(player);
        boolean bl2 = this.doesNotGenerateChunks(player);
        boolean bl4 = bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();
        if (bl3 || bl != bl2) {
            this.method_20726(player);
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
            int o = Math.min(i, k) - this.watchDistance;
            int p = Math.min(j, n) - this.watchDistance;
            int q = Math.max(i, k) + this.watchDistance;
            int r = Math.max(j, n) + this.watchDistance;
            for (int s = o; s <= q; ++s) {
                for (int t = p; t <= r; ++t) {
                    ChunkPos chunkPos = new ChunkPos(s, t);
                    boolean bl42 = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, k, n) <= this.watchDistance;
                    boolean bl5 = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, i, j) <= this.watchDistance;
                    this.sendWatchPackets(player, chunkPos, new Packet[2], bl42, bl5);
                }
            }
        } else {
            boolean bl7;
            boolean bl6;
            ChunkPos chunkPos2;
            int p;
            int o;
            for (o = k - this.watchDistance; o <= k + this.watchDistance; ++o) {
                for (p = n - this.watchDistance; p <= n + this.watchDistance; ++p) {
                    chunkPos2 = new ChunkPos(o, p);
                    bl6 = true;
                    bl7 = false;
                    this.sendWatchPackets(player, chunkPos2, new Packet[2], true, false);
                }
            }
            for (o = i - this.watchDistance; o <= i + this.watchDistance; ++o) {
                for (p = j - this.watchDistance; p <= j + this.watchDistance; ++p) {
                    chunkPos2 = new ChunkPos(o, p);
                    bl6 = false;
                    bl7 = true;
                    this.sendWatchPackets(player, chunkPos2, new Packet[2], false, true);
                }
            }
        }
    }

    @Override
    public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
        return this.playerChunkWatchingManager.getPlayersWatchingChunk(chunkPos.toLong()).filter(serverPlayerEntity -> {
            int i = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, serverPlayerEntity, true);
            if (i > this.watchDistance) {
                return false;
            }
            return !onlyOnWatchDistanceEdge || i == this.watchDistance;
        });
    }

    protected void loadEntity(Entity entity) {
        if (entity instanceof EnderDragonPart) {
            return;
        }
        EntityType<?> entityType = entity.getType();
        int i = entityType.getMaxTrackDistance() * 16;
        int j = entityType.getTrackTickInterval();
        if (this.entityTrackers.containsKey(entity.getEntityId())) {
            throw Util.throwOrPause(new IllegalStateException("Entity is already tracked!"));
        }
        EntityTracker entityTracker = new EntityTracker(entity, i, j, entityType.alwaysUpdateVelocity());
        this.entityTrackers.put(entity.getEntityId(), (Object)entityTracker);
        entityTracker.updateCameraPosition(this.world.getPlayers());
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.handlePlayerAddedOrRemoved(serverPlayerEntity, true);
            for (EntityTracker entityTracker2 : this.entityTrackers.values()) {
                if (entityTracker2.entity == serverPlayerEntity) continue;
                entityTracker2.updateCameraPosition(serverPlayerEntity);
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
        if ((entityTracker2 = (EntityTracker)this.entityTrackers.remove(entity.getEntityId())) != null) {
            entityTracker2.stopTracking();
        }
    }

    protected void tickEntityMovement() {
        ArrayList list = Lists.newArrayList();
        List<ServerPlayerEntity> list2 = this.world.getPlayers();
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            ChunkSectionPos chunkSectionPos2;
            ChunkSectionPos chunkSectionPos = entityTracker.lastCameraPosition;
            if (!Objects.equals(chunkSectionPos, chunkSectionPos2 = ChunkSectionPos.from(entityTracker.entity))) {
                entityTracker.updateCameraPosition(list2);
                Entity entity = entityTracker.entity;
                if (entity instanceof ServerPlayerEntity) {
                    list.add((ServerPlayerEntity)entity);
                }
                entityTracker.lastCameraPosition = chunkSectionPos2;
            }
            entityTracker.entry.tick();
        }
        if (!list.isEmpty()) {
            for (EntityTracker entityTracker : this.entityTrackers.values()) {
                entityTracker.updateCameraPosition(list);
            }
        }
    }

    protected void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getEntityId());
        if (entityTracker != null) {
            entityTracker.sendToOtherNearbyPlayers(packet);
        }
    }

    protected void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getEntityId());
        if (entityTracker != null) {
            entityTracker.sendToNearbyPlayers(packet);
        }
    }

    private void sendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk) {
        if (packets[0] == null) {
            packets[0] = new ChunkDataS2CPacket(chunk, 65535);
            packets[1] = new LightUpdateS2CPacket(chunk.getPos(), this.serverLightingProvider, true);
        }
        player.sendInitialChunkPackets(chunk.getPos(), packets[0], packets[1]);
        DebugInfoSender.sendChunkWatchingChange(this.world, chunk.getPos());
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            Entity entity = entityTracker.entity;
            if (entity == player || entity.chunkX != chunk.getPos().x || entity.chunkZ != chunk.getPos().z) continue;
            entityTracker.updateCameraPosition(player);
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

    public CompletableFuture<Void> enableTickSchedulers(WorldChunk worldChunk) {
        return this.mainThreadExecutor.submit(() -> worldChunk.enableTickSchedulers(this.world));
    }

    class EntityTracker {
        private final EntityTrackerEntry entry;
        private final Entity entity;
        private final int maxDistance;
        private ChunkSectionPos lastCameraPosition;
        private final Set<ServerPlayerEntity> playersTracking = Sets.newHashSet();

        public EntityTracker(Entity maxDistance, int tickInterval, int i, boolean bl) {
            this.entry = new EntityTrackerEntry(ThreadedAnvilChunkStorage.this.world, maxDistance, i, bl, this::sendToOtherNearbyPlayers);
            this.entity = maxDistance;
            this.maxDistance = tickInterval;
            this.lastCameraPosition = ChunkSectionPos.from(maxDistance);
        }

        public boolean equals(Object o) {
            if (o instanceof EntityTracker) {
                return ((EntityTracker)o).entity.getEntityId() == this.entity.getEntityId();
            }
            return false;
        }

        public int hashCode() {
            return this.entity.getEntityId();
        }

        public void sendToOtherNearbyPlayers(Packet<?> packet) {
            for (ServerPlayerEntity serverPlayerEntity : this.playersTracking) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }

        public void sendToNearbyPlayers(Packet<?> packet) {
            this.sendToOtherNearbyPlayers(packet);
            if (this.entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
            }
        }

        public void stopTracking() {
            for (ServerPlayerEntity serverPlayerEntity : this.playersTracking) {
                this.entry.stopTracking(serverPlayerEntity);
            }
        }

        public void stopTracking(ServerPlayerEntity serverPlayerEntity) {
            if (this.playersTracking.remove(serverPlayerEntity)) {
                this.entry.stopTracking(serverPlayerEntity);
            }
        }

        public void updateCameraPosition(ServerPlayerEntity player) {
            boolean bl;
            if (player == this.entity) {
                return;
            }
            Vec3d vec3d = player.getPos().subtract(this.entry.getLastPos());
            int i = Math.min(this.getMaxTrackDistance(), (ThreadedAnvilChunkStorage.this.watchDistance - 1) * 16);
            boolean bl2 = bl = vec3d.x >= (double)(-i) && vec3d.x <= (double)i && vec3d.z >= (double)(-i) && vec3d.z <= (double)i && this.entity.canBeSpectated(player);
            if (bl) {
                ChunkPos chunkPos;
                ChunkHolder chunkHolder;
                boolean bl22 = this.entity.teleporting;
                if (!bl22 && (chunkHolder = ThreadedAnvilChunkStorage.this.getChunkHolder((chunkPos = new ChunkPos(this.entity.chunkX, this.entity.chunkZ)).toLong())) != null && chunkHolder.getWorldChunk() != null) {
                    boolean bl3 = bl22 = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, player, false) <= ThreadedAnvilChunkStorage.this.watchDistance;
                }
                if (bl22 && this.playersTracking.add(player)) {
                    this.entry.startTracking(player);
                }
            } else if (this.playersTracking.remove(player)) {
                this.entry.stopTracking(player);
            }
        }

        private int adjustTrackingDistance(int initialDistance) {
            return ThreadedAnvilChunkStorage.this.world.getServer().adjustTrackingDistance(initialDistance);
        }

        private int getMaxTrackDistance() {
            Collection<Entity> collection = this.entity.getPassengersDeep();
            int i = this.maxDistance;
            for (Entity entity : collection) {
                int j = entity.getType().getMaxTrackDistance() * 16;
                if (j <= i) continue;
                i = j;
            }
            return this.adjustTrackingDistance(i);
        }

        public void updateCameraPosition(List<ServerPlayerEntity> players) {
            for (ServerPlayerEntity serverPlayerEntity : players) {
                this.updateCameraPosition(serverPlayerEntity);
            }
        }
    }

    class TicketManager
    extends ChunkTicketManager {
        protected TicketManager(Executor mainThreadExecutor, Executor executor) {
            super(mainThreadExecutor, executor);
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
}

