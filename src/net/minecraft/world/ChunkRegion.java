/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiTickScheduler;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkRegion
implements IWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Chunk> chunks;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int width;
    private final ServerWorld world;
    private final long seed;
    private final int seaLevel;
    private final LevelProperties levelProperties;
    private final Random random;
    private final Dimension dimension;
    private final ChunkGeneratorConfig generatorSettings;
    private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<Block>(blockPos -> this.getChunk((BlockPos)blockPos).getBlockTickScheduler());
    private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<Fluid>(blockPos -> this.getChunk((BlockPos)blockPos).getFluidTickScheduler());

    public ChunkRegion(ServerWorld world, List<Chunk> chunks) {
        int i = MathHelper.floor(Math.sqrt(chunks.size()));
        if (i * i != chunks.size()) {
            throw new IllegalStateException("Cache size is not a square.");
        }
        ChunkPos chunkPos = chunks.get(chunks.size() / 2).getPos();
        this.chunks = chunks;
        this.centerChunkX = chunkPos.x;
        this.centerChunkZ = chunkPos.z;
        this.width = i;
        this.world = world;
        this.seed = world.getSeed();
        this.generatorSettings = world.getChunkManager().getChunkGenerator().getConfig();
        this.seaLevel = world.getSeaLevel();
        this.levelProperties = world.getLevelProperties();
        this.random = world.getRandom();
        this.dimension = world.getDimension();
    }

    public int getCenterChunkX() {
        return this.centerChunkX;
    }

    public int getCenterChunkZ() {
        return this.centerChunkZ;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY);
    }

    @Override
    @Nullable
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        Chunk chunk;
        if (this.isChunkLoaded(chunkX, chunkZ)) {
            ChunkPos chunkPos = this.chunks.get(0).getPos();
            int i = chunkX - chunkPos.x;
            int j = chunkZ - chunkPos.z;
            chunk = this.chunks.get(i + j * this.width);
            if (chunk.getStatus().isAtLeast(leastStatus)) {
                return chunk;
            }
        } else {
            chunk = null;
        }
        if (!create) {
            return null;
        }
        Chunk chunk2 = this.chunks.get(0);
        Chunk chunk3 = this.chunks.get(this.chunks.size() - 1);
        LOGGER.error("Requested chunk : {} {}", (Object)chunkX, (Object)chunkZ);
        LOGGER.error("Region bounds : {} {} | {} {}", (Object)chunk2.getPos().x, (Object)chunk2.getPos().z, (Object)chunk3.getPos().x, (Object)chunk3.getPos().z);
        if (chunk != null) {
            throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", leastStatus, chunk.getStatus(), chunkX, chunkZ));
        }
        throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", chunkX, chunkZ));
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        Chunk chunk = this.chunks.get(0);
        Chunk chunk2 = this.chunks.get(this.chunks.size() - 1);
        return chunkX >= chunk.getPos().x && chunkX <= chunk2.getPos().x && chunkZ >= chunk.getPos().z && chunkZ <= chunk2.getPos().z;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4).getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getChunk(pos).getFluidState(pos);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, Predicate<Entity> targetPredicate) {
        return null;
    }

    @Override
    public int getAmbientDarkness() {
        return 0;
    }

    @Override
    public Biome getBiome(BlockPos blockPos) {
        Biome biome = this.getChunk(blockPos).getBiomeArray()[blockPos.getX() & 0xF | (blockPos.getZ() & 0xF) << 4];
        if (biome == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", blockPos));
        }
        return biome;
    }

    @Override
    public int getLightLevel(LightType type, BlockPos pos) {
        return this.getChunkManager().getLightingProvider().get(type).getLightLevel(pos);
    }

    @Override
    public int getLightLevel(BlockPos blockPos, int i) {
        return this.getChunk(blockPos).getLightLevel(blockPos, i, this.getDimension().hasSkyLight());
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean bl) {
        BlockState blockState = this.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        if (bl) {
            BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? this.getBlockEntity(pos) : null;
            Block.dropStacks(blockState, this.world, pos, blockEntity);
        }
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        Chunk chunk = this.getChunk(pos);
        BlockEntity blockEntity = chunk.getBlockEntity(pos);
        if (blockEntity != null) {
            return blockEntity;
        }
        CompoundTag compoundTag = chunk.getBlockEntityTagAt(pos);
        if (compoundTag != null) {
            if ("DUMMY".equals(compoundTag.getString("id"))) {
                Block block = this.getBlockState(pos).getBlock();
                if (!(block instanceof BlockEntityProvider)) {
                    return null;
                }
                blockEntity = ((BlockEntityProvider)((Object)block)).createBlockEntity(this.world);
            } else {
                blockEntity = BlockEntity.createFromTag(compoundTag);
            }
            if (blockEntity != null) {
                chunk.setBlockEntity(pos, blockEntity);
                return blockEntity;
            }
        }
        if (chunk.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", (Object)pos);
        }
        return null;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        Block block;
        Chunk chunk = this.getChunk(pos);
        BlockState blockState = chunk.setBlockState(pos, state, false);
        if (blockState != null) {
            this.world.onBlockChanged(pos, blockState, state);
        }
        if ((block = state.getBlock()).hasBlockEntity()) {
            if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
                chunk.setBlockEntity(pos, ((BlockEntityProvider)((Object)block)).createBlockEntity(this));
            } else {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("x", pos.getX());
                compoundTag.putInt("y", pos.getY());
                compoundTag.putInt("z", pos.getZ());
                compoundTag.putString("id", "DUMMY");
                chunk.addPendingBlockEntityTag(compoundTag);
            }
        } else if (blockState != null && blockState.getBlock().hasBlockEntity()) {
            chunk.removeBlockEntity(pos);
        }
        if (state.shouldPostProcess(this, pos)) {
            this.markBlockForPostProcessing(pos);
        }
        return true;
    }

    private void markBlockForPostProcessing(BlockPos blockPos) {
        this.getChunk(blockPos).markBlockForPostProcessing(blockPos);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        int i = MathHelper.floor(entity.x / 16.0);
        int j = MathHelper.floor(entity.z / 16.0);
        this.getChunk(i, j).addEntity(entity);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
        return true;
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    @Deprecated
    public ServerWorld getWorld() {
        return this.world;
    }

    @Override
    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        if (!this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        }
        return new LocalDifficulty(this.world.getDifficulty(), this.world.getTimeOfDay(), 0L, this.world.getMoonSize());
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.world.getChunkManager();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public void updateNeighbors(BlockPos pos, Block block) {
    }

    @Override
    public int getTop(Heightmap.Type type, int x, int z) {
        return this.getChunk(x >> 4, z >> 4).sampleHeightmap(type, x & 0xF, z & 0xF) + 1;
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public void playLevelEvent(@Nullable PlayerEntity player, int eventId, BlockPos blockPos, int data) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public BlockPos getSpawnPos() {
        return this.world.getSpawnPos();
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public boolean testBlockState(BlockPos blockPos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(blockPos));
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity except, Box box, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    public List<PlayerEntity> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    public BlockPos getTopPosition(Heightmap.Type type, BlockPos blockPos) {
        return new BlockPos(blockPos.getX(), this.getTop(type, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
    }

    @Override
    @Deprecated
    public /* synthetic */ World getWorld() {
        return this.getWorld();
    }
}
