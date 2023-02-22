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

public class VillageFeature
extends JigsawFeature {
    public VillageFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, 0, true, true);
    }
}

