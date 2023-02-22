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
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.world.BlockView;

public abstract class AbstractSkullBlock
extends BlockWithEntity {
    private final SkullBlock.SkullType type;

    public AbstractSkullBlock(SkullBlock.SkullType type, Block.Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasBlockEntityBreakingRender(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new SkullBlockEntity();
    }

    @Environment(value=EnvType.CLIENT)
    public SkullBlock.SkullType getSkullType() {
        return this.type;
    }
}

