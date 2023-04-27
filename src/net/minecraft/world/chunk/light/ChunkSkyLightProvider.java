/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.world.chunk.light;

import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_8528;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.chunk.light.SkyLightStorage;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public final class ChunkSkyLightProvider
extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
    private static final long field_44743 = ChunkLightProvider.class_8531.packWithAllDirectionsSet(15);
    private static final long field_44744 = ChunkLightProvider.class_8531.packWithOneDirectionCleared(15, Direction.UP);
    private static final long field_44745 = ChunkLightProvider.class_8531.method_51574(15, false, Direction.UP);
    private final BlockPos.Mutable field_44746 = new BlockPos.Mutable();
    private final class_8528 field_44747;

    public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
        this(chunkProvider, new SkyLightStorage(chunkProvider));
    }

    @VisibleForTesting
    protected ChunkSkyLightProvider(ChunkProvider chunkProvider, SkyLightStorage lightStorage) {
        super(chunkProvider, lightStorage);
        this.field_44747 = new class_8528(chunkProvider.getWorld());
    }

    private static boolean method_51584(int i) {
        return i == 15;
    }

    private int method_51585(int x, int z, int i) {
        class_8528 lv = this.method_51589(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
        if (lv == null) {
            return i;
        }
        return lv.method_51535(ChunkSectionPos.getLocalCoord(x), ChunkSectionPos.getLocalCoord(z));
    }

    @Nullable
    private class_8528 method_51589(int chunkX, int chunkZ) {
        LightSourceView lightSourceView = this.chunkProvider.getChunk(chunkX, chunkZ);
        return lightSourceView != null ? lightSourceView.getLightSourcesStream() : null;
    }

    @Override
    protected void method_51529(long blockPos) {
        boolean bl;
        int m;
        int i = BlockPos.unpackLongX(blockPos);
        int j = BlockPos.unpackLongY(blockPos);
        int k = BlockPos.unpackLongZ(blockPos);
        long l = ChunkSectionPos.fromBlockPos(blockPos);
        int n = m = ((SkyLightStorage)this.lightStorage).isSectionInEnabledColumn(l) ? this.method_51585(i, k, Integer.MAX_VALUE) : Integer.MAX_VALUE;
        if (m != Integer.MAX_VALUE) {
            this.method_51590(i, k, m);
        }
        if (!((SkyLightStorage)this.lightStorage).hasSection(l)) {
            return;
        }
        boolean bl2 = bl = j >= m;
        if (bl) {
            this.method_51565(blockPos, field_44744);
            this.method_51566(blockPos, field_44745);
        } else {
            int n2 = ((SkyLightStorage)this.lightStorage).get(blockPos);
            if (n2 > 0) {
                ((SkyLightStorage)this.lightStorage).set(blockPos, 0);
                this.method_51565(blockPos, ChunkLightProvider.class_8531.packWithAllDirectionsSet(n2));
            } else {
                this.method_51565(blockPos, field_44731);
            }
        }
    }

    private void method_51590(int i, int j, int k) {
        int l = ChunkSectionPos.getBlockCoord(((SkyLightStorage)this.lightStorage).getMinSectionY());
        this.method_51586(i, j, k, l);
        this.method_51591(i, j, k, l);
    }

    private void method_51586(int x, int z, int i, int j) {
        if (i <= j) {
            return;
        }
        int k = ChunkSectionPos.getSectionCoord(x);
        int l = ChunkSectionPos.getSectionCoord(z);
        int m = i - 1;
        int n = ChunkSectionPos.getSectionCoord(m);
        while (((SkyLightStorage)this.lightStorage).isAboveMinHeight(n)) {
            if (((SkyLightStorage)this.lightStorage).hasSection(ChunkSectionPos.asLong(k, n, l))) {
                int o = ChunkSectionPos.getBlockCoord(n);
                int p = o + 15;
                for (int q = Math.min(p, m); q >= o; --q) {
                    long r = BlockPos.asLong(x, q, z);
                    if (!ChunkSkyLightProvider.method_51584(((SkyLightStorage)this.lightStorage).get(r))) {
                        return;
                    }
                    ((SkyLightStorage)this.lightStorage).set(r, 0);
                    this.method_51565(r, q == i - 1 ? field_44743 : field_44744);
                }
            }
            --n;
        }
    }

    private void method_51591(int i, int j, int k, int l) {
        int m = ChunkSectionPos.getSectionCoord(i);
        int n = ChunkSectionPos.getSectionCoord(j);
        int o = Math.max(Math.max(this.method_51585(i - 1, j, Integer.MIN_VALUE), this.method_51585(i + 1, j, Integer.MIN_VALUE)), Math.max(this.method_51585(i, j - 1, Integer.MIN_VALUE), this.method_51585(i, j + 1, Integer.MIN_VALUE)));
        int p = Math.max(k, l);
        long q = ChunkSectionPos.asLong(m, ChunkSectionPos.getSectionCoord(p), n);
        while (!((SkyLightStorage)this.lightStorage).isAtOrAboveTopmostSection(q)) {
            if (((SkyLightStorage)this.lightStorage).hasSection(q)) {
                int r = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(q));
                int s = r + 15;
                for (int t = Math.max(r, p); t <= s; ++t) {
                    long u = BlockPos.asLong(i, t, j);
                    if (ChunkSkyLightProvider.method_51584(((SkyLightStorage)this.lightStorage).get(u))) {
                        return;
                    }
                    ((SkyLightStorage)this.lightStorage).set(u, 15);
                    if (t >= o && t != k) continue;
                    this.method_51566(u, field_44745);
                }
            }
            q = ChunkSectionPos.offset(q, Direction.UP);
        }
    }

    @Override
    protected void method_51531(long blockPos, long l, int i) {
        BlockState blockState = null;
        int j = this.getNumberOfSectionsBelowPos(blockPos);
        for (Direction direction : DIRECTIONS) {
            int k;
            int n;
            long m;
            if (!ChunkLightProvider.class_8531.isDirectionBitSet(l, direction) || !((SkyLightStorage)this.lightStorage).hasSection(ChunkSectionPos.fromBlockPos(m = BlockPos.offset(blockPos, direction))) || (n = i - 1) <= (k = ((SkyLightStorage)this.lightStorage).get(m))) continue;
            this.field_44746.set(m);
            BlockState blockState2 = this.getStateForLighting(this.field_44746);
            int o = i - this.getOpacity(blockState2, this.field_44746);
            if (o <= k) continue;
            if (blockState == null) {
                BlockState blockState3 = blockState = ChunkLightProvider.class_8531.isTrivial(l) ? Blocks.AIR.getDefaultState() : this.getStateForLighting(this.field_44746.set(blockPos));
            }
            if (this.shapesCoverFullCube(blockPos, blockState, m, blockState2, direction)) continue;
            ((SkyLightStorage)this.lightStorage).set(m, o);
            if (o > 1) {
                this.method_51566(m, ChunkLightProvider.class_8531.method_51574(o, ChunkSkyLightProvider.isTrivialForLighting(blockState2), direction.getOpposite()));
            }
            this.method_51587(m, direction, o, true, j);
        }
    }

    @Override
    protected void method_51530(long blockPos, long l) {
        int i = this.getNumberOfSectionsBelowPos(blockPos);
        int j = ChunkLightProvider.class_8531.getLightLevel(l);
        for (Direction direction : DIRECTIONS) {
            int k;
            long m;
            if (!ChunkLightProvider.class_8531.isDirectionBitSet(l, direction) || !((SkyLightStorage)this.lightStorage).hasSection(ChunkSectionPos.fromBlockPos(m = BlockPos.offset(blockPos, direction))) || (k = ((SkyLightStorage)this.lightStorage).get(m)) == 0) continue;
            if (k <= j - 1) {
                ((SkyLightStorage)this.lightStorage).set(m, 0);
                this.method_51565(m, ChunkLightProvider.class_8531.packWithOneDirectionCleared(k, direction.getOpposite()));
                this.method_51587(m, direction, k, false, i);
                continue;
            }
            this.method_51566(m, ChunkLightProvider.class_8531.method_51579(k, false, direction.getOpposite()));
        }
    }

    private int getNumberOfSectionsBelowPos(long blockPos) {
        int i = BlockPos.unpackLongY(blockPos);
        int j = ChunkSectionPos.getLocalCoord(i);
        if (j != 0) {
            return 0;
        }
        int k = BlockPos.unpackLongX(blockPos);
        int l = BlockPos.unpackLongZ(blockPos);
        int m = ChunkSectionPos.getLocalCoord(k);
        int n = ChunkSectionPos.getLocalCoord(l);
        if (m == 0 || m == 15 || n == 0 || n == 15) {
            int o = ChunkSectionPos.getSectionCoord(k);
            int p = ChunkSectionPos.getSectionCoord(i);
            int q = ChunkSectionPos.getSectionCoord(l);
            int r = 0;
            while (!((SkyLightStorage)this.lightStorage).hasSection(ChunkSectionPos.asLong(o, p - r - 1, q)) && ((SkyLightStorage)this.lightStorage).isAboveMinHeight(p - r - 1)) {
                ++r;
            }
            return r;
        }
        return 0;
    }

    private void method_51587(long blockPos, Direction direction, int lightLevel, boolean bl, int i) {
        if (i == 0) {
            return;
        }
        int j = BlockPos.unpackLongX(blockPos);
        int k = BlockPos.unpackLongZ(blockPos);
        if (!ChunkSkyLightProvider.exitsChunkXZ(direction, ChunkSectionPos.getLocalCoord(j), ChunkSectionPos.getLocalCoord(k))) {
            return;
        }
        int l = BlockPos.unpackLongY(blockPos);
        int m = ChunkSectionPos.getSectionCoord(j);
        int n = ChunkSectionPos.getSectionCoord(k);
        int o = ChunkSectionPos.getSectionCoord(l) - 1;
        int p = o - i + 1;
        while (o >= p) {
            if (!((SkyLightStorage)this.lightStorage).hasSection(ChunkSectionPos.asLong(m, o, n))) {
                --o;
                continue;
            }
            int q = ChunkSectionPos.getBlockCoord(o);
            for (int r = 15; r >= 0; --r) {
                long s = BlockPos.asLong(j, q + r, k);
                if (bl) {
                    ((SkyLightStorage)this.lightStorage).set(s, lightLevel);
                    if (lightLevel <= 1) continue;
                    this.method_51566(s, ChunkLightProvider.class_8531.method_51574(lightLevel, true, direction.getOpposite()));
                    continue;
                }
                ((SkyLightStorage)this.lightStorage).set(s, 0);
                this.method_51565(s, ChunkLightProvider.class_8531.packWithOneDirectionCleared(lightLevel, direction.getOpposite()));
            }
            --o;
        }
    }

    private static boolean exitsChunkXZ(Direction direction, int localX, int localZ) {
        return switch (direction) {
            case Direction.NORTH -> {
                if (localZ == 15) {
                    yield true;
                }
                yield false;
            }
            case Direction.SOUTH -> {
                if (localZ == 0) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST -> {
                if (localX == 15) {
                    yield true;
                }
                yield false;
            }
            case Direction.EAST -> {
                if (localX == 0) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @Override
    public void setColumnEnabled(ChunkPos pos, boolean retainData) {
        super.setColumnEnabled(pos, retainData);
        if (retainData) {
            class_8528 lv = Objects.requireNonNullElse(this.method_51589(pos.x, pos.z), this.field_44747);
            int i = ChunkSectionPos.getSectionCoord(lv.method_51533());
            long l = ChunkSectionPos.withZeroY(pos.x, pos.z);
            int j = ((SkyLightStorage)this.lightStorage).getTopSectionForColumn(l);
            int k = Math.max(((SkyLightStorage)this.lightStorage).getMinSectionY(), i);
            for (int m = j - 1; m >= k; --m) {
                ChunkNibbleArray chunkNibbleArray = ((SkyLightStorage)this.lightStorage).method_51547(ChunkSectionPos.asLong(pos.x, m, pos.z));
                if (chunkNibbleArray == null || !chunkNibbleArray.isUninitialized()) continue;
                chunkNibbleArray.method_51527(15);
            }
        }
    }

    @Override
    public void propagateLight(ChunkPos chunkPos) {
        long l = ChunkSectionPos.withZeroY(chunkPos.x, chunkPos.z);
        ((SkyLightStorage)this.lightStorage).setColumnEnabled(l, true);
        class_8528 lv = Objects.requireNonNullElse(this.method_51589(chunkPos.x, chunkPos.z), this.field_44747);
        class_8528 lv2 = Objects.requireNonNullElse(this.method_51589(chunkPos.x, chunkPos.z - 1), this.field_44747);
        class_8528 lv3 = Objects.requireNonNullElse(this.method_51589(chunkPos.x, chunkPos.z + 1), this.field_44747);
        class_8528 lv4 = Objects.requireNonNullElse(this.method_51589(chunkPos.x - 1, chunkPos.z), this.field_44747);
        class_8528 lv5 = Objects.requireNonNullElse(this.method_51589(chunkPos.x + 1, chunkPos.z), this.field_44747);
        int i = ((SkyLightStorage)this.lightStorage).getTopSectionForColumn(l);
        int j = ((SkyLightStorage)this.lightStorage).getMinSectionY();
        int k = ChunkSectionPos.getBlockCoord(chunkPos.x);
        int m = ChunkSectionPos.getBlockCoord(chunkPos.z);
        for (int n = i - 1; n >= j; --n) {
            long o = ChunkSectionPos.asLong(chunkPos.x, n, chunkPos.z);
            ChunkNibbleArray chunkNibbleArray = ((SkyLightStorage)this.lightStorage).method_51547(o);
            if (chunkNibbleArray == null) continue;
            int p = ChunkSectionPos.getBlockCoord(n);
            int q = p + 15;
            boolean bl = false;
            for (int r = 0; r < 16; ++r) {
                for (int s = 0; s < 16; ++s) {
                    int t = lv.method_51535(s, r);
                    if (t > q) continue;
                    int u = r == 0 ? lv2.method_51535(s, 15) : lv.method_51535(s, r - 1);
                    int v = r == 15 ? lv3.method_51535(s, 0) : lv.method_51535(s, r + 1);
                    int w = s == 0 ? lv4.method_51535(15, r) : lv.method_51535(s - 1, r);
                    int x = s == 15 ? lv5.method_51535(0, r) : lv.method_51535(s + 1, r);
                    int y = Math.max(Math.max(u, v), Math.max(w, x));
                    for (int z = q; z >= Math.max(p, t); --z) {
                        chunkNibbleArray.set(s, ChunkSectionPos.getLocalCoord(z), r, 15);
                        if (z != t && z >= y) continue;
                        long aa = BlockPos.asLong(k + s, z, m + r);
                        this.method_51566(aa, ChunkLightProvider.class_8531.method_51578(z == t, z < u, z < v, z < w, z < x));
                    }
                    if (t >= p) continue;
                    bl = true;
                }
            }
            if (!bl) break;
        }
    }
}

