/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.Stainable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;

public class StainedGlassPaneBlock
extends PaneBlock
implements Stainable {
    private final DyeColor color;

    public StainedGlassPaneBlock(DyeColor color, Block.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.TRANSLUCENT;
    }
}

