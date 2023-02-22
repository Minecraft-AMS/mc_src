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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public class RandomFeatureEntry {
    public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(config -> config.feature), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance").forGetter(config -> Float.valueOf(config.chance))).apply((Applicative)instance, RandomFeatureEntry::new));
    public final RegistryEntry<PlacedFeature> feature;
    public final float chance;

    public RandomFeatureEntry(RegistryEntry<PlacedFeature> feature, float chance) {
        this.feature = feature;
        this.chance = chance;
    }

    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
        return this.feature.value().generateUnregistered(world, chunkGenerator, random, pos);
    }
}

