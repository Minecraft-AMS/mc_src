/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Either
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
 *  it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet
 *  it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  it.unimi.dsi.fastutil.objects.ObjectSortedSet
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
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
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
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
import net.minecraft.util.ChunkPosDistanceLevelPropagator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkTicketManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL) - 2;
    private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap();
    private final class_4077 distanceFromTicketTracker = new class_4077();
    private final DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new DistanceFromNearestPlayerTracker(8);
    private final NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new NearbyChunkTicketUpdater(33);
    private final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
    private final ChunkTaskPrioritySystem levelUpdateListener;
    private final MessageListener<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> playerTicketThrottler;
    private final MessageListener<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
    private final LongSet chunkPositions = new LongOpenHashSet();
    private final Executor mainThreadExecutor;
    private long age;

    protected ChunkTicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
        ChunkTaskPrioritySystem chunkTaskPrioritySystem;
        MessageListener<Runnable> messageListener = MessageListener.create("player ticket throttler", mainThreadExecutor::execute);
        this.levelUpdateListener = chunkTaskPrioritySystem = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(messageListener), workerExecutor, 4);
        this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutor(messageListener, true);
        this.playerTicketThrottlerSorter = chunkTaskPrioritySystem.createSorterExecutor(messageListener);
        this.mainThreadExecutor = mainThreadExecutor;
    }

    protected void purge() {
        ++this.age;
        ObjectIterator objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            if (((ObjectSortedSet)entry.getValue()).removeIf(chunkTicket -> chunkTicket.method_20627(this.age))) {
                this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), this.getLevel((ObjectSortedSet)entry.getValue()), false);
            }
            if (!((ObjectSortedSet)entry.getValue()).isEmpty()) continue;
            objectIterator.remove();
        }
    }

    private int getLevel(ObjectSortedSet<ChunkTicket<?>> ticketSet) {
        ObjectBidirectionalIterator objectBidirectionalIterator = ticketSet.iterator();
        if (objectBidirectionalIterator.hasNext()) {
            return ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
        }
        return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
    }

    protected abstract boolean isUnloaded(long var1);

    @Nullable
    protected abstract ChunkHolder getChunkHolder(long var1);

    @Nullable
    protected abstract ChunkHolder setLevel(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    public boolean tick(ThreadedAnvilChunkStorage chunkStorage) {
        boolean bl;
        this.distanceFromNearestPlayerTracker.updateLevels();
        this.nearbyChunkTicketUpdater.updateLevels();
        int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.method_18746(Integer.MAX_VALUE);
        boolean bl2 = bl = i != 0;
        if (bl) {
            // empty if block
        }
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(chunkHolder -> chunkHolder.tick(chunkStorage));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            LongIterator longIterator = this.chunkPositions.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                if (!this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.getType() == ChunkTicketType.PLAYER)) continue;
                ChunkHolder chunkHolder2 = chunkStorage.getCurrentChunkHolder(l);
                if (chunkHolder2 == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder2.getEntityTickingFuture();
                completableFuture.thenAccept(either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, l, false))));
            }
            this.chunkPositions.clear();
        }
        return bl;
    }

    private void addTicket(long position, ChunkTicket<?> ticket) {
        ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(position);
        ObjectBidirectionalIterator objectBidirectionalIterator = objectSortedSet.iterator();
        int i = objectBidirectionalIterator.hasNext() ? ((ChunkTicket)objectBidirectionalIterator.next()).getLevel() : ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        if (objectSortedSet.add(ticket)) {
            // empty if block
        }
        if (ticket.getLevel() < i) {
            this.distanceFromTicketTracker.updateLevel(position, ticket.getLevel(), true);
        }
    }

    private void removeTicket(long pos, ChunkTicket<?> ticket) {
        ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(pos);
        if (objectSortedSet.remove(ticket)) {
            // empty if block
        }
        if (objectSortedSet.isEmpty()) {
            this.ticketsByPosition.remove(pos);
        }
        this.distanceFromTicketTracker.updateLevel(pos, this.getLevel(objectSortedSet), false);
    }

    public <T> void addTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<T>(type, level, argument, this.age));
    }

    public <T> void removeTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(type, level, argument, this.age);
        this.removeTicket(pos.toLong(), chunkTicket);
    }

    public <T> void addTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<T>(type, 33 - radius, argument, this.age));
    }

    public <T> void removeTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(type, 33 - radius, argument, this.age);
        this.removeTicket(pos.toLong(), chunkTicket);
    }

    private ObjectSortedSet<ChunkTicket<?>> getTicketSet(long position) {
        return (ObjectSortedSet)this.ticketsByPosition.computeIfAbsent(position, l -> new ObjectAVLTreeSet());
    }

    protected void setChunkForced(ChunkPos pos, boolean forced) {
        ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<ChunkPos>(ChunkTicketType.FORCED, 31, pos, this.age);
        if (forced) {
            this.addTicket(pos.toLong(), chunkTicket);
        } else {
            this.removeTicket(pos.toLong(), chunkTicket);
        }
    }

    public void handleChunkEnter(ChunkSectionPos pos, ServerPlayerEntity serverPlayerEntity) {
        long l2 = pos.toChunkPos().toLong();
        ((ObjectSet)this.playersByChunkPos.computeIfAbsent(l2, l -> new ObjectOpenHashSet())).add((Object)serverPlayerEntity);
        this.distanceFromNearestPlayerTracker.updateLevel(l2, 0, true);
        this.nearbyChunkTicketUpdater.updateLevel(l2, 0, true);
    }

    public void handleChunkLeave(ChunkSectionPos pos, ServerPlayerEntity player) {
        long l = pos.toChunkPos().toLong();
        ObjectSet objectSet = (ObjectSet)this.playersByChunkPos.get(l);
        objectSet.remove((Object)player);
        if (objectSet.isEmpty()) {
            this.playersByChunkPos.remove(l);
            this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
            this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
        }
    }

    protected String method_21623(long l) {
        ObjectSortedSet objectSortedSet = (ObjectSortedSet)this.ticketsByPosition.get(l);
        String string = objectSortedSet == null || objectSortedSet.isEmpty() ? "no_ticket" : ((ChunkTicket)objectSortedSet.first()).toString();
        return string;
    }

    protected void setWatchDistance(int viewDistance) {
        this.nearbyChunkTicketUpdater.setWatchDistance(viewDistance);
    }

    public int getLevelCount() {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
    }

    public boolean method_20800(long l) {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(l);
    }

    public String method_21683() {
        return this.levelUpdateListener.method_21680();
    }

    class class_4077
    extends ChunkPosDistanceLevelPropagator {
        public class_4077() {
            super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
        }

        @Override
        protected int getInitialLevel(long id) {
            ObjectSortedSet objectSortedSet = (ObjectSortedSet)ChunkTicketManager.this.ticketsByPosition.get(id);
            if (objectSortedSet == null) {
                return Integer.MAX_VALUE;
            }
            ObjectBidirectionalIterator objectBidirectionalIterator = objectSortedSet.iterator();
            if (!objectBidirectionalIterator.hasNext()) {
                return Integer.MAX_VALUE;
            }
            return ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
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

        public int method_18746(int i) {
            return this.applyPendingUpdates(i);
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
                ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<ChunkPos>(ChunkTicketType.PLAYER, NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(pos), ChunkTicketManager.this.age);
                if (withinViewDistance) {
                    ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
                        if (this.isWithinViewDistance(this.getLevel(pos))) {
                            ChunkTicketManager.this.addTicket(pos, chunkTicket);
                            ChunkTicketManager.this.chunkPositions.add(pos);
                        } else {
                            ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, pos, false));
                        }
                    }), pos, () -> distance));
                } else {
                    ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(pos, chunkTicket)), pos, true));
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
                    int i2 = this.distances.get(l);
                    if (i2 == (j = this.getLevel(l))) continue;
                    ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, i -> {
                        if (i >= this.distances.defaultReturnValue()) {
                            this.distances.remove(l);
                        } else {
                            this.distances.put(l, i);
                        }
                    });
                    this.updateTicket(l, j, this.isWithinViewDistance(i2), this.isWithinViewDistance(j));
                }
                this.positionsAffected.clear();
            }
        }

        private boolean isWithinViewDistance(int distance) {
            return distance <= this.watchDistance - 2;
        }
    }

    class DistanceFromNearestPlayerTracker
    extends ChunkPosDistanceLevelPropagator {
        protected final Long2ByteMap distanceFromNearestPlayer;
        protected final int maxDistance;

        protected DistanceFromNearestPlayerTracker(int i) {
            super(i + 2, 16, 256);
            this.distanceFromNearestPlayer = new Long2ByteOpenHashMap();
            this.maxDistance = i;
            this.distanceFromNearestPlayer.defaultReturnValue((byte)(i + 2));
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
    }
}
