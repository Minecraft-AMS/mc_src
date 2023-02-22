/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class JungleGroundBushFeature
extends AbstractTreeFeature<DefaultFeatureConfig> {
    private final BlockState leaves;
    private final BlockState log;

    public JungleGroundBushFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, BlockState log, BlockState leaves) {
        super(configFactory, false);
        this.log = log;
        this.leaves = leaves;
    }

    @Override
    public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
        if (JungleGroundBushFeature.isNaturalDirtOrGrass(world, pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos).down())) {
            pos = pos.up();
            this.setBlockState(logPositions, world, pos, this.log, blockBox);
            for (int i = pos.getY(); i <= pos.getY() + 2; ++i) {
                int j = i - pos.getY();
                int k = 2 - j;
                for (int l = pos.getX() - k; l <= pos.getX() + k; ++l) {
                    int m = l - pos.getX();
                    for (int n = pos.getZ() - k; n <= pos.getZ() + k; ++n) {
                        BlockPos blockPos;
                        int o = n - pos.getZ();
                        if (Math.abs(m) == k && Math.abs(o) == k && random.nextInt(2) == 0 || !JungleGroundBushFeature.isAirOrLeaves(world, blockPos = new BlockPos(l, i, n))) continue;
                        this.setBlockState(logPositions, world, blockPos, this.leaves, blockBox);
                    }
                }
            }
        }
        return true;
    }
}

