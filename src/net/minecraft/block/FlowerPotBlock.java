/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock
extends Block {
    private static final Map<Block, Block> CONTENT_TO_POTTED = Maps.newHashMap();
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    private final Block content;

    public FlowerPotBlock(Block content, Block.Settings settings) {
        super(settings);
        this.content = content;
        CONTENT_TO_POTTED.put(content, this);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean bl2;
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        Block block = item instanceof BlockItem ? CONTENT_TO_POTTED.getOrDefault(((BlockItem)item).getBlock(), Blocks.AIR) : Blocks.AIR;
        boolean bl = block == Blocks.AIR;
        boolean bl3 = bl2 = this.content == Blocks.AIR;
        if (bl != bl2) {
            if (bl2) {
                world.setBlockState(pos, block.getDefaultState(), 3);
                player.incrementStat(Stats.POT_FLOWER);
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
            } else {
                ItemStack itemStack2 = new ItemStack(this.content);
                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, itemStack2);
                } else if (!player.giveItemStack(itemStack2)) {
                    player.dropItem(itemStack2, false);
                }
                world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
            }
        }
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (this.content == Blocks.AIR) {
            return super.getPickStack(world, pos, state);
        }
        return new ItemStack(this.content);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.CUTOUT;
    }

    public Block getContent() {
        return this.content;
    }
}
