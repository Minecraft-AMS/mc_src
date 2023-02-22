/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ScatteredOreFeature
extends Feature<OreFeatureConfig> {
    private static final int field_31515 = 7;

    ScatteredOreFeature(Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<OreFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        OreFeatureConfig oreFeatureConfig = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        int i = random.nextInt(oreFeatureConfig.size + 1);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        block0: for (int j = 0; j < i; ++j) {
            this.setPos(mutable, random, blockPos, Math.min(j, 7));
            BlockState blockState = structureWorldAccess.getBlockState(mutable);
            for (OreFeatureConfig.Target target : oreFeatureConfig.targets) {
                if (!OreFeature.shouldPlace(blockState, structureWorldAccess::getBlockState, random, oreFeatureConfig, target, mutable)) continue;
                structureWorldAccess.setBlockState(mutable, target.state, 2);
                continue block0;
            }
        }
        return true;
    }

    private void setPos(BlockPos.Mutable mutable, Random random, BlockPos origin, int spread) {
        int i = this.getSpread(random, spread);
        int j = this.getSpread(random, spread);
        int k = this.getSpread(random, spread);
        mutable.set(origin, i, j, k);
    }

    private int getSpread(Random random, int spread) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float)spread);
    }
}

