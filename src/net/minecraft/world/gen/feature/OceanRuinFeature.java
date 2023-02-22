/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class OceanRuinFeature
extends StructureFeature<OceanRuinFeatureConfig> {
    public OceanRuinFeature(Codec<OceanRuinFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<OceanRuinFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static final class BiomeType
    extends Enum<BiomeType>
    implements StringIdentifiable {
        public static final /* enum */ BiomeType WARM = new BiomeType("warm");
        public static final /* enum */ BiomeType COLD = new BiomeType("cold");
        public static final Codec<BiomeType> CODEC;
        private static final Map<String, BiomeType> BY_NAME;
        private final String name;
        private static final /* synthetic */ BiomeType[] field_14531;

        public static BiomeType[] values() {
            return (BiomeType[])field_14531.clone();
        }

        public static BiomeType valueOf(String string) {
            return Enum.valueOf(BiomeType.class, string);
        }

        private BiomeType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static BiomeType byName(String name) {
            return BY_NAME.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ BiomeType[] method_36760() {
            return new BiomeType[]{WARM, COLD};
        }

        static {
            field_14531 = BiomeType.method_36760();
            CODEC = StringIdentifiable.createCodec(BiomeType::values, BiomeType::byName);
            BY_NAME = Arrays.stream(BiomeType.values()).collect(Collectors.toMap(BiomeType::getName, biomeType -> biomeType));
        }
    }

    public static class Start
    extends StructureStart<OceanRuinFeatureConfig> {
        public Start(StructureFeature<OceanRuinFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
            super(structureFeature, chunkPos, i, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, OceanRuinFeatureConfig oceanRuinFeatureConfig, HeightLimitView heightLimitView) {
            BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 90, chunkPos.getStartZ());
            BlockRotation blockRotation = BlockRotation.random(this.random);
            OceanRuinGenerator.addPieces(structureManager, blockPos, blockRotation, this, this.random, oceanRuinFeatureConfig);
        }
    }
}

