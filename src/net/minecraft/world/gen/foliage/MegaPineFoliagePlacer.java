/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> MegaPineFoliagePlacer.fillFoliagePlacerFields(instance).and((App)UniformIntDistribution.createValidatedCodec(0, 16, 8).fieldOf("crown_height").forGetter(megaPineFoliagePlacer -> megaPineFoliagePlacer.crownHeight)).apply((Applicative)instance, MegaPineFoliagePlacer::new));
    private final UniformIntDistribution crownHeight;

    public MegaPineFoliagePlacer(UniformIntDistribution radius, UniformIntDistribution offset, UniformIntDistribution crownHeight) {
        super(radius, offset);
        this.crownHeight = crownHeight;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int offset, BlockBox box) {
        BlockPos blockPos = treeNode.getCenter();
        int i = 0;
        for (int j = blockPos.getY() - foliageHeight + offset; j <= blockPos.getY() + offset; ++j) {
            int k = blockPos.getY() - j;
            int l = radius + treeNode.getFoliageRadius() + MathHelper.floor((float)k / (float)foliageHeight * 3.5f);
            int m = k > 0 && l == i && (j & 1) == 0 ? l + 1 : l;
            this.generateSquare(world, random, config, new BlockPos(blockPos.getX(), j, blockPos.getZ()), m, leaves, 0, treeNode.isGiantTrunk(), box);
            i = l;
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.crownHeight.getValue(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        if (dx + dz >= 7) {
            return true;
        }
        return dx * dx + dz * dz > radius * radius;
    }
}

