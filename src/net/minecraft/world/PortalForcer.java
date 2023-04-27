/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

public class PortalForcer {
    private static final int field_31810 = 3;
    private static final int field_31811 = 128;
    private static final int field_31812 = 16;
    private static final int field_31813 = 5;
    private static final int field_31814 = 4;
    private static final int field_31815 = 3;
    private static final int field_31816 = -1;
    private static final int field_31817 = 4;
    private static final int field_31818 = -1;
    private static final int field_31819 = 3;
    private static final int field_31820 = -1;
    private static final int field_31821 = 2;
    private static final int field_31822 = -1;
    private final ServerWorld world;

    public PortalForcer(ServerWorld world) {
        this.world = world;
    }

    public Optional<BlockLocating.Rectangle> getPortalRect(BlockPos pos, boolean destIsNether, WorldBorder worldBorder) {
        PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
        int i = destIsNether ? 16 : 128;
        pointOfInterestStorage.preloadChunks(this.world, pos, i);
        Optional<PointOfInterest> optional = pointOfInterestStorage.getInSquare(poiType -> poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL), pos, i, PointOfInterestStorage.OccupationStatus.ANY).filter(poi -> worldBorder.contains(poi.getPos())).sorted(Comparator.comparingDouble(poi -> poi.getPos().getSquaredDistance(pos)).thenComparingInt(poi -> poi.getPos().getY())).filter(poi -> this.world.getBlockState(poi.getPos()).contains(Properties.HORIZONTAL_AXIS)).findFirst();
        return optional.map(poi -> {
            BlockPos blockPos = poi.getPos();
            this.world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(blockPos), 3, blockPos);
            BlockState blockState = this.world.getBlockState(blockPos);
            return BlockLocating.getLargestRectangle(blockPos, blockState.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, pos -> this.world.getBlockState((BlockPos)pos) == blockState);
        });
    }

    public Optional<BlockLocating.Rectangle> createPortal(BlockPos pos, Direction.Axis axis) {
        int m;
        int l;
        int k;
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double d = -1.0;
        BlockPos blockPos = null;
        double e = -1.0;
        BlockPos blockPos2 = null;
        WorldBorder worldBorder = this.world.getWorldBorder();
        int i = Math.min(this.world.getTopY(), this.world.getBottomY() + this.world.getLogicalHeight()) - 1;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (BlockPos.Mutable mutable2 : BlockPos.iterateInSquare(pos, 16, Direction.EAST, Direction.SOUTH)) {
            int j = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
            k = 1;
            if (!worldBorder.contains(mutable2) || !worldBorder.contains(mutable2.move(direction, 1))) continue;
            mutable2.move(direction.getOpposite(), 1);
            for (l = j; l >= this.world.getBottomY(); --l) {
                int n;
                mutable2.setY(l);
                if (!this.isBlockStateValid(mutable2)) continue;
                m = l;
                while (l > this.world.getBottomY() && this.isBlockStateValid(mutable2.move(Direction.DOWN))) {
                    --l;
                }
                if (l + 4 > i || (n = m - l) > 0 && n < 3) continue;
                mutable2.setY(l);
                if (!this.isValidPortalPos(mutable2, mutable, direction, 0)) continue;
                double f = pos.getSquaredDistance(mutable2);
                if (this.isValidPortalPos(mutable2, mutable, direction, -1) && this.isValidPortalPos(mutable2, mutable, direction, 1) && (d == -1.0 || d > f)) {
                    d = f;
                    blockPos = mutable2.toImmutable();
                }
                if (d != -1.0 || e != -1.0 && !(e > f)) continue;
                e = f;
                blockPos2 = mutable2.toImmutable();
            }
        }
        if (d == -1.0 && e != -1.0) {
            blockPos = blockPos2;
            d = e;
        }
        if (d == -1.0) {
            int p = i - 9;
            int o = Math.max(this.world.getBottomY() - -1, 70);
            if (p < o) {
                return Optional.empty();
            }
            blockPos = new BlockPos(pos.getX(), MathHelper.clamp(pos.getY(), o, p), pos.getZ()).toImmutable();
            Direction direction2 = direction.rotateYClockwise();
            if (!worldBorder.contains(blockPos)) {
                return Optional.empty();
            }
            for (k = -1; k < 2; ++k) {
                for (l = 0; l < 2; ++l) {
                    for (m = -1; m < 3; ++m) {
                        BlockState blockState = m < 0 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
                        mutable.set(blockPos, l * direction.getOffsetX() + k * direction2.getOffsetX(), m, l * direction.getOffsetZ() + k * direction2.getOffsetZ());
                        this.world.setBlockState(mutable, blockState);
                    }
                }
            }
        }
        for (int o = -1; o < 3; ++o) {
            for (int p = -1; p < 4; ++p) {
                if (o != -1 && o != 2 && p != -1 && p != 3) continue;
                mutable.set(blockPos, o * direction.getOffsetX(), p, o * direction.getOffsetZ());
                this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
        }
        BlockState blockState2 = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);
        for (int p = 0; p < 2; ++p) {
            for (int j = 0; j < 3; ++j) {
                mutable.set(blockPos, p * direction.getOffsetX(), j, p * direction.getOffsetZ());
                this.world.setBlockState(mutable, blockState2, 18);
            }
        }
        return Optional.of(new BlockLocating.Rectangle(blockPos.toImmutable(), 2, 3));
    }

    private boolean isBlockStateValid(BlockPos.Mutable pos) {
        BlockState blockState = this.world.getBlockState(pos);
        return blockState.isReplaceable() && blockState.getFluidState().isEmpty();
    }

    private boolean isValidPortalPos(BlockPos pos, BlockPos.Mutable temp, Direction portalDirection, int distanceOrthogonalToPortal) {
        Direction direction = portalDirection.rotateYClockwise();
        for (int i = -1; i < 3; ++i) {
            for (int j = -1; j < 4; ++j) {
                temp.set(pos, portalDirection.getOffsetX() * i + direction.getOffsetX() * distanceOrthogonalToPortal, j, portalDirection.getOffsetZ() * i + direction.getOffsetZ() * distanceOrthogonalToPortal);
                if (j < 0 && !this.world.getBlockState(temp).isSolid()) {
                    return false;
                }
                if (j < 0 || this.isBlockStateValid(temp)) continue;
                return false;
            }
        }
        return true;
    }
}

