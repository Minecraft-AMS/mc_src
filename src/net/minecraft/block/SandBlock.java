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
import net.minecraft.block.FallingBlock;

public class SandBlock
extends FallingBlock {
    private final int color;

    public SandBlock(int color, Block.Settings settings) {
        super(settings);
        this.color = color;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState state) {
        return this.color;
    }
}

