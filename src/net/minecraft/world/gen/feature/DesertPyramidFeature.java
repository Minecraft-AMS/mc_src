/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class DesertPyramidFeature
extends AbstractTempleFeature<DefaultFeatureConfig> {
    public DesertPyramidFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public String getName() {
        return "Desert_Pyramid";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier() {
        return 14357617;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            DesertTempleGenerator desertTempleGenerator = new DesertTempleGenerator(this.random, x * 16, z * 16);
            this.children.add(desertTempleGenerator);
            this.setBoundingBoxFromChildren();
        }
    }
}

