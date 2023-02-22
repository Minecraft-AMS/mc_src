/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem
extends Item {
    public ShearsItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient) {
            stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        Block block = state.getBlock();
        if (state.matches(BlockTags.LEAVES) || block == Blocks.COBWEB || block == Blocks.GRASS || block == Blocks.FERN || block == Blocks.DEAD_BUSH || block == Blocks.VINE || block == Blocks.TRIPWIRE || block.matches(BlockTags.WOOL)) {
            return true;
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.COBWEB || state.matches(BlockTags.LEAVES)) {
            return 15.0f;
        }
        if (block.matches(BlockTags.WOOL)) {
            return 5.0f;
        }
        return super.getMiningSpeed(stack, state);
    }
}

