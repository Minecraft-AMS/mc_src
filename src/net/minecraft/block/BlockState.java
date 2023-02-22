/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.AbstractState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockState
extends AbstractState<Block, BlockState>
implements State<BlockState> {
    @Nullable
    private ShapeCache shapeCache;
    private final int luminance;
    private final boolean hasSidedTransparency;

    public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap) {
        super(block, propertyMap);
        this.luminance = block.getLuminance(this);
        this.hasSidedTransparency = block.hasSidedTransparency(this);
    }

    public void initShapeCache() {
        if (!this.getBlock().hasDynamicBounds()) {
            this.shapeCache = new ShapeCache(this);
        }
    }

    public Block getBlock() {
        return (Block)this.owner;
    }

    public Material getMaterial() {
        return this.getBlock().getMaterial(this);
    }

    public boolean allowsSpawning(BlockView view, BlockPos pos, EntityType<?> type) {
        return this.getBlock().allowsSpawning(this, view, pos, type);
    }

    public boolean isTranslucent(BlockView view, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.translucent;
        }
        return this.getBlock().isTranslucent(this, view, pos);
    }

    public int getOpacity(BlockView view, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.lightSubtracted;
        }
        return this.getBlock().getOpacity(this, view, pos);
    }

    public VoxelShape getCullingFace(BlockView view, BlockPos pos, Direction facing) {
        if (this.shapeCache != null && this.shapeCache.extrudedFaces != null) {
            return this.shapeCache.extrudedFaces[facing.ordinal()];
        }
        return VoxelShapes.extrudeFace(this.getCullingShape(view, pos), facing);
    }

    public boolean exceedsCube() {
        return this.shapeCache == null || this.shapeCache.exceedsCube;
    }

    public boolean hasSidedTransparency() {
        return this.hasSidedTransparency;
    }

    public int getLuminance() {
        return this.luminance;
    }

    public boolean isAir() {
        return this.getBlock().isAir(this);
    }

    public MaterialColor getTopMaterialColor(BlockView view, BlockPos pos) {
        return this.getBlock().getMapColor(this, view, pos);
    }

    public BlockState rotate(BlockRotation rotation) {
        return this.getBlock().rotate(this, rotation);
    }

    public BlockState mirror(BlockMirror mirror) {
        return this.getBlock().mirror(this, mirror);
    }

    public BlockRenderType getRenderType() {
        return this.getBlock().getRenderType(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasEmissiveLighting() {
        return this.getBlock().hasEmissiveLighting(this);
    }

    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockView view, BlockPos pos) {
        return this.getBlock().getAmbientOcclusionLightLevel(this, view, pos);
    }

    public boolean isSimpleFullBlock(BlockView view, BlockPos pos) {
        return this.getBlock().isSimpleFullBlock(this, view, pos);
    }

    public boolean emitsRedstonePower() {
        return this.getBlock().emitsRedstonePower(this);
    }

    public int getWeakRedstonePower(BlockView view, BlockPos pos, Direction facing) {
        return this.getBlock().getWeakRedstonePower(this, view, pos, facing);
    }

    public boolean hasComparatorOutput() {
        return this.getBlock().hasComparatorOutput(this);
    }

    public int getComparatorOutput(World world, BlockPos pos) {
        return this.getBlock().getComparatorOutput(this, world, pos);
    }

    public float getHardness(BlockView view, BlockPos pos) {
        return this.getBlock().getHardness(this, view, pos);
    }

    public float calcBlockBreakingDelta(PlayerEntity player, BlockView view, BlockPos pos) {
        return this.getBlock().calcBlockBreakingDelta(this, player, view, pos);
    }

    public int getStrongRedstonePower(BlockView view, BlockPos pos, Direction facing) {
        return this.getBlock().getStrongRedstonePower(this, view, pos, facing);
    }

    public PistonBehavior getPistonBehavior() {
        return this.getBlock().getPistonBehavior(this);
    }

    public boolean isFullOpaque(BlockView view, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.fullOpaque;
        }
        return this.getBlock().isFullOpaque(this, view, pos);
    }

    public boolean isOpaque() {
        if (this.shapeCache != null) {
            return this.shapeCache.opaque;
        }
        return this.getBlock().isOpaque(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState neighbor, Direction facing) {
        return this.getBlock().isSideInvisible(this, neighbor, facing);
    }

    public VoxelShape getOutlineShape(BlockView view, BlockPos pos) {
        return this.getOutlineShape(view, pos, EntityContext.absent());
    }

    public VoxelShape getOutlineShape(BlockView view, BlockPos pos, EntityContext context) {
        return this.getBlock().getOutlineShape(this, view, pos, context);
    }

    public VoxelShape getCollisionShape(BlockView view, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.collisionShape;
        }
        return this.getCollisionShape(view, pos, EntityContext.absent());
    }

    public VoxelShape getCollisionShape(BlockView view, BlockPos pos, EntityContext context) {
        return this.getBlock().getCollisionShape(this, view, pos, context);
    }

    public VoxelShape getCullingShape(BlockView view, BlockPos pos) {
        return this.getBlock().getCullingShape(this, view, pos);
    }

    public VoxelShape getRayTraceShape(BlockView view, BlockPos pos) {
        return this.getBlock().getRayTraceShape(this, view, pos);
    }

    public final boolean hasSolidTopSurface(BlockView view, BlockPos pos, Entity entity) {
        return Block.isFaceFullSquare(this.getCollisionShape(view, pos, EntityContext.of(entity)), Direction.UP);
    }

    public Vec3d getOffsetPos(BlockView view, BlockPos pos) {
        return this.getBlock().getOffsetPos(this, view, pos);
    }

    public boolean onBlockAction(World world, BlockPos pos, int type, int data) {
        return this.getBlock().onBlockAction(this, world, pos, type, data);
    }

    public void neighborUpdate(World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean bl) {
        this.getBlock().neighborUpdate(this, world, pos, neighborBlock, neighborPos, bl);
    }

    public void updateNeighborStates(IWorld world, BlockPos pos, int flags) {
        this.getBlock().updateNeighborStates(this, world, pos, flags);
    }

    public void method_11637(IWorld world, BlockPos pos, int flags) {
        this.getBlock().method_9517(this, world, pos, flags);
    }

    public void onBlockAdded(World world, BlockPos pos, BlockState oldState, boolean moved) {
        this.getBlock().onBlockAdded(this, world, pos, oldState, moved);
    }

    public void onBlockRemoved(World world, BlockPos pos, BlockState newState, boolean moved) {
        this.getBlock().onBlockRemoved(this, world, pos, newState, moved);
    }

    public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
        this.getBlock().scheduledTick(this, world, pos, random);
    }

    public void randomTick(ServerWorld world, BlockPos pos, Random random) {
        this.getBlock().randomTick(this, world, pos, random);
    }

    public void onEntityCollision(World world, BlockPos pos, Entity entity) {
        this.getBlock().onEntityCollision(this, world, pos, entity);
    }

    public void onStacksDropped(World world, BlockPos pos, ItemStack stack) {
        this.getBlock().onStacksDropped(this, world, pos, stack);
    }

    public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
        return this.getBlock().getDroppedStacks(this, builder);
    }

    public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return this.getBlock().onUse(this, world, hit.getBlockPos(), player, hand, hit);
    }

    public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
        this.getBlock().onBlockBreakStart(this, world, pos, player);
    }

    public boolean canSuffocate(BlockView view, BlockPos pos) {
        return this.getBlock().canSuffocate(this, view, pos);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasInWallOverlay(BlockView view, BlockPos pos) {
        return this.getBlock().hasInWallOverlay(this, view, pos);
    }

    public BlockState getStateForNeighborUpdate(Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return this.getBlock().getStateForNeighborUpdate(this, facing, neighborState, world, pos, neighborPos);
    }

    public boolean canPlaceAtSide(BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return this.getBlock().canPlaceAtSide(this, view, pos, env);
    }

    public boolean canReplace(ItemPlacementContext ctx) {
        return this.getBlock().canReplace(this, ctx);
    }

    public boolean canBucketPlace(Fluid fluid) {
        return this.getBlock().canBucketPlace(this, fluid);
    }

    public boolean canPlaceAt(WorldView world, BlockPos pos) {
        return this.getBlock().canPlaceAt(this, world, pos);
    }

    public boolean shouldPostProcess(BlockView view, BlockPos pos) {
        return this.getBlock().shouldPostProcess(this, view, pos);
    }

    @Nullable
    public NameableContainerFactory createContainerFactory(World world, BlockPos pos) {
        return this.getBlock().createContainerFactory(this, world, pos);
    }

    public boolean matches(Tag<Block> tag) {
        return this.getBlock().matches(tag);
    }

    public FluidState getFluidState() {
        return this.getBlock().getFluidState(this);
    }

    public boolean hasRandomTicks() {
        return this.getBlock().hasRandomTicks(this);
    }

    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockPos pos) {
        return this.getBlock().getRenderingSeed(this, pos);
    }

    public BlockSoundGroup getSoundGroup() {
        return this.getBlock().getSoundGroup(this);
    }

    public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity projectile) {
        this.getBlock().onProjectileHit(world, state, hitResult, projectile);
    }

    public boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction) {
        if (this.shapeCache != null) {
            return this.shapeCache.solidFullSquare[direction.ordinal()];
        }
        return Block.isSideSolidFullSquare(this, world, pos, direction);
    }

    public boolean isFullCube(BlockView world, BlockPos pos) {
        if (this.shapeCache != null) {
            return this.shapeCache.isFullCube;
        }
        return Block.isShapeFullCube(this.getCollisionShape(world, pos));
    }

    public static <T> Dynamic<T> serialize(DynamicOps<T> ops, BlockState state) {
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
        Object object = immutableMap.isEmpty() ? ops.createMap((Map)ImmutableMap.of((Object)ops.createString("Name"), (Object)ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()))) : ops.createMap((Map)ImmutableMap.of((Object)ops.createString("Name"), (Object)ops.createString(Registry.BLOCK.getId(state.getBlock()).toString()), (Object)ops.createString("Properties"), (Object)ops.createMap(immutableMap.entrySet().stream().map(entry -> Pair.of((Object)ops.createString(((Property)entry.getKey()).getName()), (Object)ops.createString(State.nameValue((Property)entry.getKey(), (Comparable)entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        return new Dynamic(ops, object);
    }

    public static <T> BlockState deserialize(Dynamic<T> dynamic2) {
        Block block = Registry.BLOCK.get(new Identifier(dynamic2.getElement("Name").flatMap(arg_0 -> ((DynamicOps)dynamic2.getOps()).getStringValue(arg_0)).orElse("minecraft:air")));
        Map map = dynamic2.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        BlockState blockState = block.getDefaultState();
        StateManager<Block, BlockState> stateManager = block.getStateManager();
        for (Map.Entry entry : map.entrySet()) {
            String string = (String)entry.getKey();
            Property<?> property = stateManager.getProperty(string);
            if (property == null) continue;
            blockState = State.tryRead(blockState, property, string, dynamic2.toString(), (String)entry.getValue());
        }
        return blockState;
    }

    static final class ShapeCache {
        private static final Direction[] DIRECTIONS = Direction.values();
        private final boolean opaque;
        private final boolean fullOpaque;
        private final boolean translucent;
        private final int lightSubtracted;
        private final VoxelShape[] extrudedFaces;
        private final VoxelShape collisionShape;
        private final boolean exceedsCube;
        private final boolean[] solidFullSquare;
        private final boolean isFullCube;

        private ShapeCache(BlockState state) {
            Block block = state.getBlock();
            this.opaque = block.isOpaque(state);
            this.fullOpaque = block.isFullOpaque(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
            this.translucent = block.isTranslucent(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
            this.lightSubtracted = block.getOpacity(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
            if (!state.isOpaque()) {
                this.extrudedFaces = null;
            } else {
                this.extrudedFaces = new VoxelShape[DIRECTIONS.length];
                VoxelShape voxelShape = block.getCullingShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                Direction[] directionArray = DIRECTIONS;
                int n = directionArray.length;
                for (int i = 0; i < n; ++i) {
                    Direction direction = directionArray[i];
                    this.extrudedFaces[direction.ordinal()] = VoxelShapes.extrudeFace(voxelShape, direction);
                }
            }
            this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, EntityContext.absent());
            this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> this.collisionShape.getMinimum((Direction.Axis)axis) < 0.0 || this.collisionShape.getMaximum((Direction.Axis)axis) > 1.0);
            this.solidFullSquare = new boolean[6];
            for (Direction direction2 : DIRECTIONS) {
                this.solidFullSquare[direction2.ordinal()] = Block.isSideSolidFullSquare(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction2);
            }
            this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
        }
    }
}

