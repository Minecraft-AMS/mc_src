/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class BlockPileFeatureConfig
implements FeatureConfig {
    public static final Codec<BlockPileFeatureConfig> CODEC = BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").xmap(BlockPileFeatureConfig::new, blockPileFeatureConfig -> blockPileFeatureConfig.stateProvider).codec();
    public final BlockStateProvider stateProvider;

    public BlockPileFeatureConfig(BlockStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }
}

