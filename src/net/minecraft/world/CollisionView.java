/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import org.jetbrains.annotations.Nullable;

public interface CollisionView
extends BlockRenderView {
    default public boolean isAir(BlockPos pos) {
        return this.getBlockState(pos).isAir();
    }

    default public boolean method_8626(BlockPos blockPos) {
        if (blockPos.getY() >= this.getSeaLevel()) {
            return this.isSkyVisible(blockPos);
        }
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
        if (!this.isSkyVisible(blockPos2)) {
            return false;
        }
        blockPos2 = blockPos2.down();
        while (blockPos2.getY() > blockPos.getY()) {
            BlockState blockState = this.getBlockState(blockPos2);
            if (blockState.getOpacity(this, blockPos2) > 0 && !blockState.getMaterial().isLiquid()) {
                return false;
            }
            blockPos2 = blockPos2.down();
        }
        return true;
    }

    public int getLightLevel(BlockPos var1, int var2);

    @Nullable
    public Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean isChunkLoaded(int var1, int var2);

    public BlockPos getTopPosition(Heightmap.Type var1, BlockPos var2);

    public int getTop(Heightmap.Type var1, int var2, int var3);

    default public float getBrightness(BlockPos blockPos) {
        return this.getDimension().getLightLevelToBrightness()[this.getLightLevel(blockPos)];
    }

    public int getAmbientDarkness();

    public WorldBorder getWorldBorder();

    public boolean intersectsEntities(@Nullable Entity var1, VoxelShape var2);

    default public int getEmittedStrongRedstonePower(BlockPos pos, Direction direction) {
        return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
    }

    public boolean isClient();

    public int getSeaLevel();

    default public Chunk getChunk(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    default public Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
    }

    default public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredState) {
        return this.getChunk(chunkX, chunkZ, requiredState, true);
    }

    default public ChunkStatus getLeastChunkStatusForCollisionCalculation() {
        return ChunkStatus.EMPTY;
    }

    default public boolean canPlace(BlockState state, BlockPos pos, EntityContext context) {
        VoxelShape voxelShape = state.getCollisionShape(this, pos, context);
        return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset(pos.getX(), pos.getY(), pos.getZ()));
    }

    default public boolean intersectsEntities(Entity entity) {
        return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
    }

    default public boolean doesNotCollide(Box box) {
        return this.doesNotCollide(null, box, Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity) {
        return this.doesNotCollide(entity, entity.getBoundingBox(), Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity, Box box) {
        return this.doesNotCollide(entity, box, Collections.emptySet());
    }

    default public boolean doesNotCollide(@Nullable Entity entity, Box entityBoundingBox, Set<Entity> otherEntities) {
        return this.getCollisions(entity, entityBoundingBox, otherEntities).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Set<Entity> excluded) {
        return Streams.concat((Stream[])new Stream[]{this.method_20812(entity, box), this.method_20743(entity, box, excluded)});
    }

    default public Stream<VoxelShape> method_20812(final @Nullable Entity entity, Box box) {
        int i = MathHelper.floor(box.x1 - 1.0E-7) - 1;
        int j = MathHelper.floor(box.x2 + 1.0E-7) + 1;
        int k = MathHelper.floor(box.y1 - 1.0E-7) - 1;
        int l = MathHelper.floor(box.y2 + 1.0E-7) + 1;
        int m = MathHelper.floor(box.z1 - 1.0E-7) - 1;
        int n = MathHelper.floor(box.z2 + 1.0E-7) + 1;
        final EntityContext entityContext = entity == null ? EntityContext.absent() : EntityContext.of(entity);
        final CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        final VoxelShape voxelShape = VoxelShapes.cuboid(box);
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280){
            boolean field_19296;
            {
                super(l, i);
                this.field_19296 = entity == null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
                if (!this.field_19296) {
                    this.field_19296 = true;
                    VoxelShape voxelShape4 = CollisionView.this.getWorldBorder().asVoxelShape();
                    boolean bl = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
                    boolean bl2 = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
                    if (!bl && bl2) {
                        consumer.accept(voxelShape4);
                        return true;
                    }
                }
                while (cuboidBlockIterator.step()) {
                    VoxelShape voxelShape2;
                    VoxelShape voxelShape3;
                    int n;
                    int m;
                    Chunk chunk;
                    int i = cuboidBlockIterator.getX();
                    int j = cuboidBlockIterator.getY();
                    int k = cuboidBlockIterator.getZ();
                    int l = cuboidBlockIterator.method_20789();
                    if (l == 3 || (chunk = CollisionView.this.getChunk(m = i >> 4, n = k >> 4, CollisionView.this.getLeastChunkStatusForCollisionCalculation(), false)) == null) continue;
                    mutable.set(i, j, k);
                    BlockState blockState = chunk.getBlockState(mutable);
                    if (l == 1 && !blockState.method_17900() || l == 2 && blockState.getBlock() != Blocks.MOVING_PISTON || !VoxelShapes.matchesAnywhere(voxelShape, voxelShape3 = (voxelShape2 = blockState.getCollisionShape(CollisionView.this, mutable, entityContext)).offset(i, j, k), BooleanBiFunction.AND)) continue;
                    consumer.accept(voxelShape3);
                    return true;
                }
                return false;
            }
        }, false);
    }

    default public boolean isWaterAt(BlockPos pos) {
        return this.getFluidState(pos).matches(FluidTags.WATER);
    }

    default public boolean intersectsFluid(Box box) {
        int i = MathHelper.floor(box.x1);
        int j = MathHelper.ceil(box.x2);
        int k = MathHelper.floor(box.y1);
        int l = MathHelper.ceil(box.y2);
        int m = MathHelper.floor(box.z1);
        int n = MathHelper.ceil(box.z2);
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        BlockState blockState = this.getBlockState(pooledMutable.set(o, p, q));
                        if (blockState.getFluidState().isEmpty()) continue;
                        boolean bl = true;
                        return bl;
                    }
                }
            }
        }
        return false;
    }

    default public int getLightLevel(BlockPos blockPos) {
        return this.method_8603(blockPos, this.getAmbientDarkness());
    }

    default public int method_8603(BlockPos blockPos, int darkness) {
        if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
            return 15;
        }
        return this.getLightLevel(blockPos, darkness);
    }

    @Deprecated
    default public boolean isBlockLoaded(BlockPos blockPos) {
        return this.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Deprecated
    default public boolean isAreaLoaded(BlockPos min, BlockPos max) {
        return this.isAreaLoaded(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    @Deprecated
    default public boolean isAreaLoaded(int minX, int minY, int minZ, int maxX, int i, int j) {
        if (i < 0 || minY >= 256) {
            return false;
        }
        minZ >>= 4;
        maxX >>= 4;
        j >>= 4;
        for (int k = minX >>= 4; k <= maxX; ++k) {
            for (int l = minZ; l <= j; ++l) {
                if (this.isChunkLoaded(k, l)) continue;
                return false;
            }
        }
        return true;
    }

    public Dimension getDimension();
}

