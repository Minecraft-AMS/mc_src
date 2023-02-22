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
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class GrowingPlantFeatureConfig
implements FeatureConfig {
    public static final Codec<GrowingPlantFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DataPool.createCodec(IntProvider.VALUE_CODEC).fieldOf("height_distribution").forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.heightDistribution), (App)Direction.CODEC.fieldOf("direction").forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.direction), (App)BlockStateProvider.TYPE_CODEC.fieldOf("body_provider").forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.bodyProvider), (App)BlockStateProvider.TYPE_CODEC.fieldOf("head_provider").forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.headProvider), (App)Codec.BOOL.fieldOf("allow_water").forGetter(growingPlantFeatureConfig -> growingPlantFeatureConfig.allowWater)).apply((Applicative)instance, GrowingPlantFeatureConfig::new));
    public final DataPool<IntProvider> heightDistribution;
    public final Direction direction;
    public final BlockStateProvider bodyProvider;
    public final BlockStateProvider headProvider;
    public final boolean allowWater;

    public GrowingPlantFeatureConfig(DataPool<IntProvider> heightDistribution, Direction direction, BlockStateProvider bodyProvider, BlockStateProvider headProvider, boolean allowWater) {
        this.heightDistribution = heightDistribution;
        this.direction = direction;
        this.bodyProvider = bodyProvider;
        this.headProvider = headProvider;
        this.allowWater = allowWater;
    }
}

