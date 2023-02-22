/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class BushFoliagePlacer
extends BlobFoliagePlacer {
    public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> BushFoliagePlacer.createCodec(instance).apply((Applicative)instance, BushFoliagePlacer::new));

    public BushFoliagePlacer(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2, int i) {
        super(uniformIntDistribution, uniformIntDistribution2, i);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int offset, BlockBox box) {
        for (int i = offset; i >= offset - foliageHeight; --i) {
            int j = radius + treeNode.getFoliageRadius() - 1 - i;
            this.generateSquare(world, random, config, treeNode.getCenter(), j, leaves, i, treeNode.isGiantTrunk(), box);
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && random.nextInt(2) == 0;
    }
}

