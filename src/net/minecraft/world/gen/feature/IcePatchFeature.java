/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class IcePatchFeature
extends DiskFeature {
    public IcePatchFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DiskFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkGenerator chunkGenerator = context.getGenerator();
        Random random = context.getRandom();
        DiskFeatureConfig diskFeatureConfig = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomY() + 2) {
            blockPos = blockPos.down();
        }
        if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.SNOW_BLOCK)) {
            return false;
        }
        return super.generate(new FeatureContext<DiskFeatureConfig>(context.getFeature(), structureWorldAccess, context.getGenerator(), context.getRandom(), blockPos, context.getConfig()));
    }
}

