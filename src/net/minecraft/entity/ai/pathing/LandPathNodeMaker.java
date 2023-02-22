/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class LandPathNodeMaker
extends PathNodeMaker {
    public static final double Y_OFFSET = 0.5;
    protected float waterPathNodeTypeWeight;
    private final Long2ObjectMap<PathNodeType> nodeTypes = new Long2ObjectOpenHashMap();
    private final Object2BooleanMap<Box> collidedBoxes = new Object2BooleanOpenHashMap();

    @Override
    public void init(ChunkCache cachedWorld, MobEntity entity) {
        super.init(cachedWorld, entity);
        this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeType.WATER);
    }

    @Override
    public void clear() {
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
        this.nodeTypes.clear();
        this.collidedBoxes.clear();
        super.clear();
    }

    @Override
    public PathNode getStart() {
        BlockPos blockPos;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = this.entity.getBlockY();
        BlockState blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), (double)i, this.entity.getZ()));
        if (this.entity.canWalkOnFluid(blockState.getFluidState().getFluid())) {
            while (this.entity.canWalkOnFluid(blockState.getFluidState().getFluid())) {
                blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ()));
            }
            --i;
        } else if (this.canSwim() && this.entity.isTouchingWater()) {
            while (blockState.isOf(Blocks.WATER) || blockState.getFluidState() == Fluids.WATER.getStill(false)) {
                blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ()));
            }
            --i;
        } else if (this.entity.isOnGround()) {
            i = MathHelper.floor(this.entity.getY() + 0.5);
        } else {
            blockPos = this.entity.getBlockPos();
            while ((this.cachedWorld.getBlockState(blockPos).isAir() || this.cachedWorld.getBlockState(blockPos).canPathfindThrough(this.cachedWorld, blockPos, NavigationType.LAND)) && blockPos.getY() > this.entity.world.getBottomY()) {
                blockPos = blockPos.down();
            }
            i = blockPos.up().getY();
        }
        blockPos = this.entity.getBlockPos();
        PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos.getX(), i, blockPos.getZ());
        if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0f) {
            Box box = this.entity.getBoundingBox();
            if (this.canPathThrough(mutable.set(box.minX, (double)i, box.minZ)) || this.canPathThrough(mutable.set(box.minX, (double)i, box.maxZ)) || this.canPathThrough(mutable.set(box.maxX, (double)i, box.minZ)) || this.canPathThrough(mutable.set(box.maxX, (double)i, box.maxZ))) {
                PathNode pathNode = this.getNode(mutable);
                pathNode.type = this.getNodeType(this.entity, pathNode.getBlockPos());
                pathNode.penalty = this.entity.getPathfindingPenalty(pathNode.type);
                return pathNode;
            }
        }
        PathNode pathNode2 = this.getNode(blockPos.getX(), i, blockPos.getZ());
        pathNode2.type = this.getNodeType(this.entity, pathNode2.getBlockPos());
        pathNode2.penalty = this.entity.getPathfindingPenalty(pathNode2.type);
        return pathNode2;
    }

    private boolean canPathThrough(BlockPos pos) {
        PathNodeType pathNodeType = this.getNodeType(this.entity, pos);
        return this.entity.getPathfindingPenalty(pathNodeType) >= 0.0f;
    }

    @Override
    public TargetPathNode getNode(double x, double y, double z) {
        return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
    }

    @Override
    public int getSuccessors(PathNode[] successors, PathNode node) {
        PathNode pathNode8;
        PathNode pathNode7;
        PathNode pathNode6;
        PathNode pathNode5;
        PathNode pathNode4;
        PathNode pathNode3;
        PathNode pathNode2;
        double d;
        PathNode pathNode;
        int i = 0;
        int j = 0;
        PathNodeType pathNodeType = this.getNodeType(this.entity, node.x, node.y + 1, node.z);
        PathNodeType pathNodeType2 = this.getNodeType(this.entity, node.x, node.y, node.z);
        if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0f && pathNodeType2 != PathNodeType.STICKY_HONEY) {
            j = MathHelper.floor(Math.max(1.0f, this.entity.stepHeight));
        }
        if (this.isValidAdjacentSuccessor(pathNode = this.getPathNode(node.x, node.y, node.z + 1, j, d = this.method_37003(new BlockPos(node.x, node.y, node.z)), Direction.SOUTH, pathNodeType2), node)) {
            successors[i++] = pathNode;
        }
        if (this.isValidAdjacentSuccessor(pathNode2 = this.getPathNode(node.x - 1, node.y, node.z, j, d, Direction.WEST, pathNodeType2), node)) {
            successors[i++] = pathNode2;
        }
        if (this.isValidAdjacentSuccessor(pathNode3 = this.getPathNode(node.x + 1, node.y, node.z, j, d, Direction.EAST, pathNodeType2), node)) {
            successors[i++] = pathNode3;
        }
        if (this.isValidAdjacentSuccessor(pathNode4 = this.getPathNode(node.x, node.y, node.z - 1, j, d, Direction.NORTH, pathNodeType2), node)) {
            successors[i++] = pathNode4;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode2, pathNode4, pathNode5 = this.getPathNode(node.x - 1, node.y, node.z - 1, j, d, Direction.NORTH, pathNodeType2))) {
            successors[i++] = pathNode5;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode3, pathNode4, pathNode6 = this.getPathNode(node.x + 1, node.y, node.z - 1, j, d, Direction.NORTH, pathNodeType2))) {
            successors[i++] = pathNode6;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode2, pathNode, pathNode7 = this.getPathNode(node.x - 1, node.y, node.z + 1, j, d, Direction.SOUTH, pathNodeType2))) {
            successors[i++] = pathNode7;
        }
        if (this.isValidDiagonalSuccessor(node, pathNode3, pathNode, pathNode8 = this.getPathNode(node.x + 1, node.y, node.z + 1, j, d, Direction.SOUTH, pathNodeType2))) {
            successors[i++] = pathNode8;
        }
        return i;
    }

    protected boolean isValidAdjacentSuccessor(@Nullable PathNode node, PathNode successor1) {
        return node != null && !node.visited && (node.penalty >= 0.0f || successor1.penalty < 0.0f);
    }

    protected boolean isValidDiagonalSuccessor(PathNode xNode, @Nullable PathNode zNode, @Nullable PathNode xDiagNode, @Nullable PathNode zDiagNode) {
        if (zDiagNode == null || xDiagNode == null || zNode == null) {
            return false;
        }
        if (zDiagNode.visited) {
            return false;
        }
        if (xDiagNode.y > xNode.y || zNode.y > xNode.y) {
            return false;
        }
        if (zNode.type == PathNodeType.WALKABLE_DOOR || xDiagNode.type == PathNodeType.WALKABLE_DOOR || zDiagNode.type == PathNodeType.WALKABLE_DOOR) {
            return false;
        }
        boolean bl = xDiagNode.type == PathNodeType.FENCE && zNode.type == PathNodeType.FENCE && (double)this.entity.getWidth() < 0.5;
        return zDiagNode.penalty >= 0.0f && (xDiagNode.y < xNode.y || xDiagNode.penalty >= 0.0f || bl) && (zNode.y < xNode.y || zNode.penalty >= 0.0f || bl);
    }

    private boolean isBlocked(PathNode node) {
        Vec3d vec3d = new Vec3d((double)node.x - this.entity.getX(), (double)node.y - this.entity.getY(), (double)node.z - this.entity.getZ());
        Box box = this.entity.getBoundingBox();
        int i = MathHelper.ceil(vec3d.length() / box.getAverageSideLength());
        vec3d = vec3d.multiply(1.0f / (float)i);
        for (int j = 1; j <= i; ++j) {
            if (!this.checkBoxCollision(box = box.offset(vec3d))) continue;
            return false;
        }
        return true;
    }

    protected double method_37003(BlockPos blockPos) {
        return LandPathNodeMaker.getFeetY(this.cachedWorld, blockPos);
    }

    public static double getFeetY(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        VoxelShape voxelShape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
        return (double)blockPos.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMax(Direction.Axis.Y));
    }

    protected boolean method_37004() {
        return false;
    }

    @Nullable
    protected PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY, Direction direction, PathNodeType nodeType) {
        double h;
        double g;
        Box box;
        PathNode pathNode = null;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double d = this.method_37003(mutable.set(x, y, z));
        if (d - prevFeetY > 1.125) {
            return null;
        }
        PathNodeType pathNodeType = this.getNodeType(this.entity, x, y, z);
        float f = this.entity.getPathfindingPenalty(pathNodeType);
        double e = (double)this.entity.getWidth() / 2.0;
        if (f >= 0.0f) {
            pathNode = this.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
        }
        if (nodeType == PathNodeType.FENCE && pathNode != null && pathNode.penalty >= 0.0f && !this.isBlocked(pathNode)) {
            pathNode = null;
        }
        if (pathNodeType == PathNodeType.WALKABLE || this.method_37004() && pathNodeType == PathNodeType.WATER) {
            return pathNode;
        }
        if ((pathNode == null || pathNode.penalty < 0.0f) && maxYStep > 0 && pathNodeType != PathNodeType.FENCE && pathNodeType != PathNodeType.UNPASSABLE_RAIL && pathNodeType != PathNodeType.TRAPDOOR && pathNodeType != PathNodeType.POWDER_SNOW && (pathNode = this.getPathNode(x, y + 1, z, maxYStep - 1, prevFeetY, direction, nodeType)) != null && (pathNode.type == PathNodeType.OPEN || pathNode.type == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0f && this.checkBoxCollision(box = new Box((g = (double)(x - direction.getOffsetX()) + 0.5) - e, LandPathNodeMaker.getFeetY(this.cachedWorld, mutable.set(g, (double)(y + 1), h = (double)(z - direction.getOffsetZ()) + 0.5)) + 0.001, h - e, g + e, (double)this.entity.getHeight() + LandPathNodeMaker.getFeetY(this.cachedWorld, mutable.set((double)pathNode.x, (double)pathNode.y, (double)pathNode.z)) - 0.002, h + e))) {
            pathNode = null;
        }
        if (!this.method_37004() && pathNodeType == PathNodeType.WATER && !this.canSwim()) {
            if (this.getNodeType(this.entity, x, y - 1, z) != PathNodeType.WATER) {
                return pathNode;
            }
            while (y > this.entity.world.getBottomY()) {
                if ((pathNodeType = this.getNodeType(this.entity, x, --y, z)) == PathNodeType.WATER) {
                    pathNode = this.getNode(x, y, z);
                    pathNode.type = pathNodeType;
                    pathNode.penalty = Math.max(pathNode.penalty, this.entity.getPathfindingPenalty(pathNodeType));
                    continue;
                }
                return pathNode;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            int i = 0;
            int j = y;
            while (pathNodeType == PathNodeType.OPEN) {
                if (--y < this.entity.world.getBottomY()) {
                    PathNode pathNode2 = this.getNode(x, j, z);
                    pathNode2.type = PathNodeType.BLOCKED;
                    pathNode2.penalty = -1.0f;
                    return pathNode2;
                }
                if (i++ >= this.entity.getSafeFallDistance()) {
                    PathNode pathNode2 = this.getNode(x, y, z);
                    pathNode2.type = PathNodeType.BLOCKED;
                    pathNode2.penalty = -1.0f;
                    return pathNode2;
                }
                pathNodeType = this.getNodeType(this.entity, x, y, z);
                f = this.entity.getPathfindingPenalty(pathNodeType);
                if (pathNodeType != PathNodeType.OPEN && f >= 0.0f) {
                    pathNode = this.getNode(x, y, z);
                    pathNode.type = pathNodeType;
                    pathNode.penalty = Math.max(pathNode.penalty, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                PathNode pathNode2 = this.getNode(x, y, z);
                pathNode2.type = PathNodeType.BLOCKED;
                pathNode2.penalty = -1.0f;
                return pathNode2;
            }
        }
        if (pathNodeType == PathNodeType.FENCE) {
            pathNode = this.getNode(x, y, z);
            pathNode.visited = true;
            pathNode.type = pathNodeType;
            pathNode.penalty = pathNodeType.getDefaultPenalty();
        }
        return pathNode;
    }

    private boolean checkBoxCollision(Box box) {
        return (Boolean)this.collidedBoxes.computeIfAbsent((Object)box, box2 -> !this.cachedWorld.isSpaceEmpty(this.entity, box));
    }

    @Override
    public PathNodeType getNodeType(BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        BlockPos blockPos = mob.getBlockPos();
        pathNodeType = this.findNearbyNodeTypes(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        if (enumSet.contains((Object)PathNodeType.UNPASSABLE_RAIL)) {
            return PathNodeType.UNPASSABLE_RAIL;
        }
        PathNodeType pathNodeType2 = PathNodeType.BLOCKED;
        for (PathNodeType pathNodeType3 : enumSet) {
            if (mob.getPathfindingPenalty(pathNodeType3) < 0.0f) {
                return pathNodeType3;
            }
            if (!(mob.getPathfindingPenalty(pathNodeType3) >= mob.getPathfindingPenalty(pathNodeType2))) continue;
            pathNodeType2 = pathNodeType3;
        }
        if (pathNodeType == PathNodeType.OPEN && mob.getPathfindingPenalty(pathNodeType2) == 0.0f && sizeX <= 1) {
            return PathNodeType.OPEN;
        }
        return pathNodeType2;
    }

    public PathNodeType findNearbyNodeTypes(BlockView world, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors, EnumSet<PathNodeType> nearbyTypes, PathNodeType type, BlockPos pos) {
        for (int i = 0; i < sizeX; ++i) {
            for (int j = 0; j < sizeY; ++j) {
                for (int k = 0; k < sizeZ; ++k) {
                    int l = i + x;
                    int m = j + y;
                    int n = k + z;
                    PathNodeType pathNodeType = this.getDefaultNodeType(world, l, m, n);
                    pathNodeType = this.adjustNodeType(world, canOpenDoors, canEnterOpenDoors, pos, pathNodeType);
                    if (i == 0 && j == 0 && k == 0) {
                        type = pathNodeType;
                    }
                    nearbyTypes.add(pathNodeType);
                }
            }
        }
        return type;
    }

    protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType type) {
        if (type == PathNodeType.DOOR_WOOD_CLOSED && canOpenDoors && canEnterOpenDoors) {
            type = PathNodeType.WALKABLE_DOOR;
        }
        if (type == PathNodeType.DOOR_OPEN && !canEnterOpenDoors) {
            type = PathNodeType.BLOCKED;
        }
        if (type == PathNodeType.RAIL && !(world.getBlockState(pos).getBlock() instanceof AbstractRailBlock) && !(world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
            type = PathNodeType.UNPASSABLE_RAIL;
        }
        if (type == PathNodeType.LEAVES) {
            type = PathNodeType.BLOCKED;
        }
        return type;
    }

    private PathNodeType getNodeType(MobEntity entity, BlockPos pos) {
        return this.getNodeType(entity, pos.getX(), pos.getY(), pos.getZ());
    }

    protected PathNodeType getNodeType(MobEntity entity, int x, int y, int z) {
        return (PathNodeType)((Object)this.nodeTypes.computeIfAbsent(BlockPos.asLong(x, y, z), l -> this.getNodeType(this.cachedWorld, x, y, z, entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors())));
    }

    @Override
    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        return LandPathNodeMaker.getLandNodeType(world, new BlockPos.Mutable(x, y, z));
    }

    public static PathNodeType getLandNodeType(BlockView world, BlockPos.Mutable pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        PathNodeType pathNodeType = LandPathNodeMaker.getCommonNodeType(world, pos);
        if (pathNodeType == PathNodeType.OPEN && j >= world.getBottomY() + 1) {
            PathNodeType pathNodeType2 = LandPathNodeMaker.getCommonNodeType(world, pos.set(i, j - 1, k));
            PathNodeType pathNodeType3 = pathNodeType = pathNodeType2 == PathNodeType.WALKABLE || pathNodeType2 == PathNodeType.OPEN || pathNodeType2 == PathNodeType.WATER || pathNodeType2 == PathNodeType.LAVA ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            if (pathNodeType2 == PathNodeType.DAMAGE_FIRE) {
                pathNodeType = PathNodeType.DAMAGE_FIRE;
            }
            if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
                pathNodeType = PathNodeType.DAMAGE_CACTUS;
            }
            if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
                pathNodeType = PathNodeType.DAMAGE_OTHER;
            }
            if (pathNodeType2 == PathNodeType.STICKY_HONEY) {
                pathNodeType = PathNodeType.STICKY_HONEY;
            }
        }
        if (pathNodeType == PathNodeType.WALKABLE) {
            pathNodeType = LandPathNodeMaker.getNodeTypeFromNeighbors(world, pos.set(i, j, k), pathNodeType);
        }
        return pathNodeType;
    }

    public static PathNodeType getNodeTypeFromNeighbors(BlockView world, BlockPos.Mutable pos, PathNodeType nodeType) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        for (int l = -1; l <= 1; ++l) {
            for (int m = -1; m <= 1; ++m) {
                for (int n = -1; n <= 1; ++n) {
                    if (l == 0 && n == 0) continue;
                    pos.set(i + l, j + m, k + n);
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.isOf(Blocks.CACTUS)) {
                        return PathNodeType.DANGER_CACTUS;
                    }
                    if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
                        return PathNodeType.DANGER_OTHER;
                    }
                    if (LandPathNodeMaker.inflictsFireDamage(blockState)) {
                        return PathNodeType.DANGER_FIRE;
                    }
                    if (!world.getFluidState(pos).isIn(FluidTags.WATER)) continue;
                    return PathNodeType.WATER_BORDER;
                }
            }
        }
        return nodeType;
    }

    protected static PathNodeType getCommonNodeType(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        if (blockState.isAir()) {
            return PathNodeType.OPEN;
        }
        if (blockState.isIn(BlockTags.TRAPDOORS) || blockState.isOf(Blocks.LILY_PAD) || blockState.isOf(Blocks.BIG_DRIPLEAF)) {
            return PathNodeType.TRAPDOOR;
        }
        if (blockState.isOf(Blocks.POWDER_SNOW)) {
            return PathNodeType.POWDER_SNOW;
        }
        if (blockState.isOf(Blocks.CACTUS)) {
            return PathNodeType.DAMAGE_CACTUS;
        }
        if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
            return PathNodeType.DAMAGE_OTHER;
        }
        if (blockState.isOf(Blocks.HONEY_BLOCK)) {
            return PathNodeType.STICKY_HONEY;
        }
        if (blockState.isOf(Blocks.COCOA)) {
            return PathNodeType.COCOA;
        }
        FluidState fluidState = world.getFluidState(pos);
        if (fluidState.isIn(FluidTags.LAVA)) {
            return PathNodeType.LAVA;
        }
        if (LandPathNodeMaker.inflictsFireDamage(blockState)) {
            return PathNodeType.DAMAGE_FIRE;
        }
        if (DoorBlock.isWoodenDoor(blockState) && !blockState.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_WOOD_CLOSED;
        }
        if (block instanceof DoorBlock && material == Material.METAL && !blockState.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_IRON_CLOSED;
        }
        if (block instanceof DoorBlock && blockState.get(DoorBlock.OPEN).booleanValue()) {
            return PathNodeType.DOOR_OPEN;
        }
        if (block instanceof AbstractRailBlock) {
            return PathNodeType.RAIL;
        }
        if (block instanceof LeavesBlock) {
            return PathNodeType.LEAVES;
        }
        if (blockState.isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock && !blockState.get(FenceGateBlock.OPEN).booleanValue()) {
            return PathNodeType.FENCE;
        }
        if (!blockState.canPathfindThrough(world, pos, NavigationType.LAND)) {
            return PathNodeType.BLOCKED;
        }
        if (fluidState.isIn(FluidTags.WATER)) {
            return PathNodeType.WATER;
        }
        return PathNodeType.OPEN;
    }

    public static boolean inflictsFireDamage(BlockState state) {
        return state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA) || state.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(state) || state.isOf(Blocks.LAVA_CAULDRON);
    }
}

