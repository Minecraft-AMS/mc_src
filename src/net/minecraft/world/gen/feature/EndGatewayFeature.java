/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EndGatewayFeature
extends Feature<EndGatewayFeatureConfig> {
    public EndGatewayFeature(Codec<EndGatewayFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<EndGatewayFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        EndGatewayFeatureConfig endGatewayFeatureConfig = context.getConfig();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
            boolean bl4;
            boolean bl = blockPos2.getX() == blockPos.getX();
            boolean bl2 = blockPos2.getY() == blockPos.getY();
            boolean bl3 = blockPos2.getZ() == blockPos.getZ();
            boolean bl5 = bl4 = Math.abs(blockPos2.getY() - blockPos.getY()) == 2;
            if (bl && bl2 && bl3) {
                BlockPos blockPos3 = blockPos2.toImmutable();
                this.setBlockState(structureWorldAccess, blockPos3, Blocks.END_GATEWAY.getDefaultState());
                endGatewayFeatureConfig.getExitPos().ifPresent(pos -> {
                    BlockEntity blockEntity = structureWorldAccess.getBlockEntity(blockPos3);
                    if (blockEntity instanceof EndGatewayBlockEntity) {
                        EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
                        endGatewayBlockEntity.setExitPortalPos((BlockPos)pos, endGatewayFeatureConfig.isExact());
                        blockEntity.markDirty();
                    }
                });
                continue;
            }
            if (bl2) {
                this.setBlockState(structureWorldAccess, blockPos2, Blocks.AIR.getDefaultState());
                continue;
            }
            if (bl4 && bl && bl3) {
                this.setBlockState(structureWorldAccess, blockPos2, Blocks.BEDROCK.getDefaultState());
                continue;
            }
            if (!bl && !bl3 || bl4) {
                this.setBlockState(structureWorldAccess, blockPos2, Blocks.AIR.getDefaultState());
                continue;
            }
            this.setBlockState(structureWorldAccess, blockPos2, Blocks.BEDROCK.getDefaultState());
        }
        return true;
    }
}

