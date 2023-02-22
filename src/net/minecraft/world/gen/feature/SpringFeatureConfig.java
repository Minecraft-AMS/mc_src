/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public static final Codec<SpringFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)FluidState.CODEC.fieldOf("state").forGetter(springFeatureConfig -> springFeatureConfig.state), (App)Codec.BOOL.fieldOf("requires_block_below").orElse((Object)true).forGetter(springFeatureConfig -> springFeatureConfig.requiresBlockBelow), (App)Codec.INT.fieldOf("rock_count").orElse((Object)4).forGetter(springFeatureConfig -> springFeatureConfig.rockCount), (App)Codec.INT.fieldOf("hole_count").orElse((Object)1).forGetter(springFeatureConfig -> springFeatureConfig.holeCount), (App)Registry.BLOCK.listOf().fieldOf("valid_blocks").xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter(springFeatureConfig -> springFeatureConfig.validBlocks)).apply((Applicative)instance, SpringFeatureConfig::new));
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public SpringFeatureConfig(FluidState state, boolean requiresBlockBelow, int rockCount, int holeCount, Set<Block> validBlocks) {
        this.state = state;
        this.requiresBlockBelow = requiresBlockBelow;
        this.rockCount = rockCount;
        this.holeCount = holeCount;
        this.validBlocks = validBlocks;
    }
}

