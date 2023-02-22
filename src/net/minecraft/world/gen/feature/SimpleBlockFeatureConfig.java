/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleBlockFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("to_place").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace), (App)BlockState.CODEC.listOf().fieldOf("place_on").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeOn), (App)BlockState.CODEC.listOf().fieldOf("place_in").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeIn), (App)BlockState.CODEC.listOf().fieldOf("place_under").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.placeUnder)).apply((Applicative)instance, SimpleBlockFeatureConfig::new));
    public final BlockState toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public SimpleBlockFeatureConfig(BlockState toPlace, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder) {
        this.toPlace = toPlace;
        this.placeOn = placeOn;
        this.placeIn = placeIn;
        this.placeUnder = placeUnder;
    }
}

