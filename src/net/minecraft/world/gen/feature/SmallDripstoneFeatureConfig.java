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
import net.minecraft.world.gen.feature.FeatureConfig;

public class SmallDripstoneFeatureConfig
implements FeatureConfig {
    public static final Codec<SmallDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)100).fieldOf("max_placements").orElse((Object)5).forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.maxPlacements), (App)Codec.intRange((int)0, (int)20).fieldOf("empty_space_search_radius").orElse((Object)10).forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.emptySpaceSearchRadius), (App)Codec.intRange((int)0, (int)20).fieldOf("max_offset_from_origin").orElse((Object)2).forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.maxOffsetFromOrigin), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_taller_dripstone").orElse((Object)Float.valueOf(0.2f)).forGetter(smallDripstoneFeatureConfig -> Float.valueOf(smallDripstoneFeatureConfig.chanceOfTallerDripstone))).apply((Applicative)instance, SmallDripstoneFeatureConfig::new));
    public final int maxPlacements;
    public final int emptySpaceSearchRadius;
    public final int maxOffsetFromOrigin;
    public final float chanceOfTallerDripstone;

    public SmallDripstoneFeatureConfig(int maxPlacements, int emptySpaceSearchRadius, int maxOffsetFromOrigin, float chanceOfTallerDripstone) {
        this.maxPlacements = maxPlacements;
        this.emptySpaceSearchRadius = emptySpaceSearchRadius;
        this.maxOffsetFromOrigin = maxOffsetFromOrigin;
        this.chanceOfTallerDripstone = chanceOfTallerDripstone;
    }
}

