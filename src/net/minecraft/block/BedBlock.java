/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class BedBlock
extends HorizontalFacingBlock
implements BlockEntityProvider {
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
    protected static final VoxelShape LEG_1_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
    protected static final VoxelShape LEG_2_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
    protected static final VoxelShape LEG_3_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
    protected static final VoxelShape LEG_4_SHAPE = Block.createCuboidShape(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_3_SHAPE);
    protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_2_SHAPE, LEG_4_SHAPE);
    protected static final VoxelShape WEST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_2_SHAPE);
    protected static final VoxelShape EAST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_3_SHAPE, LEG_4_SHAPE);
    private final DyeColor color;

    public BedBlock(DyeColor color, Block.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(PART, BedPart.FOOT)).with(OCCUPIED, false));
    }

    @Override
    public MaterialColor getMapColor(BlockState state, BlockView view, BlockPos pos) {
        if (state.get(PART) == BedPart.FOOT) {
            return this.color.getMaterialColor();
        }
        return MaterialColor.WEB;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Direction getDirection(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() instanceof BedBlock ? blockState.get(FACING) : null;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return true;
        }
        if (state.get(PART) != BedPart.HEAD && (state = world.getBlockState(pos = pos.offset(state.get(FACING)))).getBlock() != this) {
            return true;
        }
        if (!world.dimension.canPlayersSleep() || world.getBiome(pos) == Biomes.NETHER) {
            world.removeBlock(pos, false);
            BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
            if (world.getBlockState(blockPos).getBlock() == this) {
                world.removeBlock(blockPos, false);
            }
            world.createExplosion(null, DamageSource.netherBed(), (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
            return true;
        }
        if (state.get(OCCUPIED).booleanValue()) {
            player.addChatMessage(new TranslatableText("block.minecraft.bed.occupied", new Object[0]), true);
            return true;
        }
        player.trySleep(pos).ifLeft(sleepFailureReason -> {
            if (sleepFailureReason != null) {
                player.addChatMessage(sleepFailureReason.toText(), true);
            }
        });
        return true;
    }

    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        super.onLandedUpon(world, pos, entity, distance * 0.5f);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.isSneaking()) {
            super.onEntityLand(world, entity);
        } else {
            Vec3d vec3d = entity.getVelocity();
            if (vec3d.y < 0.0) {
                double d = entity instanceof LivingEntity ? 1.0 : 0.8;
                entity.setVelocity(vec3d.x, -vec3d.y * (double)0.66f * d, vec3d.z);
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing == BedBlock.getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
            if (neighborState.getBlock() == this && neighborState.get(PART) != state.get(PART)) {
                return (BlockState)state.with(OCCUPIED, neighborState.get(OCCUPIED));
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BedPart bedPart = state.get(PART);
        BlockPos blockPos = pos.offset(BedBlock.getDirectionTowardsOtherPart(bedPart, state.get(FACING)));
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == this && blockState.get(PART) != bedPart) {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
            world.playLevelEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
            if (!world.isClient && !player.isCreative()) {
                ItemStack itemStack = player.getMainHandStack();
                BedBlock.dropStacks(state, world, pos, null, player, itemStack);
                BedBlock.dropStacks(blockState, world, blockPos, null, player, itemStack);
            }
            player.incrementStat(Stats.MINED.getOrCreateStat(this));
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        if (ctx.getWorld().getBlockState(blockPos2).canReplace(ctx)) {
            return (BlockState)this.getDefaultState().with(FACING, direction);
        }
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        Direction direction = state.get(FACING);
        Direction direction2 = state.get(PART) == BedPart.HEAD ? direction : direction.getOpposite();
        switch (direction2) {
            case NORTH: {
                return NORTH_SHAPE;
            }
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
        }
        return EAST_SHAPE;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState state) {
        return true;
    }

    public static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, CollisionView world, BlockPos pos, int index) {
        Direction direction = world.getBlockState(pos).get(FACING);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        for (int l = 0; l <= 1; ++l) {
            int m = i - direction.getOffsetX() * l - 1;
            int n = k - direction.getOffsetZ() * l - 1;
            int o = m + 2;
            int p = n + 2;
            for (int q = m; q <= o; ++q) {
                for (int r = n; r <= p; ++r) {
                    BlockPos blockPos = new BlockPos(q, j, r);
                    Optional<Vec3d> optional = BedBlock.canWakeUpAt(type, world, blockPos);
                    if (!optional.isPresent()) continue;
                    if (index > 0) {
                        --index;
                        continue;
                    }
                    return optional;
                }
            }
        }
        return Optional.empty();
    }

    protected static Optional<Vec3d> canWakeUpAt(EntityType<?> type, CollisionView world, BlockPos pos) {
        VoxelShape voxelShape = world.getBlockState(pos).getCollisionShape(world, pos);
        if (voxelShape.getMaximum(Direction.Axis.Y) > 0.4375) {
            return Optional.empty();
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        while (mutable.getY() >= 0 && pos.getY() - mutable.getY() <= 2 && world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty()) {
            mutable.setOffset(Direction.DOWN);
        }
        VoxelShape voxelShape2 = world.getBlockState(mutable).getCollisionShape(world, mutable);
        if (voxelShape2.isEmpty()) {
            return Optional.empty();
        }
        double d = (double)mutable.getY() + voxelShape2.getMaximum(Direction.Axis.Y) + 2.0E-7;
        if ((double)pos.getY() - d > 2.0) {
            return Optional.empty();
        }
        float f = type.getWidth() / 2.0f;
        Vec3d vec3d = new Vec3d((double)mutable.getX() + 0.5, d, (double)mutable.getZ() + 0.5);
        if (world.doesNotCollide(new Box(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)type.getHeight(), vec3d.z + (double)f))) {
            return Optional.of(vec3d);
        }
        return Optional.empty();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new BedBlockEntity(this.color);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            BlockPos blockPos = pos.offset(state.get(FACING));
            world.setBlockState(blockPos, (BlockState)state.with(PART, BedPart.HEAD), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighborStates(world, pos, 3);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.offset(state.get(FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
    }

    @Override
    public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
        return false;
    }
}

