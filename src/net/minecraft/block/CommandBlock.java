/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock
extends BlockWithEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty CONDITIONAL = Properties.CONDITIONAL;

    public CommandBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(CONDITIONAL, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity();
        commandBlockBlockEntity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
        return commandBlockBlockEntity;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
        boolean bl = world.isReceivingRedstonePower(pos);
        boolean bl2 = commandBlockBlockEntity.isPowered();
        commandBlockBlockEntity.setPowered(bl);
        if (bl2 || commandBlockBlockEntity.isAuto() || commandBlockBlockEntity.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
            return;
        }
        if (bl) {
            commandBlockBlockEntity.updateConditionMet();
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        }
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity) {
            CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
            CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
            boolean bl = !ChatUtil.isEmpty(commandBlockExecutor.getCommand());
            CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
            boolean bl2 = commandBlockBlockEntity.isConditionMet();
            if (type == CommandBlockBlockEntity.Type.AUTO) {
                commandBlockBlockEntity.updateConditionMet();
                if (bl2) {
                    this.execute(state, world, pos, commandBlockExecutor, bl);
                } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
                    commandBlockExecutor.setSuccessCount(0);
                }
                if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
                    world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
                }
            } else if (type == CommandBlockBlockEntity.Type.REDSTONE) {
                if (bl2) {
                    this.execute(state, world, pos, commandBlockExecutor, bl);
                } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
                    commandBlockExecutor.setSuccessCount(0);
                }
            }
            world.updateHorizontalAdjacent(pos, this);
        }
    }

    private void execute(BlockState state, World world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand) {
        if (hasCommand) {
            executor.execute(world);
        } else {
            executor.setSuccessCount(0);
        }
        CommandBlock.executeCommandChain(world, pos, state.get(FACING));
    }

    @Override
    public int getTickRate(CollisionView world) {
        return 1;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openCommandBlockScreen((CommandBlockBlockEntity)blockEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity) {
            return ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount();
        }
        return 0;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
        CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
        if (itemStack.hasCustomName()) {
            commandBlockExecutor.setCustomName(itemStack.getName());
        }
        if (!world.isClient) {
            if (itemStack.getSubTag("BlockEntityTag") == null) {
                commandBlockExecutor.shouldTrackOutput(world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
                commandBlockBlockEntity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }
            if (commandBlockBlockEntity.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
                boolean bl = world.isReceivingRedstonePower(pos);
                commandBlockBlockEntity.setPowered(bl);
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
        builder.add(FACING, CONDITIONAL);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    private static void executeCommandChain(World world, BlockPos pos, Direction facing) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        GameRules gameRules = world.getGameRules();
        int i = gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
        while (i-- > 0) {
            CommandBlockBlockEntity commandBlockBlockEntity;
            BlockEntity blockEntity;
            mutable.setOffset(facing);
            BlockState blockState = world.getBlockState(mutable);
            Block block = blockState.getBlock();
            if (block != Blocks.CHAIN_COMMAND_BLOCK || !((blockEntity = world.getBlockEntity(mutable)) instanceof CommandBlockBlockEntity) || (commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity).getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) break;
            if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
                CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
                if (commandBlockBlockEntity.updateConditionMet()) {
                    if (!commandBlockExecutor.execute(world)) break;
                    world.updateHorizontalAdjacent(mutable, block);
                } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
                    commandBlockExecutor.setSuccessCount(0);
                }
            }
            facing = blockState.get(FACING);
        }
        if (i <= 0) {
            int j = Math.max(gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)j);
        }
    }
}
