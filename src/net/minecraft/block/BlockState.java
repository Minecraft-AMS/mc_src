/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.fabric.api.block.v1.FabricBlockState
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.block.v1.FabricBlockState;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;

public class BlockState
extends AbstractBlock.AbstractBlockState
implements FabricBlockState {
    public static final Codec<BlockState> CODEC = BlockState.createCodec(Registries.BLOCK.getCodec(), Block::getDefaultState).stable();

    public BlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
        super(block, immutableMap, mapCodec);
    }

    @Override
    protected BlockState asBlockState() {
        return this;
    }
}

