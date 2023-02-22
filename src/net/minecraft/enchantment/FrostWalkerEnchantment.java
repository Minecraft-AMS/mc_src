/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostWalkerEnchantment
extends Enchantment {
    public FrostWalkerEnchantment(Enchantment.Weight weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.ARMOR_FEET, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return level * 10;
    }

    @Override
    public int getMaximumPower(int level) {
        return this.getMinimumPower(level) + 15;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public int getMaximumLevel() {
        return 2;
    }

    public static void freezeWater(LivingEntity entity, World world, BlockPos blockPos, int level) {
        if (!entity.onGround) {
            return;
        }
        BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
        float f = Math.min(16, 2 + level);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-f, -1.0, -f), blockPos.add(f, -1.0, f))) {
            BlockState blockState3;
            if (!blockPos2.isWithinDistance(entity.getPos(), (double)f)) continue;
            mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
            BlockState blockState2 = world.getBlockState(mutable);
            if (!blockState2.isAir() || (blockState3 = world.getBlockState(blockPos2)).getMaterial() != Material.WATER || blockState3.get(FluidBlock.LEVEL) != 0 || !blockState.canPlaceAt(world, blockPos2) || !world.canPlace(blockState, blockPos2, EntityContext.absent())) continue;
            world.setBlockState(blockPos2, blockState);
            world.getBlockTickScheduler().schedule(blockPos2, Blocks.FROSTED_ICE, MathHelper.nextInt(entity.getRandom(), 60, 120));
        }
    }

    @Override
    public boolean differs(Enchantment other) {
        return super.differs(other) && other != Enchantments.DEPTH_STRIDER;
    }
}
