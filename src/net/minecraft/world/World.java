/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public abstract class World
implements WorldAccess,
AutoCloseable {
    public static final Codec<RegistryKey<World>> CODEC = RegistryKey.createCodec(RegistryKeys.WORLD);
    public static final RegistryKey<World> OVERWORLD = RegistryKey.of(RegistryKeys.WORLD, new Identifier("overworld"));
    public static final RegistryKey<World> NETHER = RegistryKey.of(RegistryKeys.WORLD, new Identifier("the_nether"));
    public static final RegistryKey<World> END = RegistryKey.of(RegistryKeys.WORLD, new Identifier("the_end"));
    public static final int HORIZONTAL_LIMIT = 30000000;
    public static final int MAX_UPDATE_DEPTH = 512;
    public static final int field_30967 = 32;
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final int field_30968 = 15;
    public static final int field_30969 = 24000;
    public static final int MAX_Y = 20000000;
    public static final int MIN_Y = -20000000;
    protected final List<BlockEntityTickInvoker> blockEntityTickers = Lists.newArrayList();
    protected final NeighborUpdater neighborUpdater;
    private final List<BlockEntityTickInvoker> pendingBlockEntityTickers = Lists.newArrayList();
    private boolean iteratingTickingBlockEntities;
    private final Thread thread;
    private final boolean debugWorld;
    private int ambientDarkness;
    protected int lcgBlockSeed = Random.create().nextInt();
    protected final int lcgBlockSeedIncrement = 1013904223;
    protected float rainGradientPrev;
    protected float rainGradient;
    protected float thunderGradientPrev;
    protected float thunderGradient;
    public final Random random = Random.create();
    @Deprecated
    private final Random threadSafeRandom = Random.createThreadSafe();
    private final RegistryKey<DimensionType> dimension;
    private final RegistryEntry<DimensionType> dimensionEntry;
    protected final MutableWorldProperties properties;
    private final Supplier<Profiler> profiler;
    public final boolean isClient;
    private final WorldBorder border;
    private final BiomeAccess biomeAccess;
    private final RegistryKey<World> registryKey;
    private long tickOrder;

    protected World(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        this.profiler = profiler;
        this.properties = properties;
        this.dimensionEntry = dimension;
        this.dimension = dimension.getKey().orElseThrow(() -> new IllegalArgumentException("Dimension must be registered, got " + dimension));
        final DimensionType dimensionType = dimension.value();
        this.registryKey = registryRef;
        this.isClient = isClient;
        this.border = dimensionType.coordinateScale() != 1.0 ? new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / dimensionType.coordinateScale();
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / dimensionType.coordinateScale();
            }
        } : new WorldBorder();
        this.thread = Thread.currentThread();
        this.biomeAccess = new BiomeAccess(this, seed);
        this.debugWorld = debugWorld;
        this.neighborUpdater = new ChainRestrictedNeighborUpdater(this, maxChainedNeighborUpdates);
    }

    @Override
    public boolean isClient() {
        return this.isClient;
    }

    @Override
    @Nullable
    public MinecraftServer getServer() {
        return null;
    }

    public boolean isInBuildLimit(BlockPos pos) {
        return !this.isOutOfHeightLimit(pos) && World.isValidHorizontally(pos);
    }

    public static boolean isValid(BlockPos pos) {
        return !World.isInvalidVertically(pos.getY()) && World.isValidHorizontally(pos);
    }

    private static boolean isValidHorizontally(BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
    }

    private static boolean isInvalidVertically(int y) {
        return y < -20000000 || y >= 20000000;
    }

    public WorldChunk getWorldChunk(BlockPos pos) {
        return this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    @Override
    public WorldChunk getChunk(int i, int j) {
        return (WorldChunk)this.getChunk(i, j, ChunkStatus.FULL);
    }

    @Override
    @Nullable
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        Chunk chunk = this.getChunkManager().getChunk(chunkX, chunkZ, leastStatus, create);
        if (chunk == null && create) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return chunk;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        return this.setBlockState(pos, state, flags, 512);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (this.isOutOfHeightLimit(pos)) {
            return false;
        }
        if (!this.isClient && this.isDebugWorld()) {
            return false;
        }
        WorldChunk worldChunk = this.getWorldChunk(pos);
        Block block = state.getBlock();
        BlockState blockState = worldChunk.setBlockState(pos, state, (flags & 0x40) != 0);
        if (blockState != null) {
            BlockState blockState2 = this.getBlockState(pos);
            if ((flags & 0x80) == 0 && blockState2 != blockState && (blockState2.getOpacity(this, pos) != blockState.getOpacity(this, pos) || blockState2.getLuminance() != blockState.getLuminance() || blockState2.hasSidedTransparency() || blockState.hasSidedTransparency())) {
                this.getProfiler().push("queueCheckLight");
                this.getChunkManager().getLightingProvider().checkBlock(pos);
                this.getProfiler().pop();
            }
            if (blockState2 == state) {
                if (blockState != blockState2) {
                    this.scheduleBlockRerenderIfNeeded(pos, blockState, blockState2);
                }
                if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && (this.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
                    this.updateListeners(pos, blockState, state, flags);
                }
                if ((flags & 1) != 0) {
                    this.updateNeighbors(pos, blockState.getBlock());
                    if (!this.isClient && state.hasComparatorOutput()) {
                        this.updateComparators(pos, block);
                    }
                }
                if ((flags & 0x10) == 0 && maxUpdateDepth > 0) {
                    int i = flags & 0xFFFFFFDE;
                    blockState.prepare(this, pos, i, maxUpdateDepth - 1);
                    state.updateNeighbors(this, pos, i, maxUpdateDepth - 1);
                    state.prepare(this, pos, i, maxUpdateDepth - 1);
                }
                this.onBlockChanged(pos, blockState, blockState2);
            }
            return true;
        }
        return false;
    }

    public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        FluidState fluidState = this.getFluidState(pos);
        return this.setBlockState(pos, fluidState.getBlockState(), 3 | (move ? 64 : 0));
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        boolean bl;
        BlockState blockState = this.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        FluidState fluidState = this.getFluidState(pos);
        if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
            this.syncWorldEvent(2001, pos, Block.getRawIdFromState(blockState));
        }
        if (drop) {
            BlockEntity blockEntity = blockState.hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(blockState, this, pos, blockEntity, breakingEntity, ItemStack.EMPTY);
        }
        if (bl = this.setBlockState(pos, fluidState.getBlockState(), 3, maxUpdateDepth)) {
            this.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(breakingEntity, blockState));
        }
        return bl;
    }

    public void addBlockBreakParticles(BlockPos pos, BlockState state) {
    }

    public boolean setBlockState(BlockPos pos, BlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public abstract void updateListeners(BlockPos var1, BlockState var2, BlockState var3, int var4);

    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
    }

    public void updateNeighborsAlways(BlockPos pos, Block sourceBlock) {
    }

    public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
    }

    public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
    }

    public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    }

    @Override
    public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        this.neighborUpdater.replaceWithStateForNeighborUpdate(direction, neighborState, pos, neighborPos, flags, maxUpdateDepth);
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        int i = x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000 ? this.getSeaLevel() + 1 : (this.isChunkLoaded(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z)) ? this.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z)).sampleHeightmap(heightmap, x & 0xF, z & 0xF) + 1 : this.getBottomY());
        return i;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.getChunkManager().getLightingProvider();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (this.isOutOfHeightLimit(pos)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        WorldChunk worldChunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
        return worldChunk.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (this.isOutOfHeightLimit(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        WorldChunk worldChunk = this.getWorldChunk(pos);
        return worldChunk.getFluidState(pos);
    }

    public boolean isDay() {
        return !this.getDimension().hasFixedTime() && this.ambientDarkness < 4;
    }

    public boolean isNight() {
        return !this.getDimension().hasFixedTime() && !this.isDay();
    }

    public void playSound(@Nullable Entity except, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        PlayerEntity playerEntity;
        this.playSound(except instanceof PlayerEntity ? (playerEntity = (PlayerEntity)except) : null, pos, sound, category, volume, pitch);
    }

    @Override
    public void playSound(@Nullable PlayerEntity except, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.playSound(except, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch);
    }

    public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, RegistryEntry<SoundEvent> var8, SoundCategory var9, float var10, float var11, long var12);

    public void playSound(@Nullable PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, long seed) {
        this.playSound(except, x, y, z, Registries.SOUND_EVENT.getEntry(sound), category, volume, pitch, seed);
    }

    public abstract void playSoundFromEntity(@Nullable PlayerEntity var1, Entity var2, RegistryEntry<SoundEvent> var3, SoundCategory var4, float var5, float var6, long var7);

    public void playSound(@Nullable PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.playSound(except, x, y, z, sound, category, volume, pitch, this.threadSafeRandom.nextLong());
    }

    public void playSoundFromEntity(@Nullable PlayerEntity except, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.playSoundFromEntity(except, entity, Registries.SOUND_EVENT.getEntry(sound), category, volume, pitch, this.threadSafeRandom.nextLong());
    }

    public void playSoundAtBlockCenter(BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        this.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch, useDistance);
    }

    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public float getSkyAngleRadians(float tickDelta) {
        float f = this.getSkyAngle(tickDelta);
        return f * ((float)Math.PI * 2);
    }

    public void addBlockEntityTicker(BlockEntityTickInvoker ticker) {
        (this.iteratingTickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add(ticker);
    }

    protected void tickBlockEntities() {
        Profiler profiler = this.getProfiler();
        profiler.push("blockEntities");
        this.iteratingTickingBlockEntities = true;
        if (!this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }
        Iterator<BlockEntityTickInvoker> iterator = this.blockEntityTickers.iterator();
        while (iterator.hasNext()) {
            BlockEntityTickInvoker blockEntityTickInvoker = iterator.next();
            if (blockEntityTickInvoker.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (!this.shouldTickBlockPos(blockEntityTickInvoker.getPos())) continue;
            blockEntityTickInvoker.tick();
        }
        this.iteratingTickingBlockEntities = false;
        profiler.pop();
    }

    public <T extends Entity> void tickEntity(Consumer<T> tickConsumer, T entity) {
        try {
            tickConsumer.accept(entity);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking entity");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
            entity.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public boolean shouldUpdatePostDeath(Entity entity) {
        return true;
    }

    public boolean shouldTickBlocksInChunk(long chunkPos) {
        return true;
    }

    public boolean shouldTickBlockPos(BlockPos pos) {
        return this.shouldTickBlocksInChunk(ChunkPos.toLong(pos));
    }

    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, ExplosionSourceType explosionSourceType) {
        return this.createExplosion(entity, null, null, x, y, z, power, false, explosionSourceType);
    }

    public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType) {
        return this.createExplosion(entity, null, null, x, y, z, power, createFire, explosionSourceType);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, Vec3d pos, float power, boolean createFire, ExplosionSourceType explosionSourceType) {
        return this.createExplosion(entity, damageSource, behavior, pos.getX(), pos.getY(), pos.getZ(), power, createFire, explosionSourceType);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType) {
        return this.createExplosion(entity, damageSource, behavior, x, y, z, power, createFire, explosionSourceType, true);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType, boolean particles) {
        Explosion.DestructionType destructionType = switch (explosionSourceType) {
            default -> throw new IncompatibleClassChangeError();
            case ExplosionSourceType.NONE -> Explosion.DestructionType.KEEP;
            case ExplosionSourceType.BLOCK -> this.getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY);
            case ExplosionSourceType.MOB -> {
                if (this.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    yield this.getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY);
                }
                yield Explosion.DestructionType.KEEP;
            }
            case ExplosionSourceType.TNT -> this.getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY);
        };
        Explosion explosion = new Explosion(this, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(particles);
        return explosion;
    }

    private Explosion.DestructionType getDestructionType(GameRules.Key<GameRules.BooleanRule> gameRuleKey) {
        return this.getGameRules().getBoolean(gameRuleKey) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY;
    }

    public abstract String asString();

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        if (this.isOutOfHeightLimit(pos)) {
            return null;
        }
        if (!this.isClient && Thread.currentThread() != this.thread) {
            return null;
        }
        return this.getWorldChunk(pos).getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        BlockPos blockPos = blockEntity.getPos();
        if (this.isOutOfHeightLimit(blockPos)) {
            return;
        }
        this.getWorldChunk(blockPos).addBlockEntity(blockEntity);
    }

    public void removeBlockEntity(BlockPos pos) {
        if (this.isOutOfHeightLimit(pos)) {
            return;
        }
        this.getWorldChunk(pos).removeBlockEntity(pos);
    }

    public boolean canSetBlock(BlockPos pos) {
        if (this.isOutOfHeightLimit(pos)) {
            return false;
        }
        return this.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
        if (this.isOutOfHeightLimit(pos)) {
            return false;
        }
        Chunk chunk = this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        }
        return chunk.getBlockState(pos).isSolidSurface(this, pos, entity, direction);
    }

    public boolean isTopSolid(BlockPos pos, Entity entity) {
        return this.isDirectionSolid(pos, entity, Direction.UP);
    }

    public void calculateAmbientDarkness() {
        double d = 1.0 - (double)(this.getRainGradient(1.0f) * 5.0f) / 16.0;
        double e = 1.0 - (double)(this.getThunderGradient(1.0f) * 5.0f) / 16.0;
        double f = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.getSkyAngle(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.ambientDarkness = (int)((1.0 - f * d * e) * 11.0);
    }

    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        this.getChunkManager().setMobSpawnOptions(spawnMonsters, spawnAnimals);
    }

    public BlockPos getSpawnPos() {
        BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
        if (!this.getWorldBorder().contains(blockPos)) {
            blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockPos;
    }

    public float getSpawnAngle() {
        return this.properties.getSpawnAngle();
    }

    protected void initWeatherGradients() {
        if (this.properties.isRaining()) {
            this.rainGradient = 1.0f;
            if (this.properties.isThundering()) {
                this.thunderGradient = 1.0f;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.getChunkManager().close();
    }

    @Override
    @Nullable
    public BlockView getChunkAsView(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate) {
        this.getProfiler().visit("getEntities");
        ArrayList list = Lists.newArrayList();
        this.getEntityLookup().forEachIntersects(box, entity -> {
            if (entity != except && predicate.test((Entity)entity)) {
                list.add(entity);
            }
            if (entity instanceof EnderDragonEntity) {
                for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
                    if (entity == except || !predicate.test(enderDragonPart)) continue;
                    list.add(enderDragonPart);
                }
            }
        });
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
        ArrayList list = Lists.newArrayList();
        this.collectEntitiesByType(filter, box, predicate, list);
        return list;
    }

    public <T extends Entity> void collectEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate, List<? super T> result) {
        this.collectEntitiesByType(filter, box, predicate, result, Integer.MAX_VALUE);
    }

    public <T extends Entity> void collectEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate, List<? super T> result, int limit) {
        this.getProfiler().visit("getEntities");
        this.getEntityLookup().forEachIntersects(filter, box, entity -> {
            if (predicate.test(entity)) {
                result.add((Object)entity);
                if (result.size() >= limit) {
                    return LazyIterationConsumer.NextIteration.ABORT;
                }
            }
            if (entity instanceof EnderDragonEntity) {
                EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
                for (EnderDragonPart enderDragonPart : enderDragonEntity.getBodyParts()) {
                    Entity entity2 = (Entity)filter.downcast(enderDragonPart);
                    if (entity2 == null || !predicate.test(entity2)) continue;
                    result.add((Object)entity2);
                    if (result.size() < limit) continue;
                    return LazyIterationConsumer.NextIteration.ABORT;
                }
            }
            return LazyIterationConsumer.NextIteration.CONTINUE;
        });
    }

    @Nullable
    public abstract Entity getEntityById(int var1);

    public void markDirty(BlockPos pos) {
        if (this.isChunkLoaded(pos)) {
            this.getWorldChunk(pos).setNeedsSaving(true);
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    public int getReceivedStrongRedstonePower(BlockPos pos) {
        int i = 0;
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST))) >= 15) {
            return i;
        }
        return i;
    }

    public boolean isEmittingRedstonePower(BlockPos pos, Direction direction) {
        return this.getEmittedRedstonePower(pos, direction) > 0;
    }

    public int getEmittedRedstonePower(BlockPos pos, Direction direction) {
        BlockState blockState = this.getBlockState(pos);
        int i = blockState.getWeakRedstonePower(this, pos, direction);
        if (blockState.isSolidBlock(this, pos)) {
            return Math.max(i, this.getReceivedStrongRedstonePower(pos));
        }
        return i;
    }

    public boolean isReceivingRedstonePower(BlockPos pos) {
        if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
    }

    public int getReceivedRedstonePower(BlockPos pos) {
        int i = 0;
        for (Direction direction : DIRECTIONS) {
            int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
            if (j >= 15) {
                return 15;
            }
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public void disconnect() {
    }

    public long getTime() {
        return this.properties.getTime();
    }

    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        return true;
    }

    public void sendEntityStatus(Entity entity, byte status) {
    }

    public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
        this.getBlockState(pos).onSyncedBlockEvent(this, pos, type, data);
    }

    @Override
    public WorldProperties getLevelProperties() {
        return this.properties;
    }

    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }

    public float getThunderGradient(float delta) {
        return MathHelper.lerp(delta, this.thunderGradientPrev, this.thunderGradient) * this.getRainGradient(delta);
    }

    public void setThunderGradient(float thunderGradient) {
        float f;
        this.thunderGradientPrev = f = MathHelper.clamp(thunderGradient, 0.0f, 1.0f);
        this.thunderGradient = f;
    }

    public float getRainGradient(float delta) {
        return MathHelper.lerp(delta, this.rainGradientPrev, this.rainGradient);
    }

    public void setRainGradient(float rainGradient) {
        float f;
        this.rainGradientPrev = f = MathHelper.clamp(rainGradient, 0.0f, 1.0f);
        this.rainGradient = f;
    }

    public boolean isThundering() {
        if (!this.getDimension().hasSkyLight() || this.getDimension().hasCeiling()) {
            return false;
        }
        return (double)this.getThunderGradient(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainGradient(1.0f) > 0.2;
    }

    public boolean hasRain(BlockPos pos) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.isSkyVisible(pos)) {
            return false;
        }
        if (this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        Biome biome = this.getBiome(pos).value();
        return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.doesNotSnow(pos);
    }

    public boolean hasHighHumidity(BlockPos pos) {
        Biome biome = this.getBiome(pos).value();
        return biome.hasHighHumidity();
    }

    @Nullable
    public abstract MapState getMapState(String var1);

    public abstract void putMapState(String var1, MapState var2);

    public abstract int getNextMapId();

    public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
    }

    public CrashReportSection addDetailsToCrashReport(CrashReport report) {
        CrashReportSection crashReportSection = report.addElement("Affected level", 1);
        crashReportSection.add("All players", () -> this.getPlayers().size() + " total; " + this.getPlayers());
        crashReportSection.add("Chunk stats", this.getChunkManager()::getDebugString);
        crashReportSection.add("Level dimension", () -> this.getRegistryKey().getValue().toString());
        try {
            this.properties.populateCrashReport(crashReportSection, this);
        }
        catch (Throwable throwable) {
            crashReportSection.add("Level Data Unobtainable", throwable);
        }
        return crashReportSection;
    }

    public abstract void setBlockBreakingInfo(int var1, BlockPos var2, int var3);

    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateComparators(BlockPos pos, Block block) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (!this.isChunkLoaded(blockPos)) continue;
            BlockState blockState = this.getBlockState(blockPos);
            if (blockState.isOf(Blocks.COMPARATOR)) {
                this.updateNeighbor(blockState, blockPos, block, pos, false);
                continue;
            }
            if (!blockState.isSolidBlock(this, blockPos) || !(blockState = this.getBlockState(blockPos = blockPos.offset(direction))).isOf(Blocks.COMPARATOR)) continue;
            this.updateNeighbor(blockState, blockPos, block, pos, false);
        }
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        long l = 0L;
        float f = 0.0f;
        if (this.isChunkLoaded(pos)) {
            f = this.getMoonSize();
            l = this.getWorldChunk(pos).getInhabitedTime();
        }
        return new LocalDifficulty(this.getDifficulty(), this.getTimeOfDay(), l, f);
    }

    @Override
    public int getAmbientDarkness() {
        return this.ambientDarkness;
    }

    public void setLightningTicksLeft(int lightningTicksLeft) {
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.border;
    }

    public void sendPacket(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Override
    public DimensionType getDimension() {
        return this.dimensionEntry.value();
    }

    public RegistryKey<DimensionType> getDimensionKey() {
        return this.dimension;
    }

    public RegistryEntry<DimensionType> getDimensionEntry() {
        return this.dimensionEntry;
    }

    public RegistryKey<World> getRegistryKey() {
        return this.registryKey;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(pos));
    }

    @Override
    public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
        return state.test(this.getFluidState(pos));
    }

    public abstract RecipeManager getRecipeManager();

    public BlockPos getRandomPosInChunk(int x, int y, int z, int i) {
        this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
        int j = this.lcgBlockSeed >> 2;
        return new BlockPos(x + (j & 0xF), y + (j >> 16 & i), z + (j >> 8 & 0xF));
    }

    public boolean isSavingDisabled() {
        return false;
    }

    public Profiler getProfiler() {
        return this.profiler.get();
    }

    public Supplier<Profiler> getProfilerSupplier() {
        return this.profiler;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return this.biomeAccess;
    }

    public final boolean isDebugWorld() {
        return this.debugWorld;
    }

    protected abstract EntityLookup<Entity> getEntityLookup();

    @Override
    public long getTickOrder() {
        return this.tickOrder++;
    }

    @Override
    public /* synthetic */ Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ);
    }

    public static final class ExplosionSourceType
    extends Enum<ExplosionSourceType> {
        public static final /* enum */ ExplosionSourceType NONE = new ExplosionSourceType();
        public static final /* enum */ ExplosionSourceType BLOCK = new ExplosionSourceType();
        public static final /* enum */ ExplosionSourceType MOB = new ExplosionSourceType();
        public static final /* enum */ ExplosionSourceType TNT = new ExplosionSourceType();
        private static final /* synthetic */ ExplosionSourceType[] field_40892;

        public static ExplosionSourceType[] values() {
            return (ExplosionSourceType[])field_40892.clone();
        }

        public static ExplosionSourceType valueOf(String string) {
            return Enum.valueOf(ExplosionSourceType.class, string);
        }

        private static /* synthetic */ ExplosionSourceType[] method_46670() {
            return new ExplosionSourceType[]{NONE, BLOCK, MOB, TNT};
        }

        static {
            field_40892 = ExplosionSourceType.method_46670();
        }
    }
}

