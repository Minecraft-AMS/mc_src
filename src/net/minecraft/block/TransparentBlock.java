/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class TransparentBlock
extends Block {
    protected TransparentBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
        if (neighbor.getBlock() == this) {
            return true;
        }
        return super.isSideInvisible(state, neighbor, facing);
    }
}
