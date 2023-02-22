/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMaps
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import net.minecraft.world.SimulationDistanceLevelPropagator;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class ChunkTicketManager {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29764 = 2;
    static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.FULL) - 2;
    private static final int field_29765 = 4;
    private static final int field_34884 = 32;
    private static final int field_34885 = 33;
    final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap();
    final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap();
    private final TicketDistanceLevelPropagator distanceFromTicketTracker = new TicketDistanceLevelPropagator();
    private final DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new DistanceFromNearestPlayerTracker(8);
    private final SimulationDistanceLevelPropagator simulationDistanceTracker = new SimulationDistanceLevelPropagator();
    private final NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new NearbyChunkTicketUpdater(33);
    final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
    final ChunkTaskPrioritySystem levelUpdateListener;
    final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> playerTicketThrottler;
    final MessageListener<ChunkTaskPrioritySystem.UnblockingMessage> playerTicketThrottlerUnblocker;
    final LongSet chunkPositions = new LongOpenHashSet();
    final Executor mainThreadExecutor;
    private long age;
    private int simulationDistance = 10;

    protected ChunkTicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
        ChunkTaskPrioritySystem chunkTaskPrioritySystem;
        MessageListener<Runnable> messageListener = MessageListener.create("player ticket throttler", mainThreadExecutor::execute);
        this.levelUpdateListener = chunkTaskPrioritySystem = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(messageListener), workerExecutor, 4);
        this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutor(messageListener, true);
        this.playerTicketThrottlerUnblocker = chunkTaskPrioritySystem.createUnblockingExecutor(messageListener);
        this.mainThreadExecutor = mainThreadExecutor;
    }

    protected void purge() {
        ++this.age;
        ObjectIterator objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            Iterator iterator = ((SortedArraySet)entry.getValue()).iterator();
            boolean bl = false;
            while (iterator.hasNext()) {
                ChunkTicket chunkTicket = (ChunkTicket)iterator.next();
                if (!chunkTicket.isExpired(this.age)) continue;
                iterator.remove();
                bl = true;
                this.simulationDistanceTracker.remove(entry.getLongKey(), chunkTicket);
            }
            if (bl) {
                this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), ChunkTicketManager.getLevel((SortedArraySet)entry.getValue()), false);
            }
            if (!((SortedArraySet)entry.getValue()).isEmpty()) continue;
            objectIterator.remove();
        }
    }

    private static int getLevel(SortedArraySet<ChunkTicket<?>> tickets) {
        return !tickets.isEmpty() ? tickets.first().getLevel() : ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
    }

    protected abstract boolean isUnloaded(long var1);

    @Nullable
    protected abstract ChunkHolder getChunkHolder(long var1);

    @Nullable
    protected abstract ChunkHolder setLevel(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    public boolean tick(ThreadedAnvilChunkStorage chunkStorage) {
        boolean bl;
        this.distanceFromNearestPlayerTracker.updateLevels();
        this.simulationDistanceTracker.updateLevels();
        this.nearbyChunkTicketUpdater.updateLevels();
        int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.update(Integer.MAX_VALUE);
        boolean bl2 = bl = i != 0;
        if (bl) {
            // empty if block
        }
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(holder -> holder.tick(chunkStorage, this.mainThreadExecutor));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            LongIterator longIterator = this.chunkPositions.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                if (!this.getTicketSet(l).stream().anyMatch(ticket -> ticket.getType() == ChunkTicketType.PLAYER)) continue;
                ChunkHolder chunkHolder = chunkStorage.getCurrentChunkHolder(l);
                if (chunkHolder == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getEntityTickingFuture();
                completableFuture.thenAccept(either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {}, l, false))));
            }
            this.chunkPositions.clear();
        }
        return bl;
    }

    void addTicket(long position, ChunkTicket<?> ticket) {
        SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(position);
        int i = ChunkTicketManager.getLevel(sortedArraySet);
        ChunkTicket<?> chunkTicket = sortedArraySet.addAndGet(ticket);
        chunkTicket.setTickCreated(this.age);
        if (ticket.getLevel() < i) {
            this.distanceFromTicketTracker.updateLevel(position, ticket.getLevel(), true);
        }
    }

    void removeTicket(long pos, ChunkTicket<?> ticket) {
        SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(pos);
        if (sortedArraySet.remove(ticket)) {
            // empty if block
        }
        if (sortedArraySet.isEmpty()) {
            this.ticketsByPosition.remove(pos);
        }
        this.distanceFromTicketTracker.updateLevel(pos, ChunkTicketManager.getLevel(sortedArraySet), false);
    }

    public <T> void addTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<T>(type, level, argument));
    }

    public <T> void removeTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(type, level, argument);
        this.removeTicket(pos.toLong(), chunkTicket);
    }

    public <T> void addTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(type, 33 - radius, argument);
        long l = pos.toLong();
        this.addTicket(l, chunkTicket);
        this.simulationDistanceTracker.add(l, chunkTicket);
    }

    public <T> void removeTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(type, 33 - radius, argument);
        long l = pos.toLong();
        this.removeTicket(l, chunkTicket);
        this.simulationDistanceTracker.remove(l, chunkTicket);
    }

    private SortedArraySet<ChunkTicket<?>> getTicketSet(long position) {
        return (SortedArraySet)this.ticketsByPosition.computeIfAbsent(position, pos -> SortedArraySet.create(4));
    }

    protected void setChunkForced(ChunkPos pos, boolean forced) {
        ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<ChunkPos>(ChunkTicketType.FORCED, 31, pos);
        long l = pos.toLong();
        if (forced) {
            this.addTicket(l, chunkTicket);
            this.simulationDistanceTracker.add(l, chunkTicket);
        } else {
            this.removeTicket(l, chunkTicket);
            this.simulationDistanceTracker.remove(l, chunkTicket);
        }
    }

    public void handleChunkEnter(ChunkSectionPos pos, ServerPlayerEntity player) {
        ChunkPos chunkPos = pos.toChunkPos();
        long l = chunkPos.toLong();
        ((ObjectSet)this.playersByChunkPos.computeIfAbsent(l, sectionPos -> new ObjectOpenHashSet())).add((Object)player);
        this.distanceFromNearestPlayerTracker.updateLevel(l, 0, true);
        this.nearbyChunkTicketUpdater.updateLevel(l, 0, true);
        this.simulationDistanceTracker.add(ChunkTicketType.PLAYER, chunkPos, this.getPlayerSimulationLevel(), chunkPos);
    }

    public void handleChunkLeave(ChunkSectionPos pos, ServerPlayerEntity player) {
        ChunkPos chunkPos = pos.toChunkPos();
        long l = chunkPos.toLong();
        ObjectSet objectSet = (ObjectSet)this.playersByChunkPos.get(l);
        objectSet.remove((Object)player);
        if (objectSet.isEmpty()) {
            this.playersByChunkPos.remove(l);
            this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
            this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
            this.simulationDistanceTracker.remove(ChunkTicketType.PLAYER, chunkPos, this.getPlayerSimulationLevel(), chunkPos);
        }
    }

    private int getPlayerSimulationLevel() {
        return Math.max(0, 31 - this.simulationDistance);
    }

    public boolean shouldTickEntities(long chunkPos) {
        return this.simulationDistanceTracker.getLevel(chunkPos) < 32;
    }

    public boolean shouldTickBlocks(long chunkPos) {
        return this.simulationDistanceTracker.getLevel(chunkPos) < 33;
    }

    protected String getTicket(long pos) {
        SortedArraySet sortedArraySet = (SortedArraySet)this.ticketsByPosition.get(pos);
        if (sortedArraySet == null || sortedArraySet.isEmpty()) {
            return "no_ticket";
        }
        return ((ChunkTicket)sortedArraySet.first()).toString();
    }

    protected void setWatchDistance(int viewDistance) {
        this.nearbyChunkTicketUpdater.setWatchDistance(viewDistance);
    }

    public void setSimulationDistance(int simulationDistance) {
        if (simulationDistance != this.simulationDistance) {
            this.simulationDistance = simulationDistance;
            this.simulationDistanceTracker.updatePlayerTickets(this.getPlayerSimulationLevel());
        }
    }

    public int getTickedChunkCount() {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
    }

    public boolean shouldTick(long chunkPos) {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(chunkPos);
    }

    public String toDumpString() {
        return this.levelUpdateListener.getDebugString();
    }

    private void dump(String path) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(path));){
            for (Long2ObjectMap.Entry entry : this.ticketsByPosition.long2ObjectEntrySet()) {
                ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
                for (ChunkTicket chunkTicket : (SortedArraySet)entry.getValue()) {
                    fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + chunkTicket.getType() + "\t" + chunkTicket.getLevel() + "\t\n").getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to dump tickets to {}", (Object)path, (Object)iOException);
        }
    }

    @VisibleForTesting
    SimulationDistanceLevelPropagator getSimulationDistanceTracker() {
        return this.simulationDistanceTracker;
    }

    public void removePersistentTickets() {
        ImmutableSet immutableSet = ImmutableSet.of(ChunkTicketType.UNKNOWN, ChunkTicketType.POST_TELEPORT, ChunkTicketType.LIGHT);
        ObjectIterator objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            Iterator iterator = ((SortedArraySet)entry.getValue()).iterator();
            boolean bl = false;
            while (iterator.hasNext()) {
                ChunkTicket chunkTicket = (ChunkTicket)iterator.next();
                if (immutableSet.contains(chunkTicket.getType())) continue;
                iterator.remove();
                bl = true;
                this.simulationDistanceTracker.remove(entry.getLongKey(), chunkTicket);
            }
            if (bl) {
                this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), ChunkTicketManager.getLevel((SortedArraySet)entry.getValue()), false);
            }
            if (!((SortedArraySet)entry.getValue()).isEmpty()) continue;
            objectIterator.remove();
        }
    }

    public boolean shouldDelayShutdown() {
        return !this.ticketsByPosition.isEmpty();
    }

    class TicketDistanceLevelPropagator
    extends ChunkPosDistanceLevelPropagator {
        public TicketDistanceLevelPropagator() {
            super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
        }

        @Override
        protected int getInitialLevel(long id) {
            SortedArraySet sortedArraySet = (SortedArraySet)ChunkTicketManager.this.ticketsByPosition.get(id);
            if (sortedArraySet == null) {
                return Integer.MAX_VALUE;
            }
            if (sortedArraySet.isEmpty()) {
                return Integer.MAX_VALUE;
            }
            return ((ChunkTicket)sortedArraySet.first()).getLevel();
        }

        @Override
        protected int getLevel(long id) {
            ChunkHolder chunkHolder;
            if (!ChunkTicketManager.this.isUnloaded(id) && (chunkHolder = ChunkTicketManager.this.getChunkHolder(id)) != null) {
                return chunkHolder.getLevel();
            }
            return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        }

        @Override
        protected void setLevel(long id, int level) {
            int i;
            ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(id);
            int n = i = chunkHolder == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : chunkHolder.getLevel();
            if (i == level) {
                return;
            }
            if ((chunkHolder = ChunkTicketManager.this.setLevel(id, level, chunkHolder, i)) != null) {
                ChunkTicketManager.this.chunkHolders.add(chunkHolder);
            }
        }

        public int update(int distance) {
            return this.applyPendingUpdates(distance);
        }
    }

    class DistanceFromNearestPlayerTracker
    extends ChunkPosDistanceLevelPropagator {
        protected final Long2ByteMap distanceFromNearestPlayer;
        protected final int maxDistance;

        protected DistanceFromNearestPlayerTracker(int maxDistance) {
            super(maxDistance + 2, 16, 256);
            this.distanceFromNearestPlayer = new Long2ByteOpenHashMap();
            this.maxDistance = maxDistance;
            this.distanceFromNearestPlayer.defaultReturnValue((byte)(maxDistance + 2));
        }

        @Override
        protected int getLevel(long id) {
            return this.distanceFromNearestPlayer.get(id);
        }

        @Override
        protected void setLevel(long id, int level) {
            byte b = level > this.maxDistance ? this.distanceFromNearestPlayer.remove(id) : this.distanceFromNearestPlayer.put(id, (byte)level);
            this.onDistanceChange(id, b, level);
        }

        protected void onDistanceChange(long pos, int oldDistance, int distance) {
        }

        @Override
        protected int getInitialLevel(long id) {
            return this.isPlayerInChunk(id) ? 0 : Integer.MAX_VALUE;
        }

        private boolean isPlayerInChunk(long chunkPos) {
            ObjectSet objectSet = (ObjectSet)ChunkTicketManager.this.playersByChunkPos.get(chunkPos);
            return objectSet != null && !objectSet.isEmpty();
        }

        public void updateLevels() {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }

        private void dump(String path) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(path));){
                for (Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
                    ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
                    String string = Byte.toString(entry.getByteValue());
                    fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + string + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to dump chunks to {}", (Object)path, (Object)iOException);
            }
        }
    }

    class NearbyChunkTicketUpdater
    extends DistanceFromNearestPlayerTracker {
        private int watchDistance;
        private final Long2IntMap distances;
        private final LongSet positionsAffected;

        protected NearbyChunkTicketUpdater(int i) {
            super(i);
            this.distances = Long2IntMaps.synchronize((Long2IntMap)new Long2IntOpenHashMap());
            this.positionsAffected = new LongOpenHashSet();
            this.watchDistance = 0;
            this.distances.defaultReturnValue(i + 2);
        }

        @Override
        protected void onDistanceChange(long pos, int oldDistance, int distance) {
            this.positionsAffected.add(pos);
        }

        public void setWatchDistance(int watchDistance) {
            for (Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
                byte b = entry.getByteValue();
                long l = entry.getLongKey();
                this.updateTicket(l, b, this.isWithinViewDistance(b), b <= watchDistance - 2);
            }
            this.watchDistance = watchDistance;
        }

        private void updateTicket(long pos, int distance, boolean oldWithinViewDistance, boolean withinViewDistance) {
            if (oldWithinViewDistance != withinViewDistance) {
                ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<ChunkPos>(ChunkTicketType.PLAYER, NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(pos));
                if (withinViewDistance) {
                    ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
                        if (this.isWithinViewDistance(this.getLevel(pos))) {
                            ChunkTicketManager.this.addTicket(pos, chunkTicket);
                            ChunkTicketManager.this.chunkPositions.add(pos);
                        } else {
                            ChunkTicketManager.this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {}, pos, false));
                        }
                    }), pos, () -> distance));
                } else {
                    ChunkTicketManager.this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(pos, chunkTicket)), pos, true));
                }
            }
        }

        @Override
        public void updateLevels() {
            super.updateLevels();
            if (!this.positionsAffected.isEmpty()) {
                LongIterator longIterator = this.positionsAffected.iterator();
                while (longIterator.hasNext()) {
                    int j;
                    long l = longIterator.nextLong();
                    int i = this.distances.get(l);
                    if (i == (j = this.getLevel(l))) continue;
                    ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, level -> {
                        if (level >= this.distances.defaultReturnValue()) {
                            this.distances.remove(l);
                        } else {
                            this.distances.put(l, level);
                        }
                    });
                    this.updateTicket(l, j, this.isWithinViewDistance(i), this.isWithinViewDistance(j));
                }
                this.positionsAffected.clear();
            }
        }

        private boolean isWithinViewDistance(int distance) {
            return distance <= this.watchDistance - 2;
        }
    }
}

