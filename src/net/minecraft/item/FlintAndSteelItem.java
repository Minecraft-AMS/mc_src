/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlintAndSteelItem
extends Item {
    public FlintAndSteelItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        BlockPos blockPos2;
        PlayerEntity playerEntity = context.getPlayer();
        World iWorld = context.getWorld();
        if (FlintAndSteelItem.canIgnite(iWorld.getBlockState(blockPos2 = (blockPos = context.getBlockPos()).offset(context.getSide())), iWorld, blockPos2)) {
            iWorld.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            BlockState blockState = ((FireBlock)Blocks.FIRE).getStateForPosition(iWorld, blockPos2);
            iWorld.setBlockState(blockPos2, blockState, 11);
            ItemStack itemStack = context.getStack();
            if (playerEntity instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
                itemStack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            }
            return ActionResult.SUCCESS;
        }
        BlockState blockState = iWorld.getBlockState(blockPos);
        if (FlintAndSteelItem.isIgnitable(blockState)) {
            iWorld.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            iWorld.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true), 11);
            if (playerEntity != null) {
                context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static boolean isIgnitable(BlockState state) {
        return state.getBlock() == Blocks.CAMPFIRE && state.get(Properties.WATERLOGGED) == false && state.get(Properties.LIT) == false;
    }

    public static boolean canIgnite(BlockState block, IWorld world, BlockPos pos) {
        BlockState blockState = ((FireBlock)Blocks.FIRE).getStateForPosition(world, pos);
        boolean bl = false;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (world.getBlockState(pos.offset(direction)).getBlock() != Blocks.OBSIDIAN || ((NetherPortalBlock)Blocks.NETHER_PORTAL).createAreaHelper(world, pos) == null) continue;
            bl = true;
        }
        return block.isAir() && (blockState.canPlaceAt(world, pos) || bl);
    }
}

