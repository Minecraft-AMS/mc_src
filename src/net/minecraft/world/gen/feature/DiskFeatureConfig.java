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
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public record DiskFeatureConfig(BlockState state, IntProvider radius, int halfHeight, List<BlockState> targets) implements FeatureConfig
{
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("state").forGetter(DiskFeatureConfig::state), (App)IntProvider.createValidatingCodec(0, 8).fieldOf("radius").forGetter(DiskFeatureConfig::radius), (App)Codec.intRange((int)0, (int)4).fieldOf("half_height").forGetter(DiskFeatureConfig::halfHeight), (App)BlockState.CODEC.listOf().fieldOf("targets").forGetter(DiskFeatureConfig::targets)).apply((Applicative)instance, DiskFeatureConfig::new));
}

