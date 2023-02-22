/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class ChunkSectionPos
extends Vec3i {
    private ChunkSectionPos(int x, int y, int z) {
        super(x, y, z);
    }

    public static ChunkSectionPos from(int x, int y, int z) {
        return new ChunkSectionPos(x, y, z);
    }

    public static ChunkSectionPos from(BlockPos pos) {
        return new ChunkSectionPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getY()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public static ChunkSectionPos from(ChunkPos chunkPos, int y) {
        return new ChunkSectionPos(chunkPos.x, y, chunkPos.z);
    }

    public static ChunkSectionPos from(Entity entity) {
        return new ChunkSectionPos(ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getX())), ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getY())), ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getZ())));
    }

    public static ChunkSectionPos from(long packed) {
        return new ChunkSectionPos(ChunkSectionPos.unpackX(packed), ChunkSectionPos.unpackY(packed), ChunkSectionPos.unpackZ(packed));
    }

    public static long offset(long packed, Direction direction) {
        return ChunkSectionPos.offset(packed, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long offset(long packed, int x, int y, int z) {
        return ChunkSectionPos.asLong(ChunkSectionPos.unpackX(packed) + x, ChunkSectionPos.unpackY(packed) + y, ChunkSectionPos.unpackZ(packed) + z);
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }

    public static int getLocalCoord(int coord) {
        return coord & 0xF;
    }

    public static short packLocal(BlockPos pos) {
        int i = ChunkSectionPos.getLocalCoord(pos.getX());
        int j = ChunkSectionPos.getLocalCoord(pos.getY());
        int k = ChunkSectionPos.getLocalCoord(pos.getZ());
        return (short)(i << 8 | k << 4 | j << 0);
    }

    public static int unpackLocalX(short packedLocalPos) {
        return packedLocalPos >>> 8 & 0xF;
    }

    public static int unpackLocalY(short packedLocalPos) {
        return packedLocalPos >>> 0 & 0xF;
    }

    public static int unpackLocalZ(short packedLocalPos) {
        return packedLocalPos >>> 4 & 0xF;
    }

    public int unpackBlockX(short packedLocalPos) {
        return this.getMinX() + ChunkSectionPos.unpackLocalX(packedLocalPos);
    }

    public int unpackBlockY(short packedLocalPos) {
        return this.getMinY() + ChunkSectionPos.unpackLocalY(packedLocalPos);
    }

    public int unpackBlockZ(short packedLocalPos) {
        return this.getMinZ() + ChunkSectionPos.unpackLocalZ(packedLocalPos);
    }

    public BlockPos unpackBlockPos(short packedLocalPos) {
        return new BlockPos(this.unpackBlockX(packedLocalPos), this.unpackBlockY(packedLocalPos), this.unpackBlockZ(packedLocalPos));
    }

    public static int getBlockCoord(int sectionCoord) {
        return sectionCoord << 4;
    }

    public static int unpackX(long packed) {
        return (int)(packed << 0 >> 42);
    }

    public static int unpackY(long packed) {
        return (int)(packed << 44 >> 44);
    }

    public static int unpackZ(long packed) {
        return (int)(packed << 22 >> 42);
    }

    public int getSectionX() {
        return this.getX();
    }

    public int getSectionY() {
        return this.getY();
    }

    public int getSectionZ() {
        return this.getZ();
    }

    public int getMinX() {
        return this.getSectionX() << 4;
    }

    public int getMinY() {
        return this.getSectionY() << 4;
    }

    public int getMinZ() {
        return this.getSectionZ() << 4;
    }

    public int getMaxX() {
        return (this.getSectionX() << 4) + 15;
    }

    public int getMaxY() {
        return (this.getSectionY() << 4) + 15;
    }

    public int getMaxZ() {
        return (this.getSectionZ() << 4) + 15;
    }

    public static long fromBlockPos(long blockPos) {
        return ChunkSectionPos.asLong(ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getSectionCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(blockPos)));
    }

    public static long withZeroY(long pos) {
        return pos & 0xFFFFFFFFFFF00000L;
    }

    public BlockPos getMinPos() {
        return new BlockPos(ChunkSectionPos.getBlockCoord(this.getSectionX()), ChunkSectionPos.getBlockCoord(this.getSectionY()), ChunkSectionPos.getBlockCoord(this.getSectionZ()));
    }

    public BlockPos getCenterPos() {
        int i = 8;
        return this.getMinPos().add(8, 8, 8);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(this.getSectionX(), this.getSectionZ());
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long)x & 0x3FFFFFL) << 42;
        l |= ((long)y & 0xFFFFFL) << 0;
        return l |= ((long)z & 0x3FFFFFL) << 20;
    }

    public long asLong() {
        return ChunkSectionPos.asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
    }

    public Stream<BlockPos> streamBlocks() {
        return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public static Stream<ChunkSectionPos> stream(ChunkSectionPos center, int radius) {
        int i = center.getSectionX();
        int j = center.getSectionY();
        int k = center.getSectionZ();
        return ChunkSectionPos.stream(i - radius, j - radius, k - radius, i + radius, j + radius, k + radius);
    }

    public static Stream<ChunkSectionPos> stream(ChunkPos center, int radius) {
        int i = center.x;
        int j = center.z;
        return ChunkSectionPos.stream(i - radius, 0, j - radius, i + radius, 15, j + radius);
    }

    public static Stream<ChunkSectionPos> stream(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkSectionPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64){
            final CuboidBlockIterator iterator;
            {
                super(l, i);
                this.iterator = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);
            }

            @Override
            public boolean tryAdvance(Consumer<? super ChunkSectionPos> consumer) {
                if (this.iterator.step()) {
                    consumer.accept(new ChunkSectionPos(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }
}

