/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Block
implements ItemConvertible {
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final IdList<BlockState> STATE_IDS = new IdList();
    private static final Direction[] FACINGS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    private static final LoadingCache<VoxelShape, Boolean> FULL_CUBE_SHAPE_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build((CacheLoader)new CacheLoader<VoxelShape, Boolean>(){

        public Boolean load(VoxelShape voxelShape) {
            return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), voxelShape, BooleanBiFunction.NOT_SAME);
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((VoxelShape)object);
        }
    });
    private static final VoxelShape SOLID_MEDIUM_SQUARE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);
    private static final VoxelShape SOLID_SMALL_SQUARE_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
    protected final int lightLevel;
    protected final float hardness;
    protected final float resistance;
    protected final boolean randomTicks;
    protected final BlockSoundGroup soundGroup;
    protected final Material material;
    protected final MaterialColor materialColor;
    private final float slipperiness;
    protected final StateManager<Block, BlockState> stateManager;
    private BlockState defaultState;
    protected final boolean collidable;
    private final boolean dynamicBounds;
    @Nullable
    private Identifier dropTableId;
    @Nullable
    private String translationKey;
    @Nullable
    private Item cachedItem;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<NeighborGroup>(200){

            protected void rehash(int i) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });

    public static int getRawIdFromState(@Nullable BlockState state) {
        if (state == null) {
            return 0;
        }
        int i = STATE_IDS.getId(state);
        return i == -1 ? 0 : i;
    }

    public static BlockState getStateFromRawId(int stateId) {
        BlockState blockState = STATE_IDS.get(stateId);
        return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
    }

    public static Block getBlockFromItem(@Nullable Item item) {
        if (item instanceof BlockItem) {
            return ((BlockItem)item).getBlock();
        }
        return Blocks.AIR;
    }

    public static BlockState pushEntitiesUpBeforeBlockChange(BlockState from, BlockState to, World world, BlockPos pos) {
        VoxelShape voxelShape = VoxelShapes.combine(from.getCollisionShape(world, pos), to.getCollisionShape(world, pos), BooleanBiFunction.ONLY_SECOND).offset(pos.getX(), pos.getY(), pos.getZ());
        List<Entity> list = world.getEntities(null, voxelShape.getBoundingBox());
        for (Entity entity : list) {
            double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entity.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(voxelShape), -1.0);
            entity.requestTeleport(entity.x, entity.y + 1.0 + d, entity.z);
        }
        return to;
    }

    public static VoxelShape createCuboidShape(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        return VoxelShapes.cuboid(xMin / 16.0, yMin / 16.0, zMin / 16.0, xMax / 16.0, yMax / 16.0, zMax / 16.0);
    }

    @Deprecated
    public boolean allowsSpawning(BlockState state, BlockView view, BlockPos pos, EntityType<?> type) {
        return state.isSideSolidFullSquare(view, pos, Direction.UP) && this.lightLevel < 14;
    }

    @Deprecated
    public boolean isAir(BlockState state) {
        return false;
    }

    @Deprecated
    public int getLuminance(BlockState state) {
        return this.lightLevel;
    }

    @Deprecated
    public Material getMaterial(BlockState state) {
        return this.material;
    }

    @Deprecated
    public MaterialColor getMapColor(BlockState state, BlockView view, BlockPos pos) {
        return this.materialColor;
    }

    @Deprecated
    public void updateNeighborStates(BlockState state, IWorld world, BlockPos pos, int flags) {
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (Direction direction : FACINGS) {
                pooledMutable.set(pos).setOffset(direction);
                BlockState blockState = world.getBlockState(pooledMutable);
                BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), state, world, pooledMutable, pos);
                Block.replaceBlock(blockState, blockState2, world, pooledMutable, flags);
            }
        }
    }

    public boolean matches(Tag<Block> tag) {
        return tag.contains(this);
    }

    public static BlockState getRenderingState(BlockState state, IWorld world, BlockPos pos) {
        BlockState blockState = state;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : FACINGS) {
            mutable.set(pos).setOffset(direction);
            blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(mutable), world, pos, mutable);
        }
        return blockState;
    }

    public static void replaceBlock(BlockState state, BlockState newState, IWorld world, BlockPos pos, int flags) {
        if (newState != state) {
            if (newState.isAir()) {
                if (!world.isClient()) {
                    world.breakBlock(pos, (flags & 0x20) == 0);
                }
            } else {
                world.setBlockState(pos, newState, flags & 0xFFFFFFDF);
            }
        }
    }

    @Deprecated
    public void method_9517(BlockState state, IWorld world, BlockPos pos, int flags) {
    }

    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    @Deprecated
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    @Deprecated
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    public Block(Settings settings) {
        StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<Block, BlockState>(this);
        this.appendProperties(builder);
        this.material = settings.material;
        this.materialColor = settings.materialColor;
        this.collidable = settings.collidable;
        this.soundGroup = settings.soundGroup;
        this.lightLevel = settings.luminance;
        this.resistance = settings.resistance;
        this.hardness = settings.hardness;
        this.randomTicks = settings.randomTicks;
        this.slipperiness = settings.slipperiness;
        this.dynamicBounds = settings.dynamicBounds;
        this.dropTableId = settings.dropTableId;
        this.stateManager = builder.build(BlockState::new);
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    public static boolean cannotConnect(Block block) {
        return block instanceof LeavesBlock || block == Blocks.BARRIER || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.MELON || block == Blocks.PUMPKIN;
    }

    @Deprecated
    public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
        return state.getMaterial().blocksLight() && state.method_21743(view, pos) && !state.emitsRedstonePower();
    }

    @Deprecated
    public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
        return this.material.blocksMovement() && state.method_21743(view, pos);
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState state) {
        return false;
    }

    @Deprecated
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        switch (env) {
            case LAND: {
                return !world.method_21743(view, pos);
            }
            case WATER: {
                return view.getFluidState(pos).matches(FluidTags.WATER);
            }
            case AIR: {
                return !world.method_21743(view, pos);
            }
        }
        return false;
    }

    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Deprecated
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        return this.material.isReplaceable() && (ctx.getStack().isEmpty() || ctx.getStack().getItem() != this.asItem());
    }

    @Deprecated
    public float getHardness(BlockState state, BlockView world, BlockPos pos) {
        return this.hardness;
    }

    public boolean hasRandomTicks(BlockState state) {
        return this.randomTicks;
    }

    public boolean hasBlockEntity() {
        return this instanceof BlockEntityProvider;
    }

    @Deprecated
    public boolean shouldPostProcess(BlockState state, BlockView view, BlockPos pos) {
        return false;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public int getBlockBrightness(BlockState state, BlockRenderView view, BlockPos pos) {
        return view.getLightmapIndex(pos, state.getLuminance());
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean shouldDrawSide(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        BlockPos blockPos = pos.offset(facing);
        BlockState blockState = view.getBlockState(blockPos);
        if (state.isSideInvisible(blockState, facing)) {
            return false;
        }
        if (blockState.isOpaque()) {
            NeighborGroup neighborGroup = new NeighborGroup(state, blockState, facing);
            Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst((Object)neighborGroup);
            if (b != 127) {
                return b != 0;
            }
            VoxelShape voxelShape = state.getCullingFace(view, pos, facing);
            VoxelShape voxelShape2 = blockState.getCullingFace(view, blockPos, facing.getOpposite());
            boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
            if (object2ByteLinkedOpenHashMap.size() == 200) {
                object2ByteLinkedOpenHashMap.removeLastByte();
            }
            object2ByteLinkedOpenHashMap.putAndMoveToFirst((Object)neighborGroup, (byte)(bl ? 1 : 0));
            return bl;
        }
        return true;
    }

    @Deprecated
    public boolean isOpaque(BlockState state) {
        return this.collidable && this.getRenderLayer() == RenderLayer.SOLID;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
        return false;
    }

    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.fullCube();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return this.collidable ? state.getOutlineShape(view, pos) : VoxelShapes.empty();
    }

    @Deprecated
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return state.getOutlineShape(view, pos);
    }

    @Deprecated
    public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public static boolean topCoversMediumSquare(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return !blockState.matches(BlockTags.LEAVES) && !VoxelShapes.matchesAnywhere(blockState.getCollisionShape(world, pos).getFace(Direction.UP), SOLID_MEDIUM_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND);
    }

    public static boolean isSolidSmallSquare(CollisionView world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return !blockState.matches(BlockTags.LEAVES) && !VoxelShapes.matchesAnywhere(blockState.getCollisionShape(world, pos).getFace(side), SOLID_SMALL_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND);
    }

    public static boolean isSideSolidFullSquare(BlockState state, BlockView world, BlockPos pos, Direction side) {
        return !state.matches(BlockTags.LEAVES) && Block.isFaceFullSquare(state.getCollisionShape(world, pos), side);
    }

    public static boolean isFaceFullSquare(VoxelShape shape, Direction side) {
        VoxelShape voxelShape = shape.getFace(side);
        return Block.isShapeFullCube(voxelShape);
    }

    public static boolean isShapeFullCube(VoxelShape shape) {
        return (Boolean)FULL_CUBE_SHAPE_CACHE.getUnchecked((Object)shape);
    }

    @Deprecated
    public final boolean isFullOpaque(BlockState state, BlockView view, BlockPos pos) {
        if (state.isOpaque()) {
            return Block.isShapeFullCube(state.getCullingShape(view, pos));
        }
        return false;
    }

    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
        return !Block.isShapeFullCube(state.getOutlineShape(view, pos)) && state.getFluidState().isEmpty();
    }

    @Deprecated
    public int getOpacity(BlockState state, BlockView view, BlockPos pos) {
        if (state.isFullOpaque(view, pos)) {
            return view.getMaxLightLevel();
        }
        return state.isTranslucent(view, pos) ? 0 : 1;
    }

    @Deprecated
    public boolean hasSidedTransparency(BlockState state) {
        return false;
    }

    @Deprecated
    public void onRandomTick(BlockState state, World world, BlockPos pos, Random random) {
        this.onScheduledTick(state, world, pos, random);
    }

    @Deprecated
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
    }

    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    }

    public void onBroken(IWorld world, BlockPos pos, BlockState state) {
    }

    @Deprecated
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        DebugInfoSender.sendNeighborUpdate(world, pos);
    }

    public int getTickRate(CollisionView world) {
        return 10;
    }

    @Nullable
    @Deprecated
    public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    @Deprecated
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
    }

    @Deprecated
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (this.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            world.removeBlockEntity(pos);
        }
    }

    @Deprecated
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.isUsingEffectiveTool(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    @Deprecated
    public void onStacksDropped(BlockState state, World world, BlockPos pos, ItemStack stack) {
    }

    public Identifier getDropTableId() {
        if (this.dropTableId == null) {
            Identifier identifier = Registry.BLOCK.getId(this);
            this.dropTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
        }
        return this.dropTableId;
    }

    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        Identifier identifier = this.getDropTableId();
        if (identifier == LootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext lootContext = builder.put(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContext.getWorld();
        LootTable lootTable = serverWorld.getServer().getLootManager().getSupplier(identifier);
        return lootTable.getDrops(lootContext);
    }

    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity) {
        LootContext.Builder builder = new LootContext.Builder(world).setRandom(world.random).put(LootContextParameters.POSITION, pos).put(LootContextParameters.TOOL, ItemStack.EMPTY).putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity);
        return state.getDroppedStacks(builder);
    }

    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack) {
        LootContext.Builder builder = new LootContext.Builder(world).setRandom(world.random).put(LootContextParameters.POSITION, pos).put(LootContextParameters.TOOL, stack).put(LootContextParameters.THIS_ENTITY, entity).putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity);
        return state.getDroppedStacks(builder);
    }

    public static void dropStacks(BlockState state, LootContext.Builder builder) {
        ServerWorld serverWorld = builder.getWorld();
        BlockPos blockPos = builder.get(LootContextParameters.POSITION);
        state.getDroppedStacks(builder).forEach(itemStack -> Block.dropStack(serverWorld, blockPos, itemStack));
        state.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY);
    }

    public static void dropStacks(BlockState state, World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld)world, pos, null).forEach(itemStack -> Block.dropStack(world, pos, itemStack));
        }
        state.onStacksDropped(world, pos, ItemStack.EMPTY);
    }

    public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity) {
        if (world instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld)world, pos, blockEntity).forEach(itemStack -> Block.dropStack(world, pos, itemStack));
        }
        state.onStacksDropped(world, pos, ItemStack.EMPTY);
    }

    public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack) {
        if (world instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, stack).forEach(itemStack -> Block.dropStack(world, pos, itemStack));
        }
        state.onStacksDropped(world, pos, stack);
    }

    public static void dropStack(World world, BlockPos pos, ItemStack stack) {
        if (world.isClient || stack.isEmpty() || !world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            return;
        }
        float f = 0.5f;
        double d = (double)(world.random.nextFloat() * 0.5f) + 0.25;
        double e = (double)(world.random.nextFloat() * 0.5f) + 0.25;
        double g = (double)(world.random.nextFloat() * 0.5f) + 0.25;
        ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    protected void dropExperience(World world, BlockPos pos, int size) {
        if (!world.isClient && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            while (size > 0) {
                int i = ExperienceOrbEntity.roundToOrbSize(size);
                size -= i;
                world.spawnEntity(new ExperienceOrbEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, i));
            }
        }
    }

    public float getBlastResistance() {
        return this.resistance;
    }

    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
    }

    public RenderLayer getRenderLayer() {
        return RenderLayer.SOLID;
    }

    @Deprecated
    public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
        return true;
    }

    @Deprecated
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return false;
    }

    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    @Deprecated
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    }

    @Deprecated
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return 0;
    }

    @Deprecated
    public boolean emitsRedstonePower(BlockState state) {
        return false;
    }

    @Deprecated
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    }

    @Deprecated
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return 0;
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005f);
        Block.dropStacks(state, world, pos, blockEntity, player, stack);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    }

    public boolean canMobSpawnInside() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return new TranslatableText(this.getTranslationKey(), new Object[0]);
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("block", Registry.BLOCK.getId(this));
        }
        return this.translationKey;
    }

    @Deprecated
    public boolean onBlockAction(BlockState state, World world, BlockPos pos, int type, int data) {
        return false;
    }

    @Deprecated
    public PistonBehavior getPistonBehavior(BlockState state) {
        return this.material.getPistonBehavior();
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return state.method_21743(view, pos) ? 0.2f : 1.0f;
    }

    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        entity.handleFallDamage(distance, 1.0f);
    }

    public void onEntityLand(BlockView world, Entity entity) {
        entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
        list.add(new ItemStack(this));
    }

    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }

    public float getSlipperiness() {
        return this.slipperiness;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos);
    }

    public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity entity) {
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.playLevelEvent(player, 2001, pos, Block.getRawIdFromState(state));
    }

    public void rainTick(World world, BlockPos pos) {
    }

    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return true;
    }

    @Deprecated
    public boolean hasComparatorOutput(BlockState state) {
        return false;
    }

    @Deprecated
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 0;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    }

    public StateManager<Block, BlockState> getStateManager() {
        return this.stateManager;
    }

    protected final void setDefaultState(BlockState state) {
        this.defaultState = state;
    }

    public final BlockState getDefaultState() {
        return this.defaultState;
    }

    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }

    @Deprecated
    public Vec3d getOffsetPos(BlockState state, BlockView view, BlockPos blockPos) {
        OffsetType offsetType = this.getOffsetType();
        if (offsetType == OffsetType.NONE) {
            return Vec3d.ZERO;
        }
        long l = MathHelper.hashCode(blockPos.getX(), 0, blockPos.getZ());
        return new Vec3d(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, offsetType == OffsetType.XYZ ? ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * 0.2 : 0.0, ((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5);
    }

    public BlockSoundGroup getSoundGroup(BlockState state) {
        return this.soundGroup;
    }

    @Override
    public Item asItem() {
        if (this.cachedItem == null) {
            this.cachedItem = Item.fromBlock(this);
        }
        return this.cachedItem;
    }

    public boolean hasDynamicBounds() {
        return this.dynamicBounds;
    }

    public String toString() {
        return "Block{" + Registry.BLOCK.getId(this) + "}";
    }

    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, @Nullable BlockView view, List<Text> tooltip, TooltipContext options) {
    }

    public static boolean isNaturalStone(Block block) {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
    }

    public static boolean isNaturalDirt(Block block) {
        return block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
    }

    public static enum OffsetType {
        NONE,
        XZ,
        XYZ;

    }

    public static class Settings {
        private Material material;
        private MaterialColor materialColor;
        private boolean collidable = true;
        private BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
        private int luminance;
        private float resistance;
        private float hardness;
        private boolean randomTicks;
        private float slipperiness = 0.6f;
        private Identifier dropTableId;
        private boolean dynamicBounds;

        private Settings(Material material, MaterialColor materialColor) {
            this.material = material;
            this.materialColor = materialColor;
        }

        public static Settings of(Material material) {
            return Settings.of(material, material.getColor());
        }

        public static Settings of(Material material, DyeColor color) {
            return Settings.of(material, color.getMaterialColor());
        }

        public static Settings of(Material material, MaterialColor color) {
            return new Settings(material, color);
        }

        public static Settings copy(Block source) {
            Settings settings = new Settings(source.material, source.materialColor);
            settings.material = source.material;
            settings.hardness = source.hardness;
            settings.resistance = source.resistance;
            settings.collidable = source.collidable;
            settings.randomTicks = source.randomTicks;
            settings.luminance = source.lightLevel;
            settings.materialColor = source.materialColor;
            settings.soundGroup = source.soundGroup;
            settings.slipperiness = source.getSlipperiness();
            settings.dynamicBounds = source.dynamicBounds;
            return settings;
        }

        public Settings noCollision() {
            this.collidable = false;
            return this;
        }

        public Settings slipperiness(float slipperiness) {
            this.slipperiness = slipperiness;
            return this;
        }

        protected Settings sounds(BlockSoundGroup soundGroup) {
            this.soundGroup = soundGroup;
            return this;
        }

        protected Settings lightLevel(int luminance) {
            this.luminance = luminance;
            return this;
        }

        public Settings strength(float hardness, float resistance) {
            this.hardness = hardness;
            this.resistance = Math.max(0.0f, resistance);
            return this;
        }

        protected Settings breakInstantly() {
            return this.strength(0.0f);
        }

        protected Settings strength(float strength) {
            this.strength(strength, strength);
            return this;
        }

        protected Settings ticksRandomly() {
            this.randomTicks = true;
            return this;
        }

        protected Settings hasDynamicBounds() {
            this.dynamicBounds = true;
            return this;
        }

        protected Settings dropsNothing() {
            this.dropTableId = LootTables.EMPTY;
            return this;
        }

        public Settings dropsLike(Block source) {
            this.dropTableId = source.getDropTableId();
            return this;
        }
    }

    public static final class NeighborGroup {
        private final BlockState self;
        private final BlockState other;
        private final Direction facing;

        public NeighborGroup(BlockState self, BlockState other, Direction facing) {
            this.self = self;
            this.other = other;
            this.facing = facing;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof NeighborGroup)) {
                return false;
            }
            NeighborGroup neighborGroup = (NeighborGroup)o;
            return this.self == neighborGroup.self && this.other == neighborGroup.other && this.facing == neighborGroup.facing;
        }

        public int hashCode() {
            int i = this.self.hashCode();
            i = 31 * i + this.other.hashCode();
            i = 31 * i + this.facing.hashCode();
            return i;
        }
    }
}
