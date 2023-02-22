/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class NetherFossilFeature
extends StructureFeature<DefaultFeatureConfig> {
    public NetherFossilFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends MarginedStructureStart<DefaultFeatureConfig> {
        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
            super(structureFeature, i, j, blockBox, k, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, DefaultFeatureConfig defaultFeatureConfig) {
            int n;
            ChunkPos chunkPos = new ChunkPos(i, j);
            int k = chunkPos.getStartX() + this.random.nextInt(16);
            int l = chunkPos.getStartZ() + this.random.nextInt(16);
            int m = chunkGenerator.getSeaLevel();
            BlockView blockView = chunkGenerator.getColumnSample(k, l);
            BlockPos.Mutable mutable = new BlockPos.Mutable(k, n, l);
            for (n = m + this.random.nextInt(chunkGenerator.getWorldHeight() - 2 - m); n > m; --n) {
                BlockState blockState = blockView.getBlockState(mutable);
                mutable.move(Direction.DOWN);
                BlockState blockState2 = blockView.getBlockState(mutable);
                if (blockState.isAir() && (blockState2.isOf(Blocks.SOUL_SAND) || blockState2.isSideSolidFullSquare(blockView, mutable, Direction.UP))) break;
            }
            if (n <= m) {
                return;
            }
            NetherFossilGenerator.addPieces(structureManager, this.children, this.random, new BlockPos(k, n, l));
            this.setBoundingBoxFromChildren();
        }
    }
}

