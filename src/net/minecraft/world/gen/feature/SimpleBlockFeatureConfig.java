/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class SimpleBlockFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.TYPE_CODEC.fieldOf("to_place").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace), (App)BlockState.CODEC.listOf().fieldOf("place_on").orElse((Object)ImmutableList.of()).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeOn), (App)BlockState.CODEC.listOf().fieldOf("place_in").orElse((Object)ImmutableList.of()).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeIn), (App)BlockState.CODEC.listOf().fieldOf("place_under").orElse((Object)ImmutableList.of()).forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeUnder)).apply((Applicative)instance, SimpleBlockFeatureConfig::new));
    public final BlockStateProvider toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public SimpleBlockFeatureConfig(BlockStateProvider blockStateProvider, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder) {
        this.toPlace = blockStateProvider;
        this.placeOn = placeOn;
        this.placeIn = placeIn;
        this.placeUnder = placeUnder;
    }

    public SimpleBlockFeatureConfig(BlockStateProvider blockStateProvider) {
        this(blockStateProvider, (List<BlockState>)ImmutableList.of(), (List<BlockState>)ImmutableList.of(), (List<BlockState>)ImmutableList.of());
    }
}

