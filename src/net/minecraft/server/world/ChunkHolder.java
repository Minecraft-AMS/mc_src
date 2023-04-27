/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.AtomicStack;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public class ChunkHolder {
    public static final Either<Chunk, Unloaded> UNLOADED_CHUNK = Either.right((Object)Unloaded.INSTANCE);
    public static final CompletableFuture<Either<Chunk, Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
    public static final Either<WorldChunk, Unloaded> UNLOADED_WORLD_CHUNK = Either.right((Object)Unloaded.INSTANCE);
    private static final Either<Chunk, Unloaded> field_36388 = Either.right((Object)Unloaded.INSTANCE);
    private static final CompletableFuture<Either<WorldChunk, Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_WORLD_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
    private static final LevelType[] LEVEL_TYPES = LevelType.values();
    private static final int field_29668 = 64;
    private final AtomicReferenceArray<CompletableFuture<Either<Chunk, Unloaded>>> futuresByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
    private final HeightLimitView world;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private CompletableFuture<Chunk> savingFuture = CompletableFuture.completedFuture(null);
    @Nullable
    private final AtomicStack<MultithreadAction> actionStack = null;
    private int lastTickLevel;
    private int level;
    private int completedLevel;
    final ChunkPos pos;
    private boolean pendingBlockUpdates;
    private final ShortSet[] blockUpdatesBySection;
    private final BitSet blockLightUpdateBits = new BitSet();
    private final BitSet skyLightUpdateBits = new BitSet();
    private final LightingProvider lightingProvider;
    private final LevelUpdateListener levelUpdateListener;
    private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
    private boolean accessible;
    private boolean noLightingUpdates;
    private CompletableFuture<Void> field_26930 = CompletableFuture.completedFuture(null);

    public ChunkHolder(ChunkPos pos, int level, HeightLimitView world, LightingProvider lightingProvider, LevelUpdateListener levelUpdateListener, PlayersWatchingChunkProvider playersWatchingChunkProvider) {
        this.pos = pos;
        this.world = world;
        this.lightingProvider = lightingProvider;
        this.levelUpdateListener = levelUpdateListener;
        this.playersWatchingChunkProvider = playersWatchingChunkProvider;
        this.level = this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        this.completedLevel = this.lastTickLevel;
        this.setLevel(level);
        this.blockUpdatesBySection = new ShortSet[world.countVerticalSections()];
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getFutureFor(ChunkStatus leastStatus) {
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(leastStatus.getIndex());
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getValidFutureFor(ChunkStatus leastStatus) {
        if (ChunkHolder.getTargetStatusForLevel(this.level).isAtLeast(leastStatus)) {
            return this.getFutureFor(leastStatus);
        }
        return UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getTickingFuture() {
        return this.tickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getEntityTickingFuture() {
        return this.entityTickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getAccessibleFuture() {
        return this.accessibleFuture;
    }

    @Nullable
    public WorldChunk getWorldChunk() {
        CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture = this.getTickingFuture();
        Either either = completableFuture.getNow(null);
        if (either == null) {
            return null;
        }
        return either.left().orElse(null);
    }

    @Nullable
    public WorldChunk method_41205() {
        CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture = this.getAccessibleFuture();
        Either either = completableFuture.getNow(null);
        if (either == null) {
            return null;
        }
        return either.left().orElse(null);
    }

    @Nullable
    public ChunkStatus getCurrentStatus() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFutureFor(chunkStatus);
            if (!completableFuture.getNow(UNLOADED_CHUNK).left().isPresent()) continue;
            return chunkStatus;
        }
        return null;
    }

    @Nullable
    public Chunk getCurrentChunk() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            Optional optional;
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFutureFor(chunkStatus);
            if (completableFuture.isCompletedExceptionally() || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent()) continue;
            return (Chunk)optional.get();
        }
        return null;
    }

    public CompletableFuture<Chunk> getSavingFuture() {
        return this.savingFuture;
    }

    public void markForBlockUpdate(BlockPos pos) {
        WorldChunk worldChunk = this.getWorldChunk();
        if (worldChunk == null) {
            return;
        }
        int i = this.world.getSectionIndex(pos.getY());
        if (this.blockUpdatesBySection[i] == null) {
            this.pendingBlockUpdates = true;
            this.blockUpdatesBySection[i] = new ShortOpenHashSet();
        }
        this.blockUpdatesBySection[i].add(ChunkSectionPos.packLocal(pos));
    }

    public void markForLightUpdate(LightType lightType, int y) {
        Either either = this.getValidFutureFor(ChunkStatus.FEATURES).getNow(null);
        if (either == null) {
            return;
        }
        Chunk chunk = either.left().orElse(null);
        if (chunk == null) {
            return;
        }
        chunk.setNeedsSaving(true);
        WorldChunk worldChunk = this.getWorldChunk();
        if (worldChunk == null) {
            return;
        }
        int i = this.lightingProvider.getBottomY();
        int j = this.lightingProvider.getTopY();
        if (y < i || y > j) {
            return;
        }
        int k = y - i;
        if (lightType == LightType.SKY) {
            this.skyLightUpdateBits.set(k);
        } else {
            this.blockLightUpdateBits.set(k);
        }
    }

    public void flushUpdates(WorldChunk chunk) {
        int j;
        if (!this.pendingBlockUpdates && this.skyLightUpdateBits.isEmpty() && this.blockLightUpdateBits.isEmpty()) {
            return;
        }
        World world = chunk.getWorld();
        int i = 0;
        for (j = 0; j < this.blockUpdatesBySection.length; ++j) {
            i += this.blockUpdatesBySection[j] != null ? this.blockUpdatesBySection[j].size() : 0;
        }
        this.noLightingUpdates |= i >= 64;
        if (!this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
            this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(chunk.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits, true), !this.noLightingUpdates);
            this.skyLightUpdateBits.clear();
            this.blockLightUpdateBits.clear();
        }
        for (j = 0; j < this.blockUpdatesBySection.length; ++j) {
            ShortSet shortSet = this.blockUpdatesBySection[j];
            if (shortSet == null) continue;
            int k = this.world.sectionIndexToCoord(j);
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), k);
            if (shortSet.size() == 1) {
                BlockPos blockPos = chunkSectionPos.unpackBlockPos(shortSet.iterator().nextShort());
                BlockState blockState = world.getBlockState(blockPos);
                this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(blockPos, blockState), false);
                this.tryUpdateBlockEntityAt(world, blockPos, blockState);
            } else {
                ChunkSection chunkSection = chunk.getSection(j);
                ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, chunkSection, this.noLightingUpdates);
                this.sendPacketToPlayersWatching(chunkDeltaUpdateS2CPacket, false);
                chunkDeltaUpdateS2CPacket.visitUpdates((pos, state) -> this.tryUpdateBlockEntityAt(world, (BlockPos)pos, (BlockState)state));
            }
            this.blockUpdatesBySection[j] = null;
        }
        this.pendingBlockUpdates = false;
    }

    private void tryUpdateBlockEntityAt(World world, BlockPos pos, BlockState state) {
        if (state.hasBlockEntity()) {
            this.sendBlockEntityUpdatePacket(world, pos);
        }
    }

    private void sendBlockEntityUpdatePacket(World world, BlockPos pos) {
        Packet<ClientPlayPacketListener> packet;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && (packet = blockEntity.toUpdatePacket()) != null) {
            this.sendPacketToPlayersWatching(packet, false);
        }
    }

    private void sendPacketToPlayersWatching(Packet<?> packet, boolean onlyOnWatchDistanceEdge) {
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, onlyOnWatchDistanceEdge).forEach(player -> player.networkHandler.sendPacket(packet));
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getChunkAt(ChunkStatus targetStatus, ThreadedAnvilChunkStorage chunkStorage) {
        int i = targetStatus.getIndex();
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
        if (completableFuture != null) {
            Either<Chunk, Unloaded> either = completableFuture.getNow(field_36388);
            if (either == null) {
                String string = "value in future for status: " + targetStatus + " was incorrectly set to null at chunk: " + this.pos;
                throw chunkStorage.crash(new IllegalStateException("null value previously set for chunk status"), string);
            }
            if (either == field_36388 || either.right().isEmpty()) {
                return completableFuture;
            }
        }
        if (ChunkHolder.getTargetStatusForLevel(this.level).isAtLeast(targetStatus)) {
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture2 = chunkStorage.getChunk(this, targetStatus);
            this.combineSavingFuture(completableFuture2, "schedule " + targetStatus);
            this.futuresByStatus.set(i, completableFuture2);
            return completableFuture2;
        }
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    protected void combineSavingFuture(String thenDesc, CompletableFuture<?> then) {
        if (this.actionStack != null) {
            this.actionStack.push(new MultithreadAction(Thread.currentThread(), then, thenDesc));
        }
        this.savingFuture = this.savingFuture.thenCombine(then, (chunk, object) -> chunk);
    }

    private void combineSavingFuture(CompletableFuture<? extends Either<? extends Chunk, Unloaded>> then, String thenDesc) {
        if (this.actionStack != null) {
            this.actionStack.push(new MultithreadAction(Thread.currentThread(), then, thenDesc));
        }
        this.savingFuture = this.savingFuture.thenCombine(then, (chunk2, either) -> (Chunk)either.map(chunk -> chunk, unloaded -> chunk2));
    }

    public LevelType getLevelType() {
        return ChunkHolder.getLevelType(this.level);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCompletedLevel() {
        return this.completedLevel;
    }

    private void setCompletedLevel(int level) {
        this.completedLevel = level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private void method_31409(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture, Executor executor, LevelType levelType) {
        this.field_26930.cancel(false);
        CompletableFuture completableFuture2 = new CompletableFuture();
        completableFuture2.thenRunAsync(() -> threadedAnvilChunkStorage.onChunkStatusChange(this.pos, levelType), executor);
        this.field_26930 = completableFuture2;
        completableFuture.thenAccept(either -> either.ifLeft(worldChunk -> completableFuture2.complete(null)));
    }

    private void method_31408(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, LevelType levelType) {
        this.field_26930.cancel(false);
        threadedAnvilChunkStorage.onChunkStatusChange(this.pos, levelType);
    }

    protected void tick(ThreadedAnvilChunkStorage chunkStorage, Executor executor) {
        ChunkStatus chunkStatus = ChunkHolder.getTargetStatusForLevel(this.lastTickLevel);
        ChunkStatus chunkStatus2 = ChunkHolder.getTargetStatusForLevel(this.level);
        boolean bl = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        boolean bl2 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        LevelType levelType = ChunkHolder.getLevelType(this.lastTickLevel);
        LevelType levelType2 = ChunkHolder.getLevelType(this.level);
        if (bl) {
            int i;
            Either either = Either.right((Object)new Unloaded(){

                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos;
                }
            });
            int n = i = bl2 ? chunkStatus2.getIndex() + 1 : 0;
            while (i <= chunkStatus.getIndex()) {
                CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
                if (completableFuture == null) {
                    this.futuresByStatus.set(i, CompletableFuture.completedFuture(either));
                }
                ++i;
            }
        }
        boolean bl3 = levelType.isAfter(LevelType.BORDER);
        boolean bl4 = levelType2.isAfter(LevelType.BORDER);
        this.accessible |= bl4;
        if (!bl3 && bl4) {
            this.accessibleFuture = chunkStorage.makeChunkAccessible(this);
            this.method_31409(chunkStorage, this.accessibleFuture, executor, LevelType.BORDER);
            this.combineSavingFuture(this.accessibleFuture, "full");
        }
        if (bl3 && !bl4) {
            this.accessibleFuture.complete(UNLOADED_WORLD_CHUNK);
            this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        boolean bl5 = levelType.isAfter(LevelType.TICKING);
        boolean bl6 = levelType2.isAfter(LevelType.TICKING);
        if (!bl5 && bl6) {
            this.tickingFuture = chunkStorage.makeChunkTickable(this);
            this.method_31409(chunkStorage, this.tickingFuture, executor, LevelType.TICKING);
            this.combineSavingFuture(this.tickingFuture, "ticking");
        }
        if (bl5 && !bl6) {
            this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        boolean bl7 = levelType.isAfter(LevelType.ENTITY_TICKING);
        boolean bl8 = levelType2.isAfter(LevelType.ENTITY_TICKING);
        if (!bl7 && bl8) {
            if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
                throw Util.throwOrPause(new IllegalStateException());
            }
            this.entityTickingFuture = chunkStorage.makeChunkEntitiesTickable(this.pos);
            this.method_31409(chunkStorage, this.entityTickingFuture, executor, LevelType.ENTITY_TICKING);
            this.combineSavingFuture(this.entityTickingFuture, "entity ticking");
        }
        if (bl7 && !bl8) {
            this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        if (!levelType2.isAfter(levelType)) {
            this.method_31408(chunkStorage, levelType2);
        }
        this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
        this.lastTickLevel = this.level;
    }

    public static ChunkStatus getTargetStatusForLevel(int level) {
        if (level < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.byDistanceFromFull(level - 33);
    }

    public static LevelType getLevelType(int distance) {
        return LEVEL_TYPES[MathHelper.clamp(33 - distance + 1, 0, LEVEL_TYPES.length - 1)];
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public void updateAccessibleStatus() {
        this.accessible = ChunkHolder.getLevelType(this.level).isAfter(LevelType.BORDER);
    }

    public void setCompletedChunk(ReadOnlyChunk chunk) {
        for (int i = 0; i < this.futuresByStatus.length(); ++i) {
            Optional optional;
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
            if (completableFuture == null || (optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isEmpty() || !(optional.get() instanceof ProtoChunk)) continue;
            this.futuresByStatus.set(i, CompletableFuture.completedFuture(Either.left((Object)chunk)));
        }
        this.combineSavingFuture(CompletableFuture.completedFuture(Either.left((Object)chunk.getWrappedChunk())), "replaceProto");
    }

    public List<Pair<ChunkStatus, CompletableFuture<Either<Chunk, Unloaded>>>> collectFuturesByStatus() {
        ArrayList<Pair<ChunkStatus, CompletableFuture<Either<Chunk, Unloaded>>>> list = new ArrayList<Pair<ChunkStatus, CompletableFuture<Either<Chunk, Unloaded>>>>();
        for (int i = 0; i < CHUNK_STATUSES.size(); ++i) {
            list.add((Pair<ChunkStatus, CompletableFuture<Either<Chunk, Unloaded>>>)Pair.of((Object)CHUNK_STATUSES.get(i), this.futuresByStatus.get(i)));
        }
        return list;
    }

    @FunctionalInterface
    public static interface LevelUpdateListener {
        public void updateLevel(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
    }

    public static interface PlayersWatchingChunkProvider {
        public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos var1, boolean var2);
    }

    static final class MultithreadAction {
        private final Thread thread;
        private final CompletableFuture<?> action;
        private final String actionDesc;

        MultithreadAction(Thread thread, CompletableFuture<?> action, String actionDesc) {
            this.thread = thread;
            this.action = action;
            this.actionDesc = actionDesc;
        }
    }

    public static final class LevelType
    extends Enum<LevelType> {
        public static final /* enum */ LevelType INACCESSIBLE = new LevelType();
        public static final /* enum */ LevelType BORDER = new LevelType();
        public static final /* enum */ LevelType TICKING = new LevelType();
        public static final /* enum */ LevelType ENTITY_TICKING = new LevelType();
        private static final /* synthetic */ LevelType[] field_13878;

        public static LevelType[] values() {
            return (LevelType[])field_13878.clone();
        }

        public static LevelType valueOf(String string) {
            return Enum.valueOf(LevelType.class, string);
        }

        public boolean isAfter(LevelType levelType) {
            return this.ordinal() >= levelType.ordinal();
        }

        private static /* synthetic */ LevelType[] method_36576() {
            return new LevelType[]{INACCESSIBLE, BORDER, TICKING, ENTITY_TICKING};
        }

        static {
            field_13878 = LevelType.method_36576();
        }
    }

    public static interface Unloaded {
        public static final Unloaded INSTANCE = new Unloaded(){

            public String toString() {
                return "UNLOADED";
            }
        };
    }
}

