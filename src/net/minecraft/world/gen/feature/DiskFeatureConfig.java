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
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DiskFeatureConfig
implements FeatureConfig {
    public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("state").forGetter(diskFeatureConfig -> diskFeatureConfig.state), (App)UniformIntDistribution.createValidatedCodec(0, 4, 4).fieldOf("radius").forGetter(diskFeatureConfig -> diskFeatureConfig.radius), (App)Codec.intRange((int)0, (int)4).fieldOf("half_height").forGetter(diskFeatureConfig -> diskFeatureConfig.halfHeight), (App)BlockState.CODEC.listOf().fieldOf("targets").forGetter(diskFeatureConfig -> diskFeatureConfig.targets)).apply((Applicative)instance, DiskFeatureConfig::new));
    public final BlockState state;
    public final UniformIntDistribution radius;
    public final int halfHeight;
    public final List<BlockState> targets;

    public DiskFeatureConfig(BlockState state, UniformIntDistribution radius, int halfHeight, List<BlockState> targets) {
        this.state = state;
        this.radius = radius;
        this.halfHeight = halfHeight;
        this.targets = targets;
    }
}

