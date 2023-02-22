/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantFeature
extends JigsawFeature {
    private static final int STRUCTURE_START_Y = 33;

    public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> configCodec) {
        super(configCodec, 33, false, false, context -> true);
    }
}

