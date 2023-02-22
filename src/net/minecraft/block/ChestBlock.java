/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChestBlock
extends BlockWithEntity
implements Waterloggable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
    protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
    protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final PropertyRetriever<Inventory> INVENTORY_RETRIEVER = new PropertyRetriever<Inventory>(){

        @Override
        public Inventory getFromDoubleChest(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
            return new DoubleInventory(chestBlockEntity, chestBlockEntity2);
        }

        @Override
        public Inventory getFromSingleChest(ChestBlockEntity chestBlockEntity) {
            return chestBlockEntity;
        }

        @Override
        public /* synthetic */ Object getFromSingleChest(ChestBlockEntity chest) {
            return this.getFromSingleChest(chest);
        }

        @Override
        public /* synthetic */ Object getFromDoubleChest(ChestBlockEntity rightChest, ChestBlockEntity leftChest) {
            return this.getFromDoubleChest(rightChest, leftChest);
        }
    };
    private static final PropertyRetriever<NameableContainerFactory> NAME_RETRIEVER = new PropertyRetriever<NameableContainerFactory>(){

        @Override
        public NameableContainerFactory getFromDoubleChest(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
            final DoubleInventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
            return new NameableContainerFactory(){

                @Override
                @Nullable
                public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                        chestBlockEntity.checkLootInteraction(playerInventory.player);
                        chestBlockEntity2.checkLootInteraction(playerInventory.player);
                        return GenericContainer.createGeneric9x6(syncId, playerInventory, inventory);
                    }
                    return null;
                }

                @Override
                public Text getDisplayName() {
                    if (chestBlockEntity.hasCustomName()) {
                        return chestBlockEntity.getDisplayName();
                    }
                    if (chestBlockEntity2.hasCustomName()) {
                        return chestBlockEntity2.getDisplayName();
                    }
                    return new TranslatableText("container.chestDouble", new Object[0]);
                }
            };
        }

        @Override
        public NameableContainerFactory getFromSingleChest(ChestBlockEntity chestBlockEntity) {
            return chestBlockEntity;
        }

        @Override
        public /* synthetic */ Object getFromSingleChest(ChestBlockEntity chest) {
            return this.getFromSingleChest(chest);
        }

        @Override
        public /* synthetic */ Object getFromDoubleChest(ChestBlockEntity rightChest, ChestBlockEntity leftChest) {
            return this.getFromDoubleChest(rightChest, leftChest);
        }
    };

    protected ChestBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(CHEST_TYPE, ChestType.SINGLE)).with(WATERLOGGED, false));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (neighborState.getBlock() == this && facing.getAxis().isHorizontal()) {
            ChestType chestType = neighborState.get(CHEST_TYPE);
            if (state.get(CHEST_TYPE) == ChestType.SINGLE && chestType != ChestType.SINGLE && state.get(FACING) == neighborState.get(FACING) && ChestBlock.getFacing(neighborState) == facing.getOpposite()) {
                return (BlockState)state.with(CHEST_TYPE, chestType.getOpposite());
            }
        } else if (ChestBlock.getFacing(state) == facing) {
            return (BlockState)state.with(CHEST_TYPE, ChestType.SINGLE);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(CHEST_TYPE) == ChestType.SINGLE) {
            return SINGLE_SHAPE;
        }
        switch (ChestBlock.getFacing(state)) {
            default: {
                return DOUBLE_NORTH_SHAPE;
            }
            case SOUTH: {
                return DOUBLE_SOUTH_SHAPE;
            }
            case WEST: {
                return DOUBLE_WEST_SHAPE;
            }
            case EAST: 
        }
        return DOUBLE_EAST_SHAPE;
    }

    public static Direction getFacing(BlockState state) {
        Direction direction = state.get(FACING);
        return state.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction3;
        ChestType chestType = ChestType.SINGLE;
        Direction direction = ctx.getPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = ctx.shouldCancelInteraction();
        Direction direction2 = ctx.getSide();
        if (direction2.getAxis().isHorizontal() && bl && (direction3 = this.getNeighborChestDirection(ctx, direction2.getOpposite())) != null && direction3.getAxis() != direction2.getAxis()) {
            direction = direction3;
            ChestType chestType2 = chestType = direction.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
        }
        if (chestType == ChestType.SINGLE && !bl) {
            if (direction == this.getNeighborChestDirection(ctx, direction.rotateYClockwise())) {
                chestType = ChestType.LEFT;
            } else if (direction == this.getNeighborChestDirection(ctx, direction.rotateYCounterclockwise())) {
                chestType = ChestType.RIGHT;
            }
        }
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, direction)).with(CHEST_TYPE, chestType)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Nullable
    private Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
        return blockState.getBlock() == this && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(pos)) instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory)((Object)blockEntity));
            world.updateHorizontalAdjacent(pos, this);
        }
        super.onBlockRemoved(state, world, pos, newState, moved);
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return true;
        }
        NameableContainerFactory nameableContainerFactory = this.createContainerFactory(state, world, pos);
        if (nameableContainerFactory != null) {
            player.openContainer(nameableContainerFactory);
            player.incrementStat(this.getOpenStat());
        }
        return true;
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
    }

    @Nullable
    public static <T> T retrieve(BlockState state, IWorld world, BlockPos pos, boolean allowBlockedChests, PropertyRetriever<T> propertyRetriever) {
        ChestType chestType2;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ChestBlockEntity)) {
            return null;
        }
        if (!allowBlockedChests && ChestBlock.isChestBlocked(world, pos)) {
            return null;
        }
        ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity;
        ChestType chestType = state.get(CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return propertyRetriever.getFromSingleChest(chestBlockEntity);
        }
        BlockPos blockPos = pos.offset(ChestBlock.getFacing(state));
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == state.getBlock() && (chestType2 = blockState.get(CHEST_TYPE)) != ChestType.SINGLE && chestType != chestType2 && blockState.get(FACING) == state.get(FACING)) {
            if (!allowBlockedChests && ChestBlock.isChestBlocked(world, blockPos)) {
                return null;
            }
            BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
            if (blockEntity2 instanceof ChestBlockEntity) {
                ChestBlockEntity chestBlockEntity2 = chestType == ChestType.RIGHT ? chestBlockEntity : (ChestBlockEntity)blockEntity2;
                ChestBlockEntity chestBlockEntity3 = chestType == ChestType.RIGHT ? (ChestBlockEntity)blockEntity2 : chestBlockEntity;
                return propertyRetriever.getFromDoubleChest(chestBlockEntity2, chestBlockEntity3);
            }
        }
        return propertyRetriever.getFromSingleChest(chestBlockEntity);
    }

    @Nullable
    public static Inventory getInventory(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
        return ChestBlock.retrieve(blockState, world, blockPos, bl, INVENTORY_RETRIEVER);
    }

    @Override
    @Nullable
    public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
        return ChestBlock.retrieve(state, world, pos, false, NAME_RETRIEVER);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new ChestBlockEntity();
    }

    private static boolean isChestBlocked(IWorld world, BlockPos pos) {
        return ChestBlock.hasBlockOnTop(world, pos) || ChestBlock.hasOcelotOnTop(world, pos);
    }

    private static boolean hasBlockOnTop(BlockView view, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return view.getBlockState(blockPos).isSimpleFullBlock(view, blockPos);
    }

    private static boolean hasOcelotOnTop(IWorld world, BlockPos pos) {
        List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
        if (!list.isEmpty()) {
            for (CatEntity catEntity : list) {
                if (!catEntity.isSitting()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Container.calculateComparatorOutput(ChestBlock.getInventory(state, world, pos, false));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }

    static interface PropertyRetriever<T> {
        public T getFromDoubleChest(ChestBlockEntity var1, ChestBlockEntity var2);

        public T getFromSingleChest(ChestBlockEntity var1);
    }
}

