/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.AbstractNetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class BasaltDeltasSurfaceBuilder
extends AbstractNetherSurfaceBuilder {
    private static final BlockState BASALT = Blocks.BASALT.getDefaultState();
    private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final ImmutableList<BlockState> SURFACE_STATES = ImmutableList.of((Object)BASALT, (Object)BLACKSTONE);
    private static final ImmutableList<BlockState> UNDER_LAVA_STATES = ImmutableList.of((Object)BASALT);

    public BasaltDeltasSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    protected ImmutableList<BlockState> getSurfaceStates() {
        return SURFACE_STATES;
    }

    @Override
    protected ImmutableList<BlockState> getUnderLavaStates() {
        return UNDER_LAVA_STATES;
    }

    @Override
    protected BlockState getLavaShoreState() {
        return GRAVEL;
    }
}

